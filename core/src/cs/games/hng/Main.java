package cs.games.hng;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import cs.games.hng.screens.SplashScreen;
import cs.games.hng.utils.PreferenceManager;

public class Main extends Game {
	public SpriteBatch batch;
	public static final int width = 1024;
	public static final int height = 768;
		
	@Override
	public void create() {
		this.batch = new SpriteBatch();
		Assets.setupSounds();
		PreferenceManager.createPrefences();
		PreferenceManager.loadPreferences();
		this.setScreen(new SplashScreen(this));
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}
}
