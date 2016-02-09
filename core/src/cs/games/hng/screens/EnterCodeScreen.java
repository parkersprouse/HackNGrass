package cs.games.hng.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import cs.games.hng.Assets;
import cs.games.hng.Main;
import cs.games.hng.levels.LevelFive;

public class EnterCodeScreen implements Screen {

	private final Main game;
	public static float curX = MainMenu.curX;
	private OrthographicCamera camera;
	private Viewport viewport;

	private Stage stage;
	private Window win;
	private TextField field;
	private Label invalidCode;

	private static final int TIME_LIMIT = 3; // The level code error will show for three seconds
	private float timerTime = 0;

	private boolean escapeDown = true;
	
	public EnterCodeScreen(final Main game) {
		this.game = game;

		camera = new OrthographicCamera();
		viewport = new FitViewport(Main.width, Main.height, camera);

		camera.position.x = curX;

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

		if (Gdx.input.isKeyPressed(Keys.ENTER) && field.getText().length() > 0)
			checkCode(field.getText());
		
		if (Gdx.input.isKeyPressed(Keys.ESCAPE) && !escapeDown) {
			MainMenu.curX = curX;
			escapeDown = true;
			game.setScreen(new MainMenu(game, true));
		}

		if (invalidCode.isVisible()) {
			timerTime += delta;
			if (timerTime >= TIME_LIMIT) {
				invalidCode.setVisible(false);
				timerTime -= TIME_LIMIT;
			}
		}
		
		if (!Gdx.input.isKeyPressed(Keys.ESCAPE))
			escapeDown = false;
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
	}

	private void checkCode(String code) {
		if (code.toLowerCase().equals(LevelFive.code))
			game.setScreen(new LevelFive(game));
		else 
			invalidCode.setVisible(true);
	}

	private void setupGUI() {
		stage = new Stage(new FitViewport(Main.width, Main.height));
		Assets.font.setScale(0.3f);

		BitmapFont styleFont = new BitmapFont(Gdx.files.internal("skin/font.fnt"), false);

		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("skin/skin.atlas"));
		Skin skin = new Skin();
		skin.addRegions(atlas);

		// Window styling
		Window.WindowStyle winStyle=new Window.WindowStyle();
		winStyle.titleFont = Assets.font;
		winStyle.titleFontColor = Color.WHITE;
		winStyle.background=new SpriteDrawable(new Sprite(new Texture("skin/tablebg.png")));

		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = styleFont;

		// Window init
		win = new Window("Code Entry", winStyle);
		win.padTop(55);
		win.setMovable(false);
		win.setBounds(Main.width/2 - 250, Main.height/2 - 250, 500, 500);

		LabelStyle invalidLabelStyle = new LabelStyle();
		invalidLabelStyle.font = new BitmapFont(Gdx.files.internal("font.fnt"), false);
		invalidLabelStyle.font.setScale(0.25f);
		invalidLabelStyle.fontColor = new Color(1, 0.5f, 0.2f, 1);

		// Text Input style
		TextFieldStyle fieldStyle = new TextFieldStyle();
		fieldStyle.font = new BitmapFont(Gdx.files.internal("skin/font.fnt"), false);
		fieldStyle.fontColor = Color.BLACK;
		fieldStyle.background = skin.getDrawable("textField");
		fieldStyle.background.setLeftWidth(10);
		fieldStyle.background.setRightWidth(10);
		fieldStyle.cursor = skin.getDrawable("textCursor");

		// Text Input init
		field = new TextField("", fieldStyle);
		field.setBounds(win.getWidth()/2 - 100, 300, 200, 45);
		//field.setMaxLength(10);
		field.setBlinkTime(0.6f);
		/*
		field.setTextFieldListener(new TextFieldListener() {
			public void keyTyped(TextField textField, char c) {

			}}
		);
		 */

		// Button styling
		TextButtonStyle style = new TextButtonStyle();
		style.font = new BitmapFont(Gdx.files.internal("skin/font.fnt"), false);
		style.up = skin.getDrawable("btn");
		style.down = skin.getDrawable("btnDown");
		style.over = skin.getDrawable("btnHover");

		// Buttons init
		TextButton enter = new TextButton("Enter", style);
		TextButton back = new TextButton("Back", style);

		enter.setBounds(175, 150, 150, 45);
		back.setBounds(175, 50, 150, 45);

		enter.addListener(new ClickListener() {public void clicked(InputEvent e, float x, float y) {
			if (field.getText().length() > 0)
				checkCode(field.getText());
		}});
		back.addListener(new ClickListener() {public void clicked(InputEvent e, float x, float y) {MainMenu.curX = curX;game.setScreen(new MainMenu(game, true));}});

		invalidCode = new Label("Level code is invalid", invalidLabelStyle);
		invalidCode.setBounds(win.getWidth()/2 - invalidLabelStyle.font.getBounds("Level code is invalid").width/2, 375, 350, 45);
		invalidCode.setVisible(false);

		win.addActor(enter);
		win.addActor(back);
		win.addActor(field);
		win.addActor(invalidCode);
		stage.addActor(win);

		Gdx.input.setInputProcessor(stage);
	}

	public void resize(int width, int height) {
		viewport.update(width, height, true);
		stage.getViewport().update(width, height, true);
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
