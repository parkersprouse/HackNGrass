package cs.games.hng.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

import cs.games.hng.Assets;
import cs.games.hng.Main;

public class LevelOne extends Level implements Screen {

	// Code that is provided at the end of the level to allow the player to jump to the next level
	public static final String code = "";
	
	// This is only a feature of the first level of a set
	private boolean intro = true;

	public LevelOne(final Main game) {
		super(game, "maps/lvl1.tmx", 1, 180, 5);
		super.setupGUI();
		super.setSnowing(false);
		lvlTitle = "Level One: Baby Steps";
	}

	public void render(float delta) {
		if (intro) {
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
			doFade(delta);
			
			hudBatch.begin();
			Assets.font.setColor(Color.WHITE);
			Assets.font.draw(hudBatch, "The First Five", Main.width/2 - (Assets.font.getBounds("The First Five").width/2), Main.height/2 + 150);
			Assets.font.setColor(1, 1, 1, opacity);
			Assets.font.draw(hudBatch, "Press [Enter]", Main.width/2 - (Assets.font.getBounds("Press [Enter]").width/2), Main.height/2 - 200);
			player.drawHUD(hudBatch);
			hudBatch.end();
			
			if (Gdx.input.isKeyPressed(Keys.ENTER) && !enterDown) {
				intro = false;
				enterDown = true;
			}
			
			if (!Gdx.input.isKeyPressed(Keys.ENTER))
				enterDown = false;
		}
		else {
			update(delta);
			draw(delta);
		}
	}

	private void update(float delta) {				
		super.updateLevel(delta);
	}

	private void draw(float delta) {				
		super.drawLevel();
	}

	public void resetLevel() {
		game.setScreen(new LevelOne(game));
	}

	public void nextLevel() {
		game.setScreen(new LevelTwo(game));
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