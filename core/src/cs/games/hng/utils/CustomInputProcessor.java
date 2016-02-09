package cs.games.hng.utils;

import com.badlogic.gdx.InputProcessor;

public class CustomInputProcessor implements InputProcessor {

    public boolean keyPressed;
    private int pressedKey;
    
    /*
     * Returns the keycode of the last pressed key
     */
    public int getPressedKey() {
    	return pressedKey;
    }
    
    @Override
    public boolean keyDown(int keycode) {
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
    	pressedKey = keycode;
    	keyPressed = true;
        return true;
    }

	@Override
	public boolean keyTyped(char character) {
		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		return true;
	}
}