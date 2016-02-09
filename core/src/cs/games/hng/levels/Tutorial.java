package cs.games.hng.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.Actor;

import cs.games.hng.Assets;
import cs.games.hng.Controls;
import cs.games.hng.Main;
import cs.games.hng.PlayerTutorial;
import cs.games.hng.Tile;
import cs.games.hng.screens.MainMenu;
import cs.games.hng.utils.CustomInputProcessor;
import cs.games.hng.utils.MapGenerator;

public class Tutorial implements Screen {

	private Main game;
	private boolean unpausing = false;
	private boolean paused = false;
	private boolean escDown = true;
	private boolean enterDown = true;
	
	private OrthographicCamera camera;
	private SpriteBatch hudBatch;
	private Tile[][] tiles;
	private PlayerTutorial player;
	private int xSize, ySize;
	private Sprite hud = Assets.atlas.createSprite("customHud");
	private Sprite timerHud = Assets.atlas.createSprite("customHud");

	private Sprite onPlayer = new Sprite(new Texture(Gdx.files.internal("tut/onPlayer.png")));
	private Sprite onPortal = new Sprite(new Texture(Gdx.files.internal("tut/onPortal.png")));

	private boolean moveToNext = false;
	private boolean finishing = false;

	private enum ScreenState {
		BEGIN,
		INTRO,
		ONPLAYER,
		ONPORTAL,
		ONTIMER,
		ONSWINGS,
		ONSWITCHES,
		FIRSTATTACK,
		FINISHLEVEL,
		PAUSED
	}
	private ScreenState state = ScreenState.BEGIN;

	// Timer things
	protected static final int TIME_LIMIT = 60;
	protected float timerTime = 0;
	// End timer things

	// Pulsating Enter
	protected boolean alreadyFaded = false;
	protected float opacity = 1f;
	protected float millisecondsPerFrame = 0.01f;
	protected double timeSinceLastUpdate;
	// End Pulsating Enter

	// GUI things
	protected TextButton resume, menu, exit;
	protected TextButton options, back, controls, save, defaults;
	protected Stage stage;
	protected Window win, optionsWin, controlsWin;
	protected Label pauseLabel, optionsLabel, controlsLabel;
	// End GUI things

	// Volume Controls
	protected Label volumeLabel, volumeTitle;
	protected Slider volumeSlider;
	// End Volume Controls

	// Custom Buttons
	protected CustomInputProcessor input = new CustomInputProcessor();
	protected TextButton up, down, left, right, swing;
	protected Label upLabel, downLabel, leftLabel, rightLabel, swingLabel;
	protected boolean changingButton = false;
	protected enum ControlButton {
		UP,
		DOWN,
		LEFT,
		RIGHT,
		SWING
	}
	protected ControlButton buttonToChange = null;
	// End Custom Buttons

	public Tutorial(final Main game, ScreenState state) {
		this(game);
		this.state = state;
		camera.position.set(player.getPosition().x + 64, player.getPosition().y + 64, 0);
	}

	public Tutorial(final Main game) {
		this.game = game;
		camera = new OrthographicCamera();
		hudBatch = new SpriteBatch();

		Assets.font.setScale(0.3f);
		Assets.font.setColor(Color.WHITE);
		onPlayer.setBounds(0, 0, Main.width, Main.height);
		onPortal.setBounds(0, 0, Main.width, Main.height);

		setupGUI();

		xSize = 21;
		ySize = 21;
		player = new PlayerTutorial(this, (128*10), 0);

		tiles = MapGenerator.generateTutorialMap(xSize, ySize);

		hud.setBounds(0, 0, Main.width, Main.height);
		hud.setColor(0, 0, 0, 0.75f);

		timerHud.setBounds(0, Main.height - 45, Main.width, 45);
		timerHud.setColor(0, 0, 0, 1);

		player.setMapSize(xSize, ySize);

		camera.setToOrtho(false, Main.width*1.5f, Main.height*1.5f);

		Assets.bgSong.setVolume(Assets.musicVolume);
	}

	public void render(float delta) {
		update(delta);
		draw(delta);
	}

	private void update(float delta) {		
		if (changingButton) {
			if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
				changingButton = false;
				input.keyPressed = false;
				buttonToChange = null;
				escDown = true;
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

		if (!paused) {
			if (state == ScreenState.BEGIN) {
				doFade(delta);
				if (Gdx.input.isKeyPressed(Keys.ENTER) && !enterDown) {
					camera.position.set(player.getPosition().x + 64, player.getPosition().y + 64, 0);
					camera.position.y = (camera.viewportHeight/2);
					Assets.font.setColor(Color.WHITE);
					state = ScreenState.INTRO;
					enterDown = true;
				}
			}

			else if (state == ScreenState.INTRO) {
				if (Gdx.input.isKeyPressed(Keys.ENTER) && !enterDown) {
					state = ScreenState.ONPLAYER;
					enterDown = true;
				}
			}

			else if (state == ScreenState.ONPLAYER) {
				if (Gdx.input.isKeyPressed(Keys.ENTER) && !enterDown && !moveToNext) {
					moveToNext = true;
					enterDown = true;
				}

				if (moveToNext) {
					camera.translate(0, 500*delta);
					if (camera.position.y >= 2112.0) {
						camera.position.set(10*128 + 64, 16*128 + 64, 0);
						state = ScreenState.ONPORTAL;
						moveToNext = false;
					}
				}

			}

			else if (state == ScreenState.ONPORTAL) {
				if (Gdx.input.isKeyPressed(Keys.ENTER) && !enterDown) {
					enterDown = true;
					state = ScreenState.ONTIMER;
				}
			}

			else if (state == ScreenState.ONTIMER) {
				if (Gdx.input.isKeyPressed(Keys.ENTER) && !enterDown) {
					enterDown = true;
					state = ScreenState.ONSWINGS;
				}
			}

			else if (state == ScreenState.ONSWINGS) {
				if (Gdx.input.isKeyPressed(Keys.ENTER) && !enterDown && !moveToNext) {
					enterDown = true;
					state = ScreenState.ONSWITCHES;
				}
			}

			else if (state == ScreenState.ONSWITCHES) {
				if (Gdx.input.isKeyPressed(Keys.ENTER) && !enterDown && !moveToNext) {
					moveToNext = true;
					enterDown = true;
				}

				if (moveToNext) {
					camera.translate(0, -500*delta);
					if (camera.position.y <= 576.0) {
						camera.position.set(player.getPosition().x + 64, player.getPosition().y + 64, 0);
						state = ScreenState.FIRSTATTACK;
						moveToNext = false;
					}
				}
			}

			else if (state == ScreenState.FIRSTATTACK) {
				if (!player.stageOneComplete())
					player.update(tiles, 1);
				else if (player.stageOneComplete() && !player.stageTwoComplete())
					player.update(tiles, 2);
				else if (player.stageOneComplete() && player.stageTwoComplete())
					state = ScreenState.FINISHLEVEL;
			}

			else if (state == ScreenState.FINISHLEVEL) {
				if (!finishing) {
					if (Gdx.input.isKeyPressed(Keys.ENTER) && !enterDown) {
						enterDown = true;
						finishing = true;
						Assets.bgSong.play();
					}
				}
				else {
					player.update(tiles, 3);
					camera.position.set(player.getPosition().x + 64, player.getPosition().y + 64, 0);					
				}
			}

			if (Gdx.input.isKeyPressed(Keys.ESCAPE) && !escDown && finishing) {
				Assets.bgSong.pause();
				paused = true;
				escDown = true;
				unpausing = false;
			}

		}
		
		else if (paused) {
			if ((Gdx.input.isKeyPressed(Keys.ESCAPE) && !escDown && win.isVisible()) || unpausing) {
				if (win.getX() > -540) {
					win.setBounds(win.getX() - 20, 0, Main.width/2, 768);
					unpausing = true;
				}
				else {
					Assets.bgSong.play();
					paused = false;
					Assets.font.setScale(0.3f);
				}
				escDown = true;
			}
			else if ((Gdx.input.isKeyPressed(Keys.ESCAPE) && !escDown && optionsWin.isVisible())) {
				win.setVisible(true);
				optionsWin.setVisible(false);
				escDown = true;
			}
			else if ((Gdx.input.isKeyPressed(Keys.ESCAPE) && !escDown && controlsWin.isVisible())) {
				optionsWin.setVisible(true);
				controlsWin.setVisible(false);
				escDown = true;
			}

			if (volumeSlider.isDragging() && !Assets.bgSong.isPlaying())
				Assets.bgSong.play();
			else if (!volumeSlider.isDragging() && Assets.bgSong.isPlaying())
				Assets.bgSong.stop();
		}
		
		if (!Gdx.input.isKeyPressed(Keys.ESCAPE))
			escDown = false;
		if (!Gdx.input.isKeyPressed(Keys.ENTER))
			enterDown = false;

		if (camera.position.x < 0 + (camera.viewportWidth/2))
			camera.position.x = (camera.viewportWidth/2);
		if (camera.position.y < 0 + (camera.viewportHeight/2))
			camera.position.y = (camera.viewportHeight/2);
		if (camera.position.x > (128*xSize - camera.viewportWidth/2))
			camera.position.x = (128*xSize - camera.viewportWidth/2);
		if (camera.position.y > (128*ySize - camera.viewportHeight/2))
			camera.position.y = (128*ySize - camera.viewportHeight/2);
	}

	private void draw(float delta) {		
		camera.update();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (!paused) {
			if (state == ScreenState.BEGIN) {
				hudBatch.begin();
				Assets.font.setColor(Color.WHITE);
				Assets.font.draw(hudBatch, "Tutorial", Main.width/2 - (Assets.font.getBounds("Tutorial").width/2), Main.height/2 + 150);
				Assets.font.setColor(1, 1, 1, opacity);
				Assets.font.draw(hudBatch, "Press [Enter]", Main.width/2 - (Assets.font.getBounds("Press Enter").width/2), Main.height/2 - 200);
				hudBatch.end();
			}

			else if (state == ScreenState.INTRO) {
				game.batch.setProjectionMatrix(camera.combined);
				game.batch.begin();
				for(int i = 0; i < xSize; i++)
					for (int j = 0; j < ySize; j++) {
						if (!player.isGameOver())
							tiles[i][j].draw(game.batch);
						else
							tiles[i][j].drawPaused(game.batch);
					}
				if (!player.isGameOver())
					player.draw(game.batch);
				else
					player.drawPaused(game.batch);
				game.batch.end();

				hudBatch.begin();
				timerHud.draw(hudBatch);
				hud.draw(hudBatch);
				Assets.font.draw(hudBatch, "Welcome to Hack N' Grass", Main.width/2 - Assets.font.getBounds("Welcome to Hack N' Grass").width/2, Main.height - 300);
				Assets.font.draw(hudBatch, "[Enter]", Main.width - Assets.font.getBounds("[Enter]").width - 50, 100);
				hudBatch.end();
			}

			else if (state == ScreenState.ONPLAYER) {
				game.batch.setProjectionMatrix(camera.combined);
				game.batch.begin();
				for(int i = 0; i < xSize; i++)
					for (int j = 0; j < ySize; j++) {
						if (!player.isGameOver())
							tiles[i][j].draw(game.batch);
						else
							tiles[i][j].drawPaused(game.batch);
					}
				if (!player.isGameOver())
					player.draw(game.batch);
				else
					player.drawPaused(game.batch);
				game.batch.end();

				hudBatch.begin();
				if (!moveToNext) {
					timerHud.draw(hudBatch);
					onPlayer.draw(hudBatch);
					Assets.font.draw(hudBatch, "This is you.", Main.width/2 - Assets.font.getBounds("This is you.").width/2, Main.height - 300);
					Assets.font.draw(hudBatch, "[Enter]", Main.width - Assets.font.getBounds("[Enter]").width - 50, 100);
				}
				hudBatch.end();
			}

			else if (state == ScreenState.ONPORTAL) {
				game.batch.setProjectionMatrix(camera.combined);
				game.batch.begin();
				for(int i = 0; i < xSize; i++)
					for (int j = 0; j < ySize; j++) {
						if (!player.isGameOver())
							tiles[i][j].draw(game.batch);
						else
							tiles[i][j].drawPaused(game.batch);
					}
				if (!player.isGameOver())
					player.draw(game.batch);
				else
					player.drawPaused(game.batch);
				game.batch.end();

				hudBatch.begin();
				timerHud.draw(hudBatch);
				onPortal.draw(hudBatch);
				Assets.font.draw(hudBatch, "This is the portal.", Main.width/2 - Assets.font.getBounds("This is the portal.").width/2, Main.height - 200);
				Assets.font.draw(hudBatch, "Reaching the portal is your goal for each level.", Main.width/2 - Assets.font.getBounds("Reaching the portal is your goal for each level.").width/2, Main.height - (200 + Assets.font.getBounds("Reaching the portal is your goal for each level.").height + 50));
				Assets.font.draw(hudBatch, "[Enter]", Main.width - Assets.font.getBounds("[Enter]").width - 50, 100);
				hudBatch.end();
			}

			else if (state == ScreenState.ONTIMER) {
				game.batch.setProjectionMatrix(camera.combined);
				game.batch.begin();
				for(int i = 0; i < xSize; i++)
					for (int j = 0; j < ySize; j++) {
						if (!player.isGameOver())
							tiles[i][j].draw(game.batch);
						else
							tiles[i][j].drawPaused(game.batch);
					}
				if (!player.isGameOver())
					player.draw(game.batch);
				else
					player.drawPaused(game.batch);
				game.batch.end();

				hudBatch.begin();
				if (!moveToNext) {
					hud.draw(hudBatch);
					Assets.font.draw(hudBatch, "Some levels will give you a time limit.", Main.width/2 - Assets.font.getBounds("Some levels will give you a time limit.").width/2, Main.height - 200);
					Assets.font.draw(hudBatch, "The remaining time will be shown in the top left.", Main.width/2 - Assets.font.getBounds("The remaining time will be shown in the top left.").width/2, (Main.height - 200) - 50 - Assets.font.getBounds("Some levels will give you a time limit.").height);
					Assets.font.draw(hudBatch, "If you run out of time, you lose.", Main.width/2 - Assets.font.getBounds("If you run out of time, you lose.").width/2, ((Main.height - 200) - 50 - Assets.font.getBounds("Some levels will give you a time limit.").height) - 50 - Assets.font.getBounds("The remaining time will be shown in the top left.").height);
					timerHud.draw(hudBatch);
					Assets.font.draw(hudBatch, "60", 10, Gdx.graphics.getHeight() - 10);
					Assets.font.draw(hudBatch, "^", Assets.font.getBounds("60").width/2, Gdx.graphics.getHeight() - (Assets.font.getBounds("60").height + 10 + Assets.font.getBounds("^").height + 10));
					Assets.font.draw(hudBatch, "[Enter]", Main.width - Assets.font.getBounds("[Enter]").width - 50, 100);
				}
				hudBatch.end();
			}

			else if (state == ScreenState.ONSWINGS) {
				game.batch.setProjectionMatrix(camera.combined);
				game.batch.begin();
				for(int i = 0; i < xSize; i++)
					for (int j = 0; j < ySize; j++) {
						if (!player.isGameOver())
							tiles[i][j].draw(game.batch);
						else
							tiles[i][j].drawPaused(game.batch);
					}
				if (!player.isGameOver())
					player.draw(game.batch);
				else
					player.drawPaused(game.batch);
				game.batch.end();

				hudBatch.begin();
				if (!moveToNext) {					
					hud.draw(hudBatch);
					Assets.font.draw(hudBatch, "Other levels will give you a swing limit.", Main.width/2 - Assets.font.getBounds("Other levels will give you a swing limit.").width/2, Main.height - 200);
					Assets.font.draw(hudBatch, "If this is the case, the number of", Main.width/2 - Assets.font.getBounds("If this is the case, the number of").width/2, (Main.height - 200) - 50 - Assets.font.getBounds("Other levels will give you a swing limit.").height);
					Assets.font.draw(hudBatch, "remaining swings will be shown instead.", Main.width/2 - Assets.font.getBounds("remaining swings will be shown instead.").width/2, ((Main.height - 200) - 50 - Assets.font.getBounds("Other levels will give you a swing limit.").height) - 50 - Assets.font.getBounds("If this is the case, the number of").height);
					Assets.font.draw(hudBatch, "If you run out of swings, you lose.", Main.width/2 - Assets.font.getBounds("If you run out of swings, you lose.").width/2, (((Main.height - 200) - 50 - Assets.font.getBounds("Other levels will give you a swing limit.").height) - 50 - Assets.font.getBounds("If this is the case, the number of").height) - 50 - Assets.font.getBounds("If this is the case, the number of remaining swings will be shown instead.").height);
					timerHud.draw(hudBatch);
					Assets.font.draw(hudBatch, "100 swings left", 10, Gdx.graphics.getHeight() - Assets.font.getBounds("100 swings left").height + 10);
					Assets.font.draw(hudBatch, "^", Assets.font.getBounds("100 swings left").width/2 + 10, Gdx.graphics.getHeight() - (Assets.font.getBounds("100 swings left").height + 10 + Assets.font.getBounds("^").height + 10));
					Assets.font.draw(hudBatch, "[Enter]", Main.width - Assets.font.getBounds("[Enter]").width - 50, 100);
				}
				hudBatch.end();
			}

			else if (state == ScreenState.ONSWITCHES) {
				game.batch.setProjectionMatrix(camera.combined);
				game.batch.begin();
				for(int i = 0; i < xSize; i++)
					for (int j = 0; j < ySize; j++) {
						if (!player.isGameOver())
							tiles[i][j].draw(game.batch);
						else
							tiles[i][j].drawPaused(game.batch);
					}
				if (!player.isGameOver())
					player.draw(game.batch);
				else
					player.drawPaused(game.batch);
				game.batch.end();

				hudBatch.begin();
				if (!moveToNext) {					
					hud.draw(hudBatch);
					Assets.font.draw(hudBatch, "Some levels will have switches that you need to find.", Main.width/2 - Assets.font.getBounds("Some levels will have switches that you need to find.").width/2, Main.height - 200);
					Assets.font.draw(hudBatch, "A level can have switches for one of two reasons:", Main.width/2 - Assets.font.getBounds("A level can have switches for one of two reasons:").width/2, (Main.height - 250) - Assets.font.getBounds("Some levels will have switches that you need to find.").height);
					Assets.font.draw(hudBatch, "1) To reveal the path to the portal", Main.width/2 - Assets.font.getBounds("1) To reveal the path to the portal").width/2, ((Main.height - 250) - Assets.font.getBounds("Some levels will have switches that you need to find.").height) - 50 - Assets.font.getBounds("A level will have switches for one of two reasons:").height);
					Assets.font.draw(hudBatch, "2) To reveal a shortcut through the map", Main.width/2 - Assets.font.getBounds("2) To reveal a shortcut through the map").width/2, (((Main.height - 250) - Assets.font.getBounds("Some levels will have switches that you need to find.").height) - 50 - Assets.font.getBounds("A level will have switches for one of two reasons:").height) - 50 - Assets.font.getBounds("1) To reveal the path to the portal").height);
					Assets.font.draw(hudBatch, "If a level contains switches, you will be shown how", Main.width/2 - Assets.font.getBounds("If a level contains switches, you will be shown how").width/2, ((((Main.height - 250) - Assets.font.getBounds("Some levels will have switches that you need to find.").height) - 50 - Assets.font.getBounds("A level will have switches for one of two reasons:").height) - 50 - Assets.font.getBounds("1) To reveal the path to the portal").height) - 50 - Assets.font.getBounds("2) To reveal a shortcut through the map").height);
					Assets.font.draw(hudBatch, "many you have activated out of how many exist.", Main.width/2 - Assets.font.getBounds("many you have activated out of how many exist.").width/2, (((((Main.height - 250) - Assets.font.getBounds("Some levels will have switches that you need to find.").height) - 50 - Assets.font.getBounds("A level will have switches for one of two reasons:").height) - 50 - Assets.font.getBounds("1) To reveal the path to the portal").height) - 50 - Assets.font.getBounds("2) To reveal a shortcut through the map").height - 50 - Assets.font.getBounds("If a level contains switches, you will be shown how").height));
					timerHud.draw(hudBatch);
					Assets.font.draw(hudBatch, "0 / 1", Main.width - 100, Gdx.graphics.getHeight() - Assets.font.getBounds("0 / 1").height + 10);
					Assets.font.draw(hudBatch, "^", Main.width - 100 + Assets.font.getBounds("0 / 1").width/2, Gdx.graphics.getHeight() - (Assets.font.getBounds("0 / 1").height + 10 + Assets.font.getBounds("^").height + 10));
					Assets.font.draw(hudBatch, "[Enter]", Main.width - Assets.font.getBounds("[Enter]").width - 50, 100);
				}
				hudBatch.end();
			}

			else if (state == ScreenState.FIRSTATTACK) {
				game.batch.setProjectionMatrix(camera.combined);
				game.batch.begin();
				for(int i = 0; i < xSize; i++)
					for (int j = 0; j < ySize; j++) {
						if (!player.isGameOver())
							tiles[i][j].draw(game.batch);
						else
							tiles[i][j].drawPaused(game.batch);
					}
				if (!player.isGameOver())
					player.draw(game.batch);
				else
					player.drawPaused(game.batch);
				game.batch.end();

				hudBatch.begin();
				timerHud.draw(hudBatch);
				if (!player.stageOneComplete()) {
					hud.setBounds(0, 400, Main.width, 75);
					hud.draw(hudBatch);
					Assets.font.draw(hudBatch, "Press [" + Keys.toString(Controls.SWING) + "] to swing your scythe", Main.width/2 - Assets.font.getBounds("Press [" + Keys.toString(Controls.SWING) + "] to swing your scythe").width/2, 450);
				}

				else if (player.stageOneComplete() && !player.stageTwoComplete()) {
					player.drawHUD(hudBatch, 2);
					hud.setBounds(0, 190, Main.width, 200);
					hud.draw(hudBatch);

					Assets.font.draw(hudBatch, "If a snake is found while cutting,", Main.width/2 - Assets.font.getBounds("If a snake is found while cutting,").width/2, 375);
					Assets.font.draw(hudBatch, "you will be shown a random key.", Main.width/2 - Assets.font.getBounds("you will be shown a random key.").width/2, 375 - (Assets.font.getBounds("you will be shown a random key.").height + 25));

					Assets.font.draw(hudBatch, "You have a short amount of time to press the key", Main.width/2 - Assets.font.getBounds("You have a short amount of time to press the key").width/2, (375 - (Assets.font.getBounds("you will be shown a random key.").height + 25)) - (Assets.font.getBounds("You have a short amount of time to press the key").height*2));
					Assets.font.draw(hudBatch, "and kill the snake, else you will be killed", Main.width/2 - Assets.font.getBounds("and kill the snake, else you will be killed").width/2, ((375 - (Assets.font.getBounds("you will be shown a random key.").height + 25)) - (Assets.font.getBounds("You have a short amount of time to press the key").height*2)) - (Assets.font.getBounds("You have a short amount of time to press the key").height + 25));
				}

				hudBatch.end();
			}

			else if (state == ScreenState.FINISHLEVEL) {
				game.batch.setProjectionMatrix(camera.combined);
				game.batch.begin();
				for(int i = 0; i < xSize; i++)
					for (int j = 0; j < ySize; j++) {
						if (!player.isGameOver())
							tiles[i][j].draw(game.batch);
						else
							tiles[i][j].drawPaused(game.batch);
					}
				if (!player.isGameOver())
					player.draw(game.batch);
				else
					player.drawPaused(game.batch);
				game.batch.end();

				hudBatch.begin();
				timerHud.draw(hudBatch);
				if (!finishing) {
					hud.setBounds(0, Main.height/2 - 112, Main.width, 200);
					hud.draw(hudBatch);
					Assets.font.draw(hudBatch, "Now go and complete the maze", Main.width/2 - Assets.font.getBounds("Now go and complete the maze").width/2, Main.height/2 + 50);
					Assets.font.draw(hudBatch, "[Enter]", Main.width - Assets.font.getBounds("[Enter]").width - 50, Main.height/2 - 50);
				}
				player.drawHUD(hudBatch, 3);
				hudBatch.end();
			}
		}

		else if (paused) {
			game.batch.setProjectionMatrix(camera.combined);
			game.batch.begin();
			for(int i = 0; i < xSize; i++)
				for (int j = 0; j < ySize; j++)
					tiles[i][j].drawPaused(game.batch);
			player.drawPaused(game.batch);
			game.batch.end();

			hudBatch.begin();
			timerHud.draw(hudBatch);
			player.drawHUD(hudBatch, 3);
			hudBatch.end();

			if (win.getX() <= -20 && !unpausing) {
				win.setBounds(win.getX() + 20, 0, Main.width/2, Main.height);
			}

			stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1/30f));
			stage.draw();
		}
	}

	public void resetLevel() {
		game.setScreen(new Tutorial(game, ScreenState.FINISHLEVEL));
	}

	public void nextLevel() {
		game.setScreen(new MainMenu(game));
	}

	public String getCode() {
		return null;
	}


	private void doFade(float delta) {
		if (!alreadyFaded) {
			this.timeSinceLastUpdate += delta;
			if (this.timeSinceLastUpdate >= this.millisecondsPerFrame) {
				this.timeSinceLastUpdate = 0;
				opacity -= 0.015f;
				if (opacity < 0.015f)
					alreadyFaded = true;
			}
		}
		else if (alreadyFaded) {
			this.timeSinceLastUpdate += delta;
			if (this.timeSinceLastUpdate >= this.millisecondsPerFrame) {
				this.timeSinceLastUpdate = 0;
				opacity += 0.015f;
				if (opacity > 1 - 0.015f)
					alreadyFaded = false;
			}
		}
	}

	// Creates the pause menu GUI
	private void setupGUI() {
		stage = new Stage();
		Skin skin = new Skin();

		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("skin/skin.atlas"));
		final TextButtonStyle style = new TextButtonStyle();
		skin.addRegions(atlas);

		style.font = new BitmapFont(Gdx.files.internal("skin/font.fnt"), false);
		style.up = skin.getDrawable("btn");
		style.down = skin.getDrawable("btnDown");
		style.over = skin.getDrawable("btnHover");

		SliderStyle sliderStyle = new SliderStyle();
		sliderStyle.background = skin.getDrawable("slider");
		sliderStyle.knob = skin.getDrawable("slider-knob");

		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = new BitmapFont(Gdx.files.internal("font.fnt"), false);
		labelStyle.font.setScale(0.75f);

		Window.WindowStyle winStyle=new Window.WindowStyle();
		winStyle.titleFont = style.font;
		winStyle.titleFontColor = Color.WHITE;
		winStyle.background=new SpriteDrawable(new Sprite(new Texture("skin/pausebg.png")));

		LabelStyle controlLabelStyle = new LabelStyle();
		controlLabelStyle.font = new BitmapFont(Gdx.files.internal("font.fnt"), false);
		controlLabelStyle.font.setScale(0.3f);

		win = new Window("", winStyle);
		win.setMovable(false);
		win.setKeepWithinStage(false);
		optionsWin = new Window("", winStyle);
		optionsWin.setMovable(false);
		optionsWin.setVisible(false);
		controlsWin = new Window("", winStyle);
		controlsWin.setMovable(false);
		controlsWin.setVisible(false);

		// Slider init
		volumeSlider = new Slider(0.0f, 1.0f, 0.1f, false, sliderStyle);
		volumeSlider.setValue(Assets.musicVolume);
		volumeSlider.addListener(new ChangeListener() {public void changed (ChangeEvent event, Actor actor) {
			Assets.musicVolume = volumeSlider.getValue();
			Assets.bgSong.setVolume(Assets.musicVolume);
			volumeLabel.setText((int)(volumeSlider.getValue() * 100) + "%");
		}});
		volumeSlider.setBounds(optionsWin.getWidth()/2 + 50, 375, 200, 25);

		volumeLabel = new Label((int)(volumeSlider.getValue() * 100) + "%", controlLabelStyle);
		volumeLabel.setBounds(volumeSlider.getX() + volumeSlider.getWidth() + 25, 375, 350, 25);

		volumeTitle = new Label("Music Volume", controlLabelStyle);
		volumeTitle.setBounds(optionsWin.getWidth(), 450, 100, 25);

		// Pause Menu
		resume = new TextButton("Resume", style);
		resume.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				if (paused) unpausing = true;
			}
		});

		options = new TextButton("Options", style);
		options.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				if (paused) {
					win.setVisible(false);
					optionsWin.setVisible(true);
				}
			}
		});

		menu = new TextButton("Main Menu", style);
		menu.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				if (paused) {
					Assets.bgSong.stop();
					game.setScreen(new MainMenu(game, true));
				}
			}
		});

		exit = new TextButton("Desktop", style);
		exit.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				if (paused) {
					Gdx.app.exit();
				}
			}
		});

		// Options Menu
		back = new TextButton("Back", style);
		back.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				if (paused) {
					win.setVisible(true);
					optionsWin.setVisible(false);
				}
			}
		});

		controls = new TextButton("Controls", style);
		controls.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				if (paused) {
					optionsWin.setVisible(false);
					controlsWin.setVisible(true);
				}
			}
		});

		// Controls Menu
		up = new TextButton(Keys.toString(Controls.UP), style);
		up.setBounds(controlsWin.getWidth() - 250, 350, 150, 45);
		up.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				Gdx.input.setInputProcessor(input);
				buttonToChange = ControlButton.UP;
				changingButton = true;
			}
		});

		down = new TextButton(Keys.toString(Controls.DOWN), style);
		down.setBounds(controlsWin.getWidth() - 250, 300, 150, 45);
		down.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				Gdx.input.setInputProcessor(input);
				buttonToChange = ControlButton.DOWN;
				changingButton = true;
			}
		});

		left = new TextButton(Keys.toString(Controls.LEFT), style);
		left.setBounds(controlsWin.getWidth() - 250, 250, 150, 45);
		left.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				Gdx.input.setInputProcessor(input);
				buttonToChange = ControlButton.LEFT;
				changingButton = true;
			}
		});

		right = new TextButton(Keys.toString(Controls.RIGHT), style);
		right.setBounds(controlsWin.getWidth() - 250, 200, 150, 45);
		right.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				Gdx.input.setInputProcessor(input);
				buttonToChange = ControlButton.RIGHT;
				changingButton = true;
			}
		});

		swing = new TextButton(Keys.toString(Controls.SWING), style);
		swing.setBounds(controlsWin.getWidth() - 250, 150, 150, 45);
		swing.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				Gdx.input.setInputProcessor(input);
				buttonToChange = ControlButton.SWING;
				changingButton = true;
			}
		});

		save = new TextButton("Save", style);
		save.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				if (paused) {
					optionsWin.setVisible(true);
					controlsWin.setVisible(false);
				}
			}
		});

		defaults = new TextButton("Defaults", style);
		defaults.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				if (paused) {
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
			}
		});

		upLabel = new Label("Up", controlLabelStyle);
		upLabel.setBounds(controlsWin.getX() + 125, 485, 100, 25);
		downLabel = new Label("Down", controlLabelStyle);
		downLabel.setBounds(controlsWin.getX() + 125, 435, 100, 25);
		leftLabel = new Label("Left", controlLabelStyle);
		leftLabel.setBounds(controlsWin.getX() + 125, 385, 100, 25);
		rightLabel = new Label("Right", controlLabelStyle);
		rightLabel.setBounds(controlsWin.getX() + 125, 335, 100, 25);
		swingLabel = new Label("Swing", controlLabelStyle);
		swingLabel.setBounds(controlsWin.getX() + 125, 285, 100, 25);

		pauseLabel = new Label("PAUSED", labelStyle);
		optionsLabel = new Label("OPTIONS", labelStyle);
		controlsLabel = new Label("CONTROLS", labelStyle);

		win.setBounds(-540, 0, Main.width/2, 768);
		optionsWin.setBounds(0, 0, Main.width/2, 768);
		controlsWin.setBounds(0, 0, Main.width/2, 768);

		resume.setBounds(win.getWidth()/2 - resume.getWidth()/2, 375, 150, 45);
		options.setBounds(win.getWidth()/2 - menu.getWidth()/2, 300, 150, 45);
		menu.setBounds(win.getWidth()/2 - menu.getWidth()/2, 225, 150, 45);
		exit.setBounds(win.getWidth()/2 - exit.getWidth()/2, 150, 150, 45);
		pauseLabel.setBounds(win.getWidth()/2 - labelStyle.font.getBounds(pauseLabel.getText()).width/2, 612, labelStyle.font.getBounds(pauseLabel.getText()).width, labelStyle.font.getBounds(pauseLabel.getText()).height);

		back.setBounds(win.getWidth()/2 - resume.getWidth()/2, 150, 150, 45);
		controls.setBounds(win.getWidth()/2 - menu.getWidth()/2, 225, 150, 45);
		optionsLabel.setBounds(win.getWidth()/2 - labelStyle.font.getBounds(optionsLabel.getText()).width/2, 612, labelStyle.font.getBounds(optionsLabel.getText()).width, labelStyle.font.getBounds(optionsLabel.getText()).height);

		up.setBounds(controlsWin.getWidth() - up.getWidth() - 100, 475, 150, 45);
		down.setBounds(controlsWin.getWidth() - down.getWidth() - 100, 425, 150, 45);
		left.setBounds(controlsWin.getWidth() - left.getWidth() - 100, 375, 150, 45);
		right.setBounds(controlsWin.getWidth() - right.getWidth() - 100, 325, 150, 45);
		swing.setBounds(controlsWin.getWidth() - swing.getWidth() - 100, 275, 150, 45);

		defaults.setBounds(controlsWin.getX() + 100, 150, 150, 45);
		save.setBounds(controlsWin.getWidth() - save.getWidth() - 100, 150, 150, 45);
		controlsLabel.setBounds(controlsWin.getWidth()/2 - labelStyle.font.getBounds(controlsLabel.getText()).width/2, 612, labelStyle.font.getBounds(controlsLabel.getText()).width, labelStyle.font.getBounds(controlsLabel.getText()).height);

		win.addActor(resume);
		win.addActor(options);
		win.addActor(menu);
		win.addActor(exit);
		win.addActor(pauseLabel);

		optionsWin.addActor(back);
		optionsWin.addActor(controls);
		optionsWin.addActor(optionsLabel);
		optionsWin.addActor(volumeLabel);
		optionsWin.addActor(volumeSlider);
		optionsWin.addActor(volumeTitle);

		controlsWin.addActor(defaults);
		controlsWin.addActor(save);
		controlsWin.addActor(controlsLabel);
		controlsWin.addActor(up);
		controlsWin.addActor(down);
		controlsWin.addActor(left);
		controlsWin.addActor(right);
		controlsWin.addActor(swing);
		controlsWin.addActor(defaults);
		controlsWin.addActor(upLabel);
		controlsWin.addActor(downLabel);
		controlsWin.addActor(leftLabel);
		controlsWin.addActor(rightLabel);
		controlsWin.addActor(swingLabel);

		stage.addActor(win);
		stage.addActor(optionsWin);
		stage.addActor(controlsWin);

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
