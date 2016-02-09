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
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import cs.games.hng.Assets;
import cs.games.hng.Main;

public class LevelSelect implements Screen {

	private final Main game;
	public static float curX = MainMenu.curX;
	private OrthographicCamera camera;

	private Stage stage;
	private Label setLabel;
	private Window set1, set2, set3, set4, set5;
	private TextButton back, previous, next;

	private boolean escapeDown = true;

	public LevelSelect(final Main game) {
		this.game = game;

		camera = new OrthographicCamera();
		camera.setToOrtho(false, Main.width, Main.height);

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

		if (Gdx.input.isKeyPressed(Keys.ESCAPE) && !escapeDown) {
			MainMenu.curX = curX;
			escapeDown = true;
			game.setScreen(new MainMenu(game, true));
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

	private void setupGUI() {
		stage = new Stage();

		Assets.font.setScale(0.3f);

		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("skin/skin.atlas"));
		Skin skin = new Skin();
		skin.addRegions(atlas);

		// Style Creation
		BitmapFont titleFont = new BitmapFont(Gdx.files.internal("font.fnt"), false);
		titleFont.setScale(0.3f);
		BitmapFont styleFont = new BitmapFont(Gdx.files.internal("skin/font.fnt"), false);

		TextButtonStyle style = new TextButtonStyle();
		style.font = styleFont;
		style.up = skin.getDrawable("btn");
		style.down = skin.getDrawable("btnDown");
		style.over = skin.getDrawable("btnHover");

		Window.WindowStyle winStyle=new Window.WindowStyle();
		winStyle.titleFont = Assets.font;
		winStyle.titleFontColor = Color.WHITE;
		winStyle.background=new SpriteDrawable(new Sprite(new Texture("skin/lsbg.png")));

		LabelStyle titleStyle = new LabelStyle();
		titleStyle.font = titleFont;
		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = styleFont;
		// End Style Creation

		// Window init
		set1 = new Window("Level Select", winStyle);
		set1.padTop(55);
		set1.setMovable(false);
		set1.setBounds(0, 0, Main.width, Main.height);
		set1.setVisible(true);
		
		set2 = new Window("Level Select", winStyle);
		set2.padTop(55);
		set2.setMovable(false);
		set2.setBounds(0, 0, Main.width, Main.height);
		set2.setVisible(false);
		
		set3 = new Window("Level Select", winStyle);
		set3.padTop(55);
		set3.setMovable(false);
		set3.setBounds(0, 0, Main.width, Main.height);
		set3.setVisible(false);
		
		set4 = new Window("Level Select", winStyle);
		set4.padTop(55);
		set4.setMovable(false);
		set4.setBounds(0, 0, Main.width, Main.height);
		set4.setVisible(false);
		
		set5 = new Window("Level Select", winStyle);
		set5.padTop(55);
		set5.setMovable(false);
		set5.setBounds(0, 0, Main.width, Main.height);
		set5.setVisible(false);

		// Level Select title label
		setLabel = new Label("Set 1", titleStyle);
		setLabel.setBounds(25, Main.height - 150, Main.width, 40);

		// "Done" button init
		back = new TextButton("Main Menu", style);
		back.setBounds(Main.width/2 - 75, 100, 150, 45);
		back.addListener(new ClickListener() {public void clicked(InputEvent e, float x, float y) {MainMenu.curX = curX;game.setScreen(new MainMenu(game, true));back.setChecked(false);}});

		// "Previous" button init
		previous = new TextButton("Previous", style);
		previous.setBounds(75, 100, 150, 45);
		previous.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				if (set2.isVisible()) {
					setLabel.setText("Set 1");
					set1.addActor(next);
					set1.addActor(back);
					set1.addActor(setLabel);
					set2.setVisible(false);
					set1.setVisible(true);
				}
				else if (set3.isVisible()) {
					setLabel.setText("Set 2");
					set2.addActor(next);
					set2.addActor(previous);
					set2.addActor(back);
					set2.addActor(setLabel);
					set3.setVisible(false);
					set2.setVisible(true);
				}
				else if (set4.isVisible()) {
					setLabel.setText("Set 3");
					set3.addActor(next);
					set3.addActor(previous);
					set3.addActor(back);
					set3.addActor(setLabel);
					set4.setVisible(false);
					set3.setVisible(true);
				}
				else if (set5.isVisible()) {
					setLabel.setText("Set 4");
					set4.addActor(next);
					set4.addActor(previous);
					set4.addActor(back);
					set4.addActor(setLabel);
					set5.setVisible(false);
					set4.setVisible(true);
				}
			}
		});

		// "Next" button init
		next = new TextButton("Next", style);
		next.setBounds(Main.width - 225, 100, 150, 45);
		next.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				if (set1.isVisible()) {
					setLabel.setText("Set 2");
					set2.addActor(next);
					set2.addActor(previous);
					set2.addActor(back);
					set2.addActor(setLabel);
					set1.setVisible(false);
					set2.setVisible(true);
				}
				else if (set2.isVisible()) {
					setLabel.setText("Set 3");
					set3.addActor(next);
					set3.addActor(previous);
					set3.addActor(back);
					set3.addActor(setLabel);
					set2.setVisible(false);
					set3.setVisible(true);
				}
				else if (set3.isVisible()) {
					setLabel.setText("Set 4");
					set4.addActor(next);
					set4.addActor(previous);
					set4.addActor(back);
					set4.addActor(setLabel);
					set3.setVisible(false);
					set4.setVisible(true);
				}
				else if (set4.isVisible()) {
					setLabel.setText("Set 5");
					set5.addActor(previous);
					set5.addActor(back);
					set5.addActor(setLabel);
					set4.setVisible(false);
					set5.setVisible(true);
				}
			}
		});

		set1.addActor(next);
		set1.addActor(back);
		set1.addActor(setLabel);
		
		stage.addActor(set1);
		stage.addActor(set2);
		stage.addActor(set3);
		stage.addActor(set4);
		stage.addActor(set5);
		
		Gdx.input.setInputProcessor(stage);
	}

	public void resize(int width, int height) {}
	public void show() {}
	public void hide() {}
	public void pause() {}
	public void resume() {}
	public void dispose() {
		game.batch.dispose();
		stage.dispose();
	}

}
