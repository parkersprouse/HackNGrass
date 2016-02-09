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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import cs.games.hng.Assets;
import cs.games.hng.Controls;
import cs.games.hng.Main;
import cs.games.hng.utils.CustomInputProcessor;
import cs.games.hng.utils.PreferenceManager;

public class OptionScreen implements Screen {

	private final Main game;
	private OrthographicCamera camera;

	public static float curX = MainMenu.curX;

	private Stage stage;
	private Label volumeLabel, volumeTitle, effectLabel, effectTitle;
	private Slider volumeSlider, effectSlider;
	private Window win, controlWin;
	private TextButton back, fullscreen, controls, doneControls;

	private TextButton up, down, left, right, swing, defaults;
	private Label upLabel, downLabel, leftLabel, rightLabel, swingLabel;

	private boolean changingButton = false;
	private boolean escapeDown = true;
	
	private CustomInputProcessor input = new CustomInputProcessor();

	private enum ControlButton {
		UP,
		DOWN,
		LEFT,
		RIGHT,
		SWING
	}
	private ControlButton buttonToChange = null;

	public OptionScreen(final Main game) {
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

		if (Gdx.input.isKeyPressed(Keys.ESCAPE) && !escapeDown && !changingButton) {
			MainMenu.curX = curX;
			escapeDown = true;
			game.setScreen(new MainMenu(game, true));
			PreferenceManager.savePreferences();
		}
		
		/*if (volumeSlider.isDragging() && !Assets.bgSong.isPlaying())
			Assets.bgSong.play();
		else if (!volumeSlider.isDragging() && Assets.bgSong.isPlaying())
			Assets.bgSong.stop();*/

		if (changingButton) {
			if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
				changingButton = false;
				input.keyPressed = false;
				buttonToChange = null;
				Gdx.input.setInputProcessor(stage);
			}
			
			else if (buttonToChange == ControlButton.UP) {
				if (input.keyPressed) {
					Controls.UP = input.getPressedKey();
					changingButton = false;
					buttonToChange = null;
					input.keyPressed = false;
					up.setText(Keys.toString(Controls.UP));
					Gdx.input.setInputProcessor(stage);
				}
			}
			else if (buttonToChange == ControlButton.DOWN) {
				if (input.keyPressed) {
					Controls.DOWN = input.getPressedKey();
					changingButton = false;
					buttonToChange = null;
					input.keyPressed = false;
					down.setText(Keys.toString(Controls.DOWN));
					Gdx.input.setInputProcessor(stage);
				}
			}
			else if (buttonToChange == ControlButton.LEFT) {
				if (input.keyPressed) {
					Controls.LEFT = input.getPressedKey();
					changingButton = false;
					buttonToChange = null;
					input.keyPressed = false;
					left.setText(Keys.toString(Controls.LEFT));
					Gdx.input.setInputProcessor(stage);
				}
			}
			else if (buttonToChange == ControlButton.RIGHT) {
				if (input.keyPressed) {
					Controls.RIGHT = input.getPressedKey();
					changingButton = false;
					buttonToChange = null;
					input.keyPressed = false;
					right.setText(Keys.toString(Controls.RIGHT));
					Gdx.input.setInputProcessor(stage);
				}
			}
			else if (buttonToChange == ControlButton.SWING) {
				if (input.keyPressed) {
					Controls.SWING = input.getPressedKey();
					changingButton = false;
					buttonToChange = null;
					input.keyPressed = false;
					swing.setText(Keys.toString(Controls.SWING));
					Gdx.input.setInputProcessor(stage);
				}
			}
		}

		if (!Gdx.input.isKeyPressed(Keys.ESCAPE))
			escapeDown = true;
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
		BitmapFont styleFont = new BitmapFont(Gdx.files.internal("skin/font.fnt"), false);

		TextButtonStyle style = new TextButtonStyle();
		style.font = styleFont;
		style.up = skin.getDrawable("btn");
		style.down = skin.getDrawable("btnDown");
		style.over = skin.getDrawable("btnHover");

		Window.WindowStyle winStyle=new Window.WindowStyle();
		winStyle.titleFont = Assets.font;
		winStyle.titleFontColor = Color.WHITE;
		winStyle.background=new SpriteDrawable(new Sprite(new Texture("skin/tablebg.png")));

		SliderStyle sliderStyle = new SliderStyle();
		sliderStyle.background = skin.getDrawable("slider");
		sliderStyle.knob = skin.getDrawable("slider-knob");

		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = styleFont;
		// End Style Creation

		// Window init
		win = new Window("Options", winStyle);
		win.padTop(55);
		win.setMovable(false);
		win.setBounds(Main.width/2 - 250, Main.height/2 - 250, 500, 500);

		// "Done" button init
		back = new TextButton("Done", style);
		back.setBounds(175, 50, 150, 45);
		back.addListener(new ClickListener() {public void clicked(InputEvent e, float x, float y) {MainMenu.curX = curX;game.setScreen(new MainMenu(game, true));back.setChecked(false);PreferenceManager.savePreferences();}});

		// "Fullscreen" button init
		fullscreen = new TextButton("Fullscreen", style);
		fullscreen.setBounds(175, 150, 150, 45);
		fullscreen.addListener(new ClickListener() {public void clicked(InputEvent e, float x, float y) {
			Gdx.graphics.setDisplayMode(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), !Gdx.graphics.isFullscreen());
			fullscreen.setChecked(false);}
		});

		// "Controls" button init
		controls = new TextButton("Controls", style);
		controls.setBounds(175, 125, 150, 45);
		controls.addListener(new ClickListener() {public void clicked(InputEvent e, float x, float y) {controls.setChecked(false);win.setVisible(false);controlWin.setVisible(true);}});

		// Slider init
		volumeSlider = new Slider(0.0f, 1.0f, 0.1f, false, sliderStyle);
		volumeSlider.setValue(Assets.musicVolume);
		volumeSlider.addListener(new ChangeListener() {public void changed (ChangeEvent event, Actor actor) {
			Assets.musicVolume = volumeSlider.getValue();
			Assets.bgSong.setVolume(Assets.musicVolume);
			volumeLabel.setText((int)(volumeSlider.getValue() * 100) + "%");
		}});
		volumeSlider.setBounds(150, 325, 200, 25);
		
		effectSlider = new Slider(0.0f, 1.0f, 0.1f, false, sliderStyle);
		effectSlider.setValue(Assets.effectVolume);
		effectSlider.addListener(new ChangeListener() {public void changed (ChangeEvent event, Actor actor) {
			Assets.effectVolume = effectSlider.getValue();
			Assets.setupSounds();
			if (Assets.grassCut.isPlaying()) {
				Assets.grassCut.stop();
				Assets.grassCut.play();
			}
			else {
				Assets.grassCut.play();
			}
			effectLabel.setText((int)(effectSlider.getValue() * 100) + "%");
		}});
		effectSlider.setBounds(150, 225, 200, 25);

		volumeLabel = new Label((int)(volumeSlider.getValue() * 100) + "%", labelStyle);
		volumeLabel.setBounds(375, 325, 100, 25);
		effectLabel = new Label((int)(effectSlider.getValue() * 100) + "%", labelStyle);
		effectLabel.setBounds(375, 225, 100, 25);

		volumeTitle = new Label("Music Volume", labelStyle);
		volumeTitle.setBounds(175, 375, 100, 25);
		effectTitle = new Label("Effect Volume", labelStyle);
		effectTitle.setBounds(175, 275, 100, 25);

		//win.addActor(fullscreen);
		win.addActor(back);
		win.addActor(controls);
		win.addActor(volumeSlider);
		win.addActor(volumeLabel);
		win.addActor(volumeTitle);
		win.addActor(effectSlider);
		win.addActor(effectLabel);
		win.addActor(effectTitle);

		stage.addActor(win);

		/*
		 * Control Setup Window
		 */
		// Window init
		controlWin = new Window("Controls", winStyle);
		controlWin.padTop(55);
		controlWin.setMovable(false);
		controlWin.setBounds(Main.width/2 - 250, Main.height/2 - 250, 500, 500);
		controlWin.setVisible(false);

		doneControls = new TextButton("Save", style);
		doneControls.setBounds(controlWin.getWidth() - 225, 50, 150, 45);
		doneControls.addListener(new ClickListener() {public void clicked(InputEvent e, float x, float y) {doneControls.setChecked(false);win.setVisible(true);controlWin.setVisible(false);PreferenceManager.savePreferences();}});

		up = new TextButton(Keys.toString(Controls.UP), style);
		up.setBounds(controlWin.getWidth() - 250, 350, 150, 45);
		up.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				Gdx.input.setInputProcessor(input);
				buttonToChange = ControlButton.UP;
				changingButton = true;
			}
		});

		down = new TextButton(Keys.toString(Controls.DOWN), style);
		down.setBounds(controlWin.getWidth() - 250, 300, 150, 45);
		down.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				Gdx.input.setInputProcessor(input);
				buttonToChange = ControlButton.DOWN;
				changingButton = true;
			}
		});

		left = new TextButton(Keys.toString(Controls.LEFT), style);
		left.setBounds(controlWin.getWidth() - 250, 250, 150, 45);
		left.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				Gdx.input.setInputProcessor(input);
				buttonToChange = ControlButton.LEFT;
				changingButton = true;
			}
		});

		right = new TextButton(Keys.toString(Controls.RIGHT), style);
		right.setBounds(controlWin.getWidth() - 250, 200, 150, 45);
		right.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				Gdx.input.setInputProcessor(input);
				buttonToChange = ControlButton.RIGHT;
				changingButton = true;
			}
		});

		swing = new TextButton(Keys.toString(Controls.SWING), style);
		swing.setBounds(controlWin.getWidth() - 250, 150, 150, 45);
		swing.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				Gdx.input.setInputProcessor(input);
				buttonToChange = ControlButton.SWING;
				changingButton = true;
			}
		});
		
		defaults = new TextButton("Defaults", style);
		defaults.setBounds(75, 50, 150, 45);
		defaults.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				Controls.UP = Keys.W;
				Controls.DOWN = Keys.S;
				Controls.LEFT = Keys.A;
				Controls.RIGHT = Keys.D;
				Controls.SWING = Keys.SPACE;
				changingButton = false;
				buttonToChange = null;
				input.keyPressed = false;
				up.setText(Keys.toString(Controls.UP));
				down.setText(Keys.toString(Controls.DOWN));
				left.setText(Keys.toString(Controls.LEFT));
				right.setText(Keys.toString(Controls.RIGHT));
				swing.setText(Keys.toString(Controls.SWING));
				Gdx.input.setInputProcessor(stage);
			}
		});

		upLabel = new Label("Up", labelStyle);
		upLabel.setBounds(100, 360, 100, 25);
		downLabel = new Label("Down", labelStyle);
		downLabel.setBounds(100, 310, 100, 25);
		leftLabel = new Label("Left", labelStyle);
		leftLabel.setBounds(100, 260, 100, 25);
		rightLabel = new Label("Right", labelStyle);
		rightLabel.setBounds(100, 210, 100, 25);
		swingLabel = new Label("Swing", labelStyle);
		swingLabel.setBounds(100, 160, 100, 25);

		controlWin.addActor(doneControls);
		controlWin.addActor(up);
		controlWin.addActor(down);
		controlWin.addActor(left);
		controlWin.addActor(right);
		controlWin.addActor(swing);
		controlWin.addActor(defaults);
		controlWin.addActor(upLabel);
		controlWin.addActor(downLabel);
		controlWin.addActor(leftLabel);
		controlWin.addActor(rightLabel);
		controlWin.addActor(swingLabel);

		stage.addActor(controlWin);

		Gdx.input.setInputProcessor(stage);
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
