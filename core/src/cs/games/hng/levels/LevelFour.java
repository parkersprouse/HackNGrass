package cs.games.hng.levels;

import com.badlogic.gdx.Screen;
import cs.games.hng.Main;

public class LevelFour extends Level implements Screen {

	// Code that is provided at the end of the level to allow the player to jump to the next level
	public static final String code = "";

	public LevelFour(final Main game) {
		super(game, "maps/lvl4.tmx", 1, 60, 5);
		super.setupGUI();
		lvlTitle = "Level Four: Miles of Smiles";
	}

	public void render(float delta) {
		update(delta);
		draw(delta);
	}

	private void update(float delta) {				
		super.updateLevel(delta);
	}

	private void draw(float delta) {				
		super.drawLevel();
	}

	public void resetLevel() {
		game.setScreen(new LevelFour(game));
	}

	public void nextLevel() {
		game.setScreen(new LevelFive(game));
	}

	public String getCode() {
		return code;
	}

	public void resize(int width, int height) {}
	public void show() {}
	public void hide() {}
	public void pause() {}
	public void resume() {}
	public void dispose() {
		stage.dispose();
		game.batch.dispose();
	}

}