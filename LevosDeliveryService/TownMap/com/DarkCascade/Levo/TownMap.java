package com.DarkCascade.Levo;

import java.util.LinkedList;
import java.util.Random;

import com.DarkCascade.Levo.GridLocation;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

enum Direction {
	STRAIGHT, BACK, LEFT, RIGHT
};

public class TownMap {
	Array<GridLocation> _gridLocations;
	Texture _gridLoc_unselected;
	Texture _gridLoc_selected;
	Texture _gridLoc_target;
	Texture _gridLoc_dragSelection;
	Texture _gridLoc_blank;
	Texture _gridLoc_bigRedX;
	
	GridLocation _currentDragSelection;
	LinkedList<GridLocation> _currentDragRoute;
	
	GridLocation _lastLoc;
	GridLocation _curLoc;
	GridLocation _targetCell;
	
	Direction _lastDirection;
    int _numLeftTurns;
	
	Random targetRandomizer;
	int _rowCount;
	int _colCount;
	
	boolean _hitTarget;
	
	boolean _isDragging;
	
	boolean _isExecutingRoute;
	float _sSinceLastMove;
	float _sBetweenMoves;
	boolean _waitingForBlockedCell;
	
	boolean _showingGrid;
	
	Array<GridLocation> _blockedLocations;
	float _sSinceLastRandomBlocks;
	float _sBetweenRandomBlocks;

    private Scorekeeper _scorekeeper;
    private Timekeeper _timekeeper;
    private PackageTracker _packagetracker;
	
	public void Initialize(Texture unselLoc, Texture selLoc, Texture targetLoc, Texture dragSelection, Texture blank, Texture bigRedX) {
		// set up grid locations
		_gridLoc_unselected = unselLoc;
		_gridLoc_selected = selLoc;
		_gridLoc_target = targetLoc;
		_gridLoc_dragSelection = dragSelection;
		_gridLoc_blank = blank;
		_gridLoc_bigRedX = bigRedX;
		
		_gridLocations = new Array<GridLocation>();
		int rowIndex = 0;
		int colIndex = 0;
        int cellSizeX = 24;
        int cellSizeY = 24;
        int cellSpacingX = 32;
        int cellSpacingY = 32;
		for (int y = 111; y < 480-cellSizeY; y += cellSpacingY) {
			for (int x = 116; x < 800-cellSizeX; x += cellSpacingX) {
				Sprite tempSprite = new Sprite(_gridLoc_unselected);
				tempSprite.setSize(cellSizeX, cellSizeY);
				tempSprite.setPosition(x, y);
				
				GridLocation tempGridLoc = new GridLocation();
				tempGridLoc.Initialize(tempSprite, rowIndex, colIndex);				
				_gridLocations.add(tempGridLoc);
				
				colIndex++;
			}
			if (colIndex > _colCount) _colCount = colIndex;
			colIndex = 0;
			rowIndex++;
		}	
		
		_rowCount = rowIndex;
		
		targetRandomizer = new Random();
		
		// should check to make sure first and target cells are not the same
		_curLoc = getRandomCell();
		_lastLoc = _curLoc;
		setCurrentLocTexture(_gridLoc_selected);
		
		_targetCell = getRandomCell();
		setTargetCellTexture(_gridLoc_target);
		
		_hitTarget = false;
		
		_currentDragSelection = null;
		_currentDragRoute = new LinkedList<GridLocation>();
		
		_isDragging = false;
		_isExecutingRoute = false;
		
		_sSinceLastMove = 0;
		_sBetweenMoves = 0.125f;
		_waitingForBlockedCell = false;
		
		_blockedLocations = new Array<GridLocation>();
		_sSinceLastRandomBlocks = 0;
		_sBetweenRandomBlocks = 5;
		randomizeBlockedLocations();
		
		_lastDirection = Direction.STRAIGHT;
	}
	
	public boolean get_hitTarget() {
		// Target flag resets when "get"
		if (_hitTarget) {
			_hitTarget = false;
			return true;
		}
		
		return false;
	}
	
	public String GetLastDirection() {
		switch (_lastDirection) {
		case STRAIGHT:
			return "Straight";
		case BACK:
			return "Back";
		case RIGHT:
			return "Right";
		case LEFT:
			return "Left";
		}
		
		return "None";
	}

    public int GetNumLeftTurns() {
        return _numLeftTurns;
    }
	
	private void randomizeBlockedLocations() {
		int totalBlocks = targetRandomizer.nextInt(20);
		_blockedLocations.clear();
		_sSinceLastRandomBlocks = 0;
		
		for (int curBlock = 0; curBlock <= totalBlocks; curBlock++) {
			GridLocation randomCell = getRandomCell();
			if ((randomCell != _curLoc) && (randomCell != _targetCell) && (!_blockedLocations.contains(randomCell, true)))
				_blockedLocations.add(randomCell);
		}
			
	}
	
	private GridLocation getRandomCell() {
		int randomRow = targetRandomizer.nextInt(_rowCount);
		int randomCol = targetRandomizer.nextInt(_colCount);
		
		for (GridLocation gridLoc : _gridLocations)
			if (randomRow == gridLoc.get_RowNo())
				if (randomCol == gridLoc.get_ColNo())
					return gridLoc;
		
		// if this fails (for some reason), return the first cell in the array
		return _gridLocations.toArray()[0];
	}
	
	public Array<Sprite> getSprites() {
		Array<Sprite> retSprites = new Array<Sprite>();
		for(GridLocation gridLoc : _gridLocations)
			retSprites.add(gridLoc.get_Sprite());
		
		return retSprites;
	}
	
	public void UpdateSelectedGridLoc(Vector3 touchVec) {
		int touchX = (int)touchVec.x;
		int touchY = (int)touchVec.y;
		
		// reset all to unselected
		for(GridLocation gridLoc : _gridLocations) gridLoc.set_SpriteTexture(_gridLoc_unselected);
		setTargetCellTexture(_gridLoc_target);
		
		boolean selectedNewCell = false;
		for(GridLocation gridLoc : _gridLocations) {
			if(pointInSprite(touchX, touchY, gridLoc.get_Sprite())) {
				// check if cells are adjacent
				if ((_curLoc == null) || (cellsAreAdjacent(_curLoc, gridLoc))) {
					gridLoc.set_SpriteTexture(_gridLoc_selected);
					selectedNewCell = true;
					_curLoc = gridLoc;
				}
			}
		}
		
		if (!selectedNewCell) setCurrentLocTexture(_gridLoc_selected);
		
		updateTarget();
	}
	
	private void updateTarget() {
		if (currentLocIsTarget()) {
			_hitTarget = true;
			setTargetCellTexture(_gridLoc_selected);
			_targetCell = getRandomCell();
			setTargetCellTexture(_gridLoc_target);
		}
	}
	
	public boolean TouchDown(Vector3 touchVec) {
		// start new route
		GridLocation touchedLoc = getLocationAtPoint(touchVec);
		if (touchedLoc == null) return false;
		
		// can't start a route at a non-adjacent cell to current cell
		if ((touchedLoc != _curLoc) && (!cellsAreAdjacent(touchedLoc, _curLoc))) return false;
		
		// initialize LinkedList, set currentDragSelection to cell at touchVec		
		_currentDragSelection = touchedLoc; 
		_currentDragRoute.add(touchedLoc);
		touchedLoc.set_SpriteTexture(_gridLoc_dragSelection);
		
		// set isDragging to true
		_isDragging = true;
		
		// show grid on touchDown only
		_showingGrid = true;
		
		return true;
	}
	
	public boolean TouchDragged(Vector3 touchVec) {
		// user is moving finger across the screen
		// if cell not in route is at touchVec, add new cell to route
		
		GridLocation touchedLoc = getLocationAtPoint(touchVec);
		if ((touchedLoc == null) || (_isDragging == false)) return false;
		
//		if (_currentDragRoute.contains(touchedLoc) == false) {
//			// check adjacency of new location to last location
			if ((cellsAreAdjacent(touchedLoc, _currentDragSelection)) && (!cellIsBlocked(touchedLoc))) {
				_currentDragSelection = touchedLoc;
				_currentDragRoute.add(touchedLoc);
			}
//		}
		
		return true;
	}
	
	private boolean cellIsBlocked(GridLocation loc) {
		return (_blockedLocations.contains(loc, true));			
	}
	
	public boolean TouchUp(Vector3 touchVec) {
		if (!_isDragging) return false;
		
		// user has lifted finger
		// execute route
		
		// clear out currentDragRoute and currentDragSelection
		_currentDragSelection = null;
		// _lastSelected = _currentDragRoute.getLast();
		
		//_currentDragRoute.clear();
		
		_isDragging = false;
		_isExecutingRoute = true;
		_sSinceLastMove = _sBetweenMoves;
		
		// stop showing grid on finger raise
		_showingGrid = false;
		
		return true;
	}
	
	public void Update(float msDelta) {
		// reset all locs to white if showing grid
		// else reset all to blank
		if (!_showingGrid) {
			for(GridLocation gridLoc : _gridLocations)
				gridLoc.set_SpriteTexture(_gridLoc_blank);
		} else {
			for(GridLocation gridLoc : _gridLocations) {
				gridLoc.set_SpriteTexture(_gridLoc_unselected);
			}
		}		
		
		if (_isExecutingRoute) {
			// check ms since last move
			// do not increment time if waiting on blocked cell
			if (_waitingForBlockedCell) {
				// don't do anything til the next cell in the route is unblocked
				if (cellIsBlocked(_currentDragRoute.peek()) == false)
					_waitingForBlockedCell = false;
			} else {				
				_sSinceLastMove += msDelta;
				
				if (_sSinceLastMove >= _sBetweenMoves) {
					// if higher than limit, move to next cell in route
					if (cellIsBlocked(_currentDragRoute.peek())) {
						_waitingForBlockedCell = true;
					} else {
						_lastDirection = getNextDirection(_lastLoc, _curLoc, _currentDragRoute.peek());

                        // if the next direction is LEFT, keep track of it
                        if (_lastDirection == Direction.LEFT)
                            _numLeftTurns++;

						_lastLoc = _curLoc;						
						_curLoc = _currentDragRoute.pop();
						
						_sSinceLastMove = 0;
						// if route is empty, stop executing route
						if (_currentDragRoute.isEmpty()) {
							_isExecutingRoute = false;
						}
					}		
				}
			}
		} else if (_isDragging) {
			// set all cells in route to blue
			for(GridLocation gridLoc : _currentDragRoute)
				gridLoc.set_SpriteTexture(_gridLoc_dragSelection);
		}
		
		// update blocked locations
		_sSinceLastRandomBlocks += msDelta;
		if (_sSinceLastRandomBlocks > _sBetweenRandomBlocks)
			randomizeBlockedLocations();
		
		// show blocked locations
		for (GridLocation blockedLoc : _blockedLocations)
			blockedLoc.set_SpriteTexture(_gridLoc_bigRedX);
		
		// set target to red
		_targetCell.set_SpriteTexture(_gridLoc_target);
		
		// always show current location cell as green
		_curLoc.set_SpriteTexture(_gridLoc_selected);
		
		// check if current selection is on target
		if (!_isDragging) updateTarget();
	}
	
	public void Draw(SpriteBatch sprBatch) {		
		// draw all
		for (Sprite spr : getSprites())
			spr.draw(sprBatch);
			
	}
	
	public String GetCurrentRowCol() {
		return _curLoc.get_RowNo() + "," + _curLoc.get_ColNo();
	}
	
	private final boolean pointInSprite(int ptX, int ptY, Sprite spr) {
		int sprLeftX = (int)spr.getX();
		int sprRightX = (int)(spr.getX() + spr.getWidth());
		int sprBottomY = (int)spr.getY();
		int sprTopY = (int)(spr.getY() + spr.getHeight());
		
		boolean ptInSpr = true;
		
		if (ptX < sprLeftX) ptInSpr = false;
		if (ptX > sprRightX) ptInSpr = false;
		if (ptY < sprBottomY) ptInSpr = false;
		if (ptY > sprTopY) ptInSpr = false;
		
		return ptInSpr;
	}
	
	private GridLocation getLocationAtPoint(Vector3 vec) {
		GridLocation gridLocToReturn = null;
		
		for (GridLocation gridLoc : _gridLocations) {
			if (pointInSprite((int)vec.x, (int)vec.y, gridLoc.get_Sprite())) {
				gridLocToReturn = gridLoc;
			}
		}
		
		return gridLocToReturn;
	}
	
	private final boolean cellsAreAdjacent(GridLocation g1, GridLocation g2) {
		// no diagonals means colNo must be within one or rowNo must be within one, not both
		int g1c = g1.get_ColNo();
		int g1r = g1.get_RowNo();
		
		int g2c = g2.get_ColNo();
		int g2r = g2.get_RowNo();
		
		// if cells are identical, return false
		if ((g1r == g2r) && (g1c == g2c)) return false;
		
		// if ((g1c + 1 == g2c), (g1c == g2c), or (g1c - 1 == g2c)) && (g1r == g2r), cells are adjacent
		if (g1r == g2r)
			if ((g1c - 1 == g2c) || (g1c == g2c) || (g1c + 1 == g2c)) return true;
		
		// if ((g1r - 1 == g2r), (g1r == g2r), or (g1r + 1 == g2r)) && (g1c == g2c), cells are adjacent
		if (g1c == g2c)
			if ((g1r - 1 == g2r) || (g1r == g2r) || (g1r + 1 == g2r)) return true;
		
		return false;
	}
	
	private void setCurrentLocTexture(Texture texToSet) {
		for(GridLocation gridLoc : _gridLocations) {
			if (gridLoc == _curLoc) gridLoc.set_SpriteTexture(_gridLoc_selected);
		}
	}
	
	private void setTargetCellTexture(Texture texToSet) {
		for(GridLocation gridLoc : _gridLocations) {
			if (gridLoc == _targetCell) gridLoc.set_SpriteTexture(_gridLoc_target);
		}
	}
	
	private boolean currentLocIsTarget()
	{
		int lsR = _curLoc.get_RowNo();
		int lsC = _curLoc.get_ColNo();
		
		int tR = _targetCell.get_RowNo();
		int tC = _targetCell.get_ColNo();
		
		return ((lsR == tR) && (lsC == tC));
	}
	
	private final Direction getNextDirection(GridLocation last, GridLocation current, GridLocation next) {
		// if any location is invalid, return straight
		if ((last == null) || (current == null) || (next == null)) return Direction.STRAIGHT;
		
		// if for some reason the last and current locations match, just return straight
		if (last == current) return Direction.STRAIGHT;
		
		if (last.get_RowNo() + 1 == current.get_RowNo()) {
			if (current.get_RowNo() + 1 == next.get_RowNo()) return Direction.STRAIGHT;
			else if (current.get_RowNo() - 1 == next.get_RowNo()) return Direction.BACK;
			else if (current.get_ColNo() + 1 == next.get_ColNo()) return Direction.LEFT;
			else if (current.get_ColNo() - 1 == next.get_ColNo()) return Direction.RIGHT;
		} else if (last.get_RowNo() - 1 == current.get_RowNo()) {			
			if (current.get_RowNo() + 1 == next.get_RowNo()) return Direction.BACK;
			else if (current.get_RowNo() - 1 == next.get_RowNo()) return Direction.STRAIGHT;
			else if (current.get_ColNo() + 1 == next.get_ColNo()) return Direction.RIGHT;
			else if (current.get_ColNo() - 1 == next.get_ColNo()) return Direction.LEFT;
		} else if (last.get_ColNo() + 1 == current.get_ColNo()) {
			if (current.get_RowNo() + 1 == next.get_RowNo()) return Direction.RIGHT;
			else if (current.get_RowNo() - 1 == next.get_RowNo()) return Direction.LEFT;
			else if (current.get_ColNo() + 1 == next.get_ColNo()) return Direction.STRAIGHT;
			else if (current.get_ColNo() - 1 == next.get_ColNo()) return Direction.BACK;
		} else if (last.get_ColNo() - 1 == current.get_ColNo()) {
			if (current.get_RowNo() + 1 == next.get_RowNo()) return Direction.LEFT;
			else if (current.get_RowNo() - 1 == next.get_RowNo()) return Direction.RIGHT;
			else if (current.get_ColNo() + 1 == next.get_ColNo()) return Direction.BACK;
			else if (current.get_ColNo() - 1 == next.get_ColNo()) return Direction.STRAIGHT;
		}
		
		return Direction.STRAIGHT;
	}
}
