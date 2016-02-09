package cs.games.hng.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import cs.games.hng.Assets;
import cs.games.hng.Main;

public class SplashScreen implements Screen{

	private final Main game;
	private OrthographicCamera camera;
	private Viewport viewport;

	private SpriteBatch batch;
	private float blackAlpha;

	private int timeSinceJoin = (int)((TimeUtils.nanoTime()/1000)/1000);
	private int timeUntilStart = 1000;

	private int timeSinceReveal;
	private int timeUntilSwitch = 4500;

	private boolean fading = true;

	public SplashScreen(final Main game) {
		this.game = game;
		camera = new OrthographicCamera();
		viewport = new FitViewport(Main.width, Main.height, camera);
		
		batch = new SpriteBatch();
		blackAlpha = 1;
		Assets.black.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		Assets.splashSound.setVolume(Assets.effectVolume);
		Assets.splashSound.play();
	}

	public void render(float delta) {		
		if (Gdx.input.isKeyPressed(Keys.ENTER) || Gdx.input.isKeyPressed(Keys.ESCAPE) || Gdx.input.isKeyPressed(Keys.SPACE) || Gdx.input.isTouched()) {
			game.setScreen(new MainMenu(game, true));
			Assets.splashSound.stop();
			Assets.splashSound.dispose();
		}
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(viewport.getCamera().combined);
		batch.begin();
		batch.draw(Assets.csLogo, 0, 0);
		Assets.black.draw(batch, blackAlpha);
		batch.end();

		if (fading) {
			if ((int)((TimeUtils.nanoTime()/1000)/1000) - timeSinceJoin > timeUntilStart) {
				if (blackAlpha > 0)
					blackAlpha -= 0.01f;
				if (blackAlpha <= 0) {
					blackAlpha = 0;
					fading = false;
					timeSinceReveal = (int)((TimeUtils.nanoTime()/1000)/1000);
				}
			}
		}
		else {
			if ((int)((TimeUtils.nanoTime()/1000)/1000) - timeSinceReveal > timeUntilSwitch) {
				if (blackAlpha < 1)
					blackAlpha += 0.01f;
				if (blackAlpha >= 1) {
					game.setScreen(new MainMenu(game));
					Assets.splashSound.dispose();
				}
			}
		}

	}

	public void resize(int width, int height) {
		viewport.update(width, height, true);
		Assets.black.setBounds(0, 0, width, height);
	}
	public void show() {}
	public void hide() {}
	public void pause() {}
	public void resume() {}
	public void dispose() {
		game.batch.dispose();
	}

}
