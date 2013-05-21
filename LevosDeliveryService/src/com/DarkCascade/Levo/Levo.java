package com.DarkCascade.Levo;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

public class Levo implements ApplicationListener {
	private Texture _texArrow;
	private Sprite _spArrow;
	
	private TownMap _townMap;
	private int _score;
	private float _timeRemaining;

	private OrthographicCamera _camera;
	private SpriteBatch _sprBatch;
	
	private BitmapFont _infoFont;

	private final int NANOTIME_ONESECOND = 1000000000;
	
	private final int SPRITE_ROTATION_UP = 180;
	private final int SPRITE_ROTATION_DOWN = 0;
	private final int SPRITE_ROTATION_LEFT = 90;
	private final int SPRITE_ROTATION_RIGHT = 270;
	
	@Override
	public void create() {		
		Texture circleSquare = new Texture(Gdx.files.internal("circle-square.png"));
		Texture circleSquare_selected = new Texture(Gdx.files.internal("circle-square_selected.png"));
		Texture circleSquare_target = new Texture(Gdx.files.internal("circle-square_target.png"));
		Texture circleSquare_dragselection = new Texture(Gdx.files.internal("circle-square_dragselection.png"));
		Texture circleSquare_blank = new Texture(Gdx.files.internal("blank.png"));
		Texture circleSquare_bigRedX = new Texture(Gdx.files.internal("big_red_x.png"));
		
		_texArrow = new Texture(Gdx.files.internal("arrow.png"));
		
		_spArrow = new Sprite(_texArrow);
		_spArrow.setSize(32, 32);
		_spArrow.setPosition(400, 240);
		_spArrow.setRotation(SPRITE_ROTATION_RIGHT);
		
		// initialize the camera
		_camera = new OrthographicCamera();
		_camera.setToOrtho(true, 800, 480);
		
		// initialize the spriteBatch
		_sprBatch = new SpriteBatch();
		
		_townMap = new TownMap();
		_townMap.Initialize(circleSquare, circleSquare_selected, circleSquare_target, circleSquare_dragselection, circleSquare_blank, circleSquare_bigRedX);

        Texture texFont = new Texture(Gdx.files.internal("fonts/arial.png"));
        texFont.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        _infoFont = new BitmapFont(Gdx.files.internal("fonts/arial.fnt"), new TextureRegion(texFont), true);
		// _infoFont = new BitmapFont(true);
		
		_score = 0;
		_timeRemaining = 30;
		
		InputMultiplexer im = new InputMultiplexer();
		im.addProcessor(new TownMapInputProcessor(_townMap, _camera));
		im.addProcessor(new LevoInputProcessor());
		Gdx.input.setInputProcessor(im);
	}

	@Override
	public void dispose() {
		_sprBatch.dispose();
	}

	@Override
	public void render() {	
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		Gdx.gl.glDisable(GL10.GL_CULL_FACE);
		
		float sElapsed = Gdx.graphics.getDeltaTime();
		
		_camera.update();	
		_townMap.Update(sElapsed);
		
		_sprBatch.setProjectionMatrix(_camera.combined);
		_sprBatch.begin();
		
		if (_timeRemaining > 0)
			_timeRemaining -= sElapsed;
		
		_townMap.Draw(_sprBatch);
		
		if (_townMap.get_hitTarget()) { _score++; _timeRemaining += 5; }		
		_infoFont.draw(_sprBatch, "Score: " + _score, 10, 20);

        // report score loss due to left turns
        _infoFont.setColor(Color.RED);
        _infoFont.draw(_sprBatch, "(-" + String.valueOf(_townMap.GetNumLeftTurns()) + ")", 110, 20);
        _infoFont.setColor(Color.WHITE);

		_infoFont.draw(_sprBatch, "Time: " + Math.round(_timeRemaining), 10, 40);
		_infoFont.draw(_sprBatch, "Direction: " + _townMap.GetLastDirection(), 10, 60);

        // let 'em know how many left turns they made
        _infoFont.draw(_sprBatch, "Left Turns: ", 10, 80);
        _infoFont.setColor(Color.RED);
        _infoFont.draw(_sprBatch, String.valueOf(_townMap.GetNumLeftTurns()), 130, 80);
        _infoFont.setColor(Color.WHITE);
		
		_sprBatch.end();
				
		if(Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			if (resetPressed(touchPos.x, touchPos.y)) reset();
//			if (_timeRemaining > 0)
//				_townMap.UpdateSelectedGridLoc(touchPos);
		}
	}		

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
	
	private boolean resetPressed(float x, float y) {
		if ((x > 0) && (x < 20) && (y > 0) && (y < 20)) return true;
		
		return false;
	}
	
	private void reset() {
		_timeRemaining = 30;
		_score = 0;
	}
	

}
