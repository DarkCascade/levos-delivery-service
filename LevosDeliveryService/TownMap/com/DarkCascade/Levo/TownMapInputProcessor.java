package com.DarkCascade.Levo;


import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class TownMapInputProcessor implements InputProcessor {
	
	private OrthographicCamera _camera;
	private TownMap _townMap;
	private boolean _handlingInput;
	
	public TownMapInputProcessor(TownMap tm, OrthographicCamera c) {
		super();
		_camera = c;
		_townMap = tm;
		_handlingInput = true;
	}
	
	public void enable() {
		_handlingInput = true;
	}
	
	public void disable() {
		_handlingInput = false;
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		// finger at index pointer has touched the screen at (x,y)
		Vector3 touchVec = new Vector3();
		touchVec.set(x, y, 0);
		_camera.unproject(touchVec);
		return _townMap.TouchDown(touchVec);
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		// finger at index pointer has lifted off the screen at (x,y)
		Vector3 touchVec = new Vector3();
		touchVec.set(x, y, 0);
		_camera.unproject(touchVec);
		return _townMap.TouchUp(touchVec);
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		// finger at index pointer is being dragged across the screen; currently at (x, y)
		Vector3 touchVec = new Vector3();
		touchVec.set(x, y, 0);
		_camera.unproject(touchVec);
		return _townMap.TouchDragged(touchVec);
	}

	@Override
	public boolean touchMoved(int x, int y) {
		// Mouse movement with no button held down
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// Mouse scroll wheel up/down
		return false;
	}

}
