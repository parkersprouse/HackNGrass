package cs.games.hng.screens;

//import org.joda.time.DateTime;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.TimeUtils;

import cs.games.hng.Assets;
import cs.games.hng.Main;
import cs.games.hng.levels.LevelOne;
import cs.games.hng.levels.Tutorial;
import cs.games.hng.utils.PreferenceManager;

public class MainMenu implements Screen {
	private final Main game;
	private OrthographicCamera camera;
	//private Viewport viewport;
	private SpriteBatch logoBatch = new SpriteBatch();

	public static float curX = Main.width/2;

	// For Fading
	private int timeSinceJoin = (int)((TimeUtils.nanoTime()/1000)/1000);
	private int timeUntilStart = 1000;
	private boolean faded = false;
	private float blackAlpha = 1;
	// End Fading

	// Joda Time object used to check if we're in a holiday season or not
	//private DateTime dt = new DateTime();

	private Stage stage;

	public MainMenu(final Main game, boolean faded) {
		this(game);
		this.faded = faded;
		if (faded)
			blackAlpha = 0;
		else
			blackAlpha = 1;
	}

	public MainMenu(final Main game) {
		this.game = game;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Main.width, Main.height);
		camera.position.x = curX;
		
		//viewport = new FitViewport(Main.width, Main.height, camera);

		Assets.black.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		if (!Assets.bgSong.isPlaying()) {
			Assets.bgSong.setLooping(true);
			Assets.bgSong.setVolume(Assets.musicVolume);
			Assets.bgSong.play();
		}

		setupGUI();		
	}

	public void render(float delta) {		
		update(delta);
		draw(delta);
	}

	private void update(float delta) {		
		camera.translate(1, 0);
		if (camera.position.x > camera.viewportWidth)
			camera.position.x = camera.viewportWidth/2;
		curX = camera.position.x;		
	}

	private void draw(float delta) {
		camera.update();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		game.batch.draw(Assets.mainBGlong, 0, 0);
		game.batch.end();

		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1/30f));
		stage.draw();

		logoBatch.begin();
		logoBatch.draw(Assets.logo, (Main.width/2) - (Assets.logo.getWidth()/2), 450);
		if (blackAlpha > 0)
			Assets.black.draw(logoBatch, blackAlpha);
		logoBatch.end();

		if (!faded) {
			if ((int)((TimeUtils.nanoTime()/1000)/1000) - timeSinceJoin > timeUntilStart) {
				if (blackAlpha > 0)
					blackAlpha -= 0.01f;
				if (blackAlpha <= 0) {
					blackAlpha = 0;
					faded = true;
				}
			}
		}
	}

	private void setupGUI() {
		//stage = new Stage(new FitViewport(Main.width, Main.height));
		stage = new Stage();

		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("skin/skin.atlas"));
		Skin skin = new Skin();

		TextButtonStyle style = new TextButtonStyle();
		skin.addRegions(atlas);

		style.font = new BitmapFont(Gdx.files.internal("skin/font.fnt"), false);
		style.up = skin.getDrawable("btn");
		style.down = skin.getDrawable("btnDown");
		style.over = skin.getDrawable("btnHover");

		TextButton tutorial = new TextButton("Tutorial", style);
		TextButton start = new TextButton("Start", style);
		TextButton options = new TextButton("Options", style);
		TextButton quit = new TextButton("Quit", style);
		TextButton levelSelect = new TextButton("Levels", style);

		// This button will only show in seasonal times (Halloween, Christmas) and will bring you to the special set of levels
		//TextButton seasonalLevels = new TextButton("Seasonal", style);

		tutorial.setBounds(Main.width/2 - 75, 350, 150, 45);
		start.setBounds(Main.width/2 - 75, 275, 150, 45);
		levelSelect.setBounds(Main.width/2 - 75, 200, 150, 45);
		options.setBounds(Main.width/2 - 75, 125, 150, 45);
		quit.setBounds(Main.width/2 - 75, 50, 150, 45);
		//seasonalLevels.setBounds(Main.width - 170, 20, 150, 45);

		tutorial.addListener(new ClickListener() {public void clicked(InputEvent e, float x, float y) {Assets.bgSong.stop();Assets.tutorialStart.play();game.setScreen(new Tutorial(game));}});
		start.addListener(new ClickListener() {public void clicked(InputEvent e, float x, float y) {Assets.bgSong.stop();Assets.gameStart.play();game.setScreen(new LevelOne(game));}});
		options.addListener(new ClickListener() {public void clicked(InputEvent e, float x, float y) {OptionScreen.curX = curX;game.setScreen(new OptionScreen(game));}});
		quit.addListener(new ClickListener() {public void clicked(InputEvent e, float x, float y) {PreferenceManager.savePreferences();Gdx.app.exit();}});
		levelSelect.addListener(new ClickListener() {public void clicked(InputEvent e, float x, float y) {LevelSelect.curX = curX;game.setScreen(new LevelSelect(game));}});
		/*seasonalLevels.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				// when it's Halloween
				if (dt.getMonthOfYear() == 10) {
					Gdx.app.exit();
				}
				// when it's Christmas
				else if (dt.getMonthOfYear() == 12) {
					Gdx.app.exit();
				}
			}
		});*/

		stage.addActor(tutorial);
		stage.addActor(start);
		stage.addActor(options);
		stage.addActor(quit);
		stage.addActor(levelSelect);
		//stage.addActor(seasonalLevels);

		// If the system date is in the Halloween or Christmas season, show the special seasonal levels button
		/*if ((dt.getMonthOfYear() == 10 && dt.getDayOfMonth() > 25) || (dt.getMonthOfYear() == 12 && dt.getDayOfMonth() > 20 && dt.getDayOfMonth() <= 31))
			seasonalLevels.setVisible(true);
		else
			seasonalLevels.setVisible(false);
		 */

		Gdx.input.setInputProcessor(stage);
	}

	public void resize(int width, int height) {
		//viewport.update(width, height, true);
		//stage.getViewport().update(width, height, true);
	}
	public void show() {}
	public void hide() {}
	public void pause() {}
	public void resume() {}
	public void dispose() {
		stage.dispose();
		game.batch.dispose();
	}

}
