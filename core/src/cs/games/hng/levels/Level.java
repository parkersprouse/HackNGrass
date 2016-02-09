package cs.games.hng.levels;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import cs.games.hng.Assets;
import cs.games.hng.Controls;
import cs.games.hng.Main;
import cs.games.hng.Player;
import cs.games.hng.Tile;
import cs.games.hng.screens.MainMenu;
import cs.games.hng.utils.CustomInputProcessor;
import cs.games.hng.utils.MapGenerator;
import cs.games.hng.utils.PreferenceManager;

public abstract class Level {

	protected Main game;
	protected boolean unpausing = false;
	protected boolean paused;
	protected boolean escDown = true;
	protected boolean enterDown = true;
	protected int numSwitches = 0;
	protected int numSwingsAllowed;

	// For random portal selection
	private Random random = new Random();
	private ArrayList<Tile> portals = new ArrayList<Tile>();

	private int gameMode;
	protected String lvlTitle;

	protected OrthographicCamera camera;
	protected SpriteBatch hudBatch;
	protected Tile[][] tiles;
	protected Player player;
	protected int xSize, ySize;

	protected Vector2 portalPosition;

	protected Sprite hud = Assets.atlas.createSprite("customHud");

	// Timer things
	private int TIME_LIMIT;
	private float timerTime = 0;
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
	protected Label volumeLabel, volumeTitle, effectLabel, effectTitle;
	protected Slider volumeSlider, effectSlider;
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

	// Screen States
	protected enum ScreenState {
		FIRST,
		SECOND,
		PAUSED
	}
	protected ScreenState state = ScreenState.FIRST;
	// End Screen States


	// Snow Particle Effect
	private boolean snowing = false;
	private ParticleEffect snow = new ParticleEffect();
	// End Snow

	protected Level(final Main game, String lvl, int gameMode, int limit, int numSnakes) {
		this.game = game;
		hud.setBounds(0, Main.height - 45, Main.width, 45);
		hud.setColor(0, 0, 0, 1);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Main.width*1.5f, Main.height*1.5f);
		hudBatch = new SpriteBatch();
		Assets.font.setScale(0.3f);
		Assets.bgSong.setVolume(Assets.musicVolume);
		this.gameMode = gameMode;

		snow.load(Gdx.files.internal("effects/snow.p"), Gdx.files.internal("effects"));
		snow.setPosition(0, Main.height);

		if (gameMode == 1)
			TIME_LIMIT = limit;
		else if (gameMode == 2)
			numSwingsAllowed = limit;

		// This is used to get the location of the player on the Tiled map
		// and it also sets the width and height of the map in tile size
		TiledMap map = new TmxMapLoader().load(lvl);
		TiledMapTileLayer baseLayer = (TiledMapTileLayer)map.getLayers().get("Tile Layer 1");
		xSize = baseLayer.getWidth();
		ySize = baseLayer.getHeight();
		for (int i = 0; i < baseLayer.getWidth(); i++)
			for (int j = 0; j < baseLayer.getHeight(); j++)
				if (baseLayer.getCell(i, j) != null)
					if (baseLayer.getCell(i, j).getTile().getProperties().containsKey("name")) {
						if (baseLayer.getCell(i, j).getTile().getProperties().get("name").equals("player")) {
							if (gameMode == 1)
								player = new Player(this, i*128, j*128);
							else if (gameMode == 2)
								player = new Player(this, limit, i*128, j*128);
						}
					}
		// End the nonsense

		tiles = MapGenerator.generateLevel(lvl, numSnakes);
		numSwitches = MapGenerator.getNumSwitches();

		for (int i = 0; i < xSize; i++)
			for (int j = 0; j < ySize; j++)
				if (tiles[i][j].type() == -1)
					portals.add(tiles[i][j]);

		if (portals.size() > 1) {
			// Randomly decide the location of the portal
			boolean portalPicked = false;
			while (true) {
				for (int i = 0; i < portals.size(); i++) {
					if (random.nextInt(portals.size()) == 0 && !portalPicked) {
						portals.get(i).setType(-1);
						portalPicked = true;
					}
					else
						portals.get(i).setType(1);
				}
				if (portalPicked) break;
			}
		}

		for (int i = 0; i < xSize; i++)
			for (int j = 0; j < ySize; j++)
				if (tiles[i][j].type() == -1)
					portalPosition = new Vector2(tiles[i][j].getPosition().x, tiles[i][j].getPosition().y);

		player.setMapSize(xSize, ySize);
	}

	protected void updateLevel(float delta) {		
		if (!paused) {
			if (state == ScreenState.FIRST) {
				doFade(delta);
				if (Gdx.input.isKeyPressed(Keys.ENTER) && !enterDown) {
					camera.position.set(player.getPosition().x + 64, player.getPosition().y + 64, 0);
					camera.position.y = (camera.viewportHeight/2);
					Assets.bgSong.play();
					state = ScreenState.SECOND;
					Assets.font.setColor(Color.WHITE);
				}
			}

			else if (state == ScreenState.SECOND) {
				if (snowing)
					snow.update(delta);

				if (gameMode == 1 && !player.isGameOver()) {
					timerTime += delta;
					if (timerTime >= TIME_LIMIT)
						player.gameIsOver("You ran out of time!");
				}

				/* Do all of the switch calculating
				 * The calling of this used to be cancelled once all switches were active, but I think that might have been screwing it up,
				 * so for now we're going to allow it to continue being called to see if that helps
				 */
				checkSwitches();

				player.update(tiles, delta);

				camera.position.set(player.getPosition().x + 64, player.getPosition().y + 64, 0);

				if ((Math.abs(player.getPosition().x - portalPosition.x) <= 128*3) && (Math.abs(player.getPosition().y - portalPosition.y) <= 128*3) && !player.isGameOver()) {
					Assets.portalSound.setVolume(Assets.effectVolume);
					if (!Assets.portalSound.isPlaying())
						Assets.portalSound.play();
				}
				else if ((Math.abs(player.getPosition().x - portalPosition.x) <= 128*6) && (Math.abs(player.getPosition().y - portalPosition.y) <= 128*6) && !player.isGameOver()) {
					Assets.portalSound.setVolume(Assets.effectVolume/2);
					if (!Assets.portalSound.isPlaying())
						Assets.portalSound.play();
				}
				else {
					if (Assets.portalSound.isPlaying())
						Assets.portalSound.stop();
				}
			}

			// For pausing the game at any point (now only doing during actual gameplay)
			if (Gdx.input.isKeyPressed(Keys.ESCAPE) && !escDown && state == ScreenState.SECOND) {
				Assets.bgSong.pause();
				paused = true;
				unpausing = false;
				escDown = true;
			}
		}

		/*
		 *  Makes sure the camera can't go beyond the bounds of the level
		 */
		// Lower bounds (left and bottom)
		if (camera.position.x < 0 + (camera.viewportWidth/2))
			camera.position.x = (camera.viewportWidth/2);
		if (camera.position.y < 0 + (camera.viewportHeight/2))
			camera.position.y = (camera.viewportHeight/2);
		// Upper bounds (right and top)
		if (camera.position.x > (128*xSize - camera.viewportWidth/2))
			camera.position.x = (128*xSize - camera.viewportWidth/2);
		if (camera.position.y > (128*ySize - camera.viewportHeight/2))
			camera.position.y = (128*ySize - camera.viewportHeight/2);


		if (changingButton)
			changeInputButton();

		if (paused) {
			if ((Gdx.input.isKeyPressed(Keys.ESCAPE) && !escDown && win.isVisible()) || unpausing) {
				if (win.getX() > -540) {
					win.setBounds(win.getX() - 20, 0, Main.width/2, 768);
					unpausing = true;
				}
				else {
					Assets.bgSong.play();
					paused = false;
					Assets.font.setScale(0.3f);
					PreferenceManager.savePreferences();
				}
				escDown = true;
			}
			else if ((Gdx.input.isKeyPressed(Keys.ESCAPE) && !escDown && optionsWin.isVisible())) {
				win.setVisible(true);
				optionsWin.setVisible(false);
				escDown = true;
				PreferenceManager.savePreferences();
			}
			else if ((Gdx.input.isKeyPressed(Keys.ESCAPE) && !escDown && controlsWin.isVisible())) {
				optionsWin.setVisible(true);
				controlsWin.setVisible(false);
				escDown = true;
				PreferenceManager.savePreferences();
			}
		}

		if (!Gdx.input.isKeyPressed(Keys.ESCAPE))
			escDown = false;
		if (!Gdx.input.isKeyPressed(Keys.ENTER))
			enterDown = false;
	}

	protected void drawLevel() {
		camera.update();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (!paused) {
			if (state == ScreenState.FIRST) {
				hudBatch.begin();
				Assets.font.setColor(Color.WHITE);
				Assets.font.draw(hudBatch, lvlTitle, Main.width/2 - (Assets.font.getBounds(lvlTitle).width/2), Main.height/2 + 150);
				Assets.font.setColor(1, 1, 1, opacity);
				Assets.font.draw(hudBatch, "Press [Enter]", Main.width/2 - (Assets.font.getBounds("Press [Enter]").width/2), Main.height/2 - 200);
				player.drawHUD(hudBatch);
				hudBatch.end();
			}

			else if (state == ScreenState.SECOND) {
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

				if (snowing) {
					hudBatch.begin();
					snow.draw(hudBatch);
					hudBatch.end();
				}

				hudBatch.begin();
				hud.draw(hudBatch);
				if (gameMode == 1)
					Assets.font.draw(hudBatch, "" + (TIME_LIMIT - (int)timerTime), 10, Gdx.graphics.getHeight() - 10);
				else if (gameMode == 2)
					Assets.font.draw(hudBatch, player.getSwingsLeft() + " swings left", 10, Gdx.graphics.getHeight() - Assets.font.getBounds(player.getSwingsLeft() + " swings left").height + 10);
				if (numSwitches > 0)
					Assets.font.draw(hudBatch, player.getSwitchCount() + " / " + numSwitches, Main.width - 100, Gdx.graphics.getHeight() - 10);
				player.drawHUD(hudBatch);
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

			if (snowing) {
				hudBatch.begin();
				snow.draw(hudBatch);
				hudBatch.end();
			}

			hudBatch.begin();
			hud.draw(hudBatch);
			if (gameMode == 1)
				Assets.font.draw(hudBatch, "" + (TIME_LIMIT - (int)timerTime), 10, Gdx.graphics.getHeight() - 10);
			else if (gameMode == 2)
				Assets.font.draw(hudBatch, player.getSwingsLeft() + " swings left", 10, Gdx.graphics.getHeight() - Assets.font.getBounds(player.getSwingsLeft() + " swings left").height + 10);
			if (numSwitches > 0)
				Assets.font.draw(hudBatch, player.getSwitchCount() + " / " + numSwitches, Main.width - 100, Gdx.graphics.getHeight() - 10);
			player.drawHUD(hudBatch);
			hudBatch.end();

			if (win.getX() <= -20 && !unpausing) {
				win.setBounds(win.getX() + 20, 0, Main.width/2, Main.height);
			}

			stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1/30f));
			stage.draw();
		}
	}

	protected void doFade(float delta) {
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
	
	protected void setSnowing(boolean s) {
		snowing = s;
	}

	private void changeInputButton() {
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

	
	/*
	 * Needs a COMPLETE reworking.
	 * It's a piece of shit.
	 */
	
	private void checkSwitches() {
		// First set of switches
		if (MapGenerator.switches1 != null && !MapGenerator.switches1.isEmpty()) {
			for (Iterator<Tile> tileList = MapGenerator.switches1.iterator(); tileList.hasNext();) {
				Tile t = tileList.next();
				if (t.type() == -3)
					tileList.remove();
			}
		}
		if (MapGenerator.switches1 != null && MapGenerator.switches1.isEmpty() && MapGenerator.triggers1 != null && !MapGenerator.triggers1.isEmpty()) {			
			for (Iterator<Tile> tileList = MapGenerator.triggers1.iterator(); tileList.hasNext();) {
				Tile t = tileList.next();
				t.setEvilType("none");
				t.setType(0);
			}
		}

		// Second set of switches
		if (MapGenerator.switches2 != null && !MapGenerator.switches2.isEmpty()) {
			for (Iterator<Tile> tileList = MapGenerator.switches2.iterator(); tileList.hasNext();) {
				Tile t = tileList.next();
				if (t.type() == -3)
					tileList.remove();
			}
		}
		if (MapGenerator.switches2 != null && MapGenerator.switches2.isEmpty() && MapGenerator.triggers2 != null && !MapGenerator.triggers2.isEmpty()) {			
			for (Iterator<Tile> tileList = MapGenerator.triggers2.iterator(); tileList.hasNext();) {
				Tile t = tileList.next();
				t.setEvilType("none");
				t.setType(0);
			}
		}

		// Third set of switches
		if (MapGenerator.switches3 != null && !MapGenerator.switches3.isEmpty()) {
			for (Iterator<Tile> tileList = MapGenerator.switches3.iterator(); tileList.hasNext();) {
				Tile t = tileList.next();
				if (t.type() == -3)
					tileList.remove();
			}
		}
		if (MapGenerator.switches3 != null && MapGenerator.switches3.isEmpty() && MapGenerator.triggers3 != null && !MapGenerator.triggers3.isEmpty()) {			
			for (Iterator<Tile> tileList = MapGenerator.triggers3.iterator(); tileList.hasNext();) {
				Tile t = tileList.next();
				t.setEvilType("none");
				t.setType(0);
			}
		}

		// Fourth set of switches
		if (MapGenerator.switches4 != null && !MapGenerator.switches4.isEmpty()) {
			for (Iterator<Tile> tileList = MapGenerator.switches4.iterator(); tileList.hasNext();) {
				Tile t = tileList.next();
				if (t.type() == -3)
					tileList.remove();
			}
		}
		if (MapGenerator.switches4 != null && MapGenerator.switches4.isEmpty() && MapGenerator.triggers4 != null && !MapGenerator.triggers4.isEmpty()) {			
			for (Iterator<Tile> tileList = MapGenerator.triggers4.iterator(); tileList.hasNext();) {
				Tile t = tileList.next();
				t.setEvilType("none");
				t.setType(0);
			}
		}

		// Fifth set of switches
		if (MapGenerator.switches5 != null && !MapGenerator.switches5.isEmpty()) {
			for (Iterator<Tile> tileList = MapGenerator.switches5.iterator(); tileList.hasNext();) {
				Tile t = tileList.next();
				if (t.type() == -3)
					tileList.remove();
			}
		}
		if (MapGenerator.switches5 != null && MapGenerator.switches5.isEmpty() && MapGenerator.triggers5 != null && !MapGenerator.triggers5.isEmpty()) {			
			for (Iterator<Tile> tileList = MapGenerator.triggers5.iterator(); tileList.hasNext();) {
				Tile t = tileList.next();
				t.setEvilType("none");
				t.setType(0);
			}
		}

		// Sixth set of switches
		if (MapGenerator.switches6 != null && !MapGenerator.switches6.isEmpty()) {
			for (Iterator<Tile> tileList = MapGenerator.switches6.iterator(); tileList.hasNext();) {
				Tile t = tileList.next();
				if (t.type() == -3)
					tileList.remove();
			}
		}
		if (MapGenerator.switches6 != null && MapGenerator.switches6.isEmpty() && MapGenerator.triggers6 != null && !MapGenerator.triggers6.isEmpty()) {			
			for (Iterator<Tile> tileList = MapGenerator.triggers6.iterator(); tileList.hasNext();) {
				Tile t = tileList.next();
				t.setEvilType("none");
				t.setType(0);
			}
		}

		if (((MapGenerator.switches1 != null && MapGenerator.switches1.isEmpty()) || (MapGenerator.switches1 == null)) && ((MapGenerator.switches2 != null && MapGenerator.switches2.isEmpty()) || (MapGenerator.switches2 == null)) && ((MapGenerator.switches3 != null && MapGenerator.switches3.isEmpty()) || (MapGenerator.switches3 == null)) && ((MapGenerator.switches4 != null && MapGenerator.switches4.isEmpty()) || (MapGenerator.switches4 == null)) && ((MapGenerator.switches5 != null && MapGenerator.switches5.isEmpty()) || (MapGenerator.switches5 == null)) && ((MapGenerator.switches6 != null && MapGenerator.switches6.isEmpty()) || (MapGenerator.switches6 == null))) {
			MapGenerator.switches1 = null;
			MapGenerator.switches2 = null;
			MapGenerator.switches3 = null;
			MapGenerator.switches4 = null;
			MapGenerator.switches5 = null;
			MapGenerator.switches6 = null;
		}
	}

	// Creates the pause menu GUI
	protected void setupGUI() {
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
		volumeSlider.setBounds(optionsWin.getWidth()/2 + 50, 400, 200, 25);

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
		effectSlider.setBounds(optionsWin.getWidth()/2 + 50, 300, 200, 25);

		volumeLabel = new Label((int)(volumeSlider.getValue() * 100) + "%", controlLabelStyle);
		volumeLabel.setBounds(volumeSlider.getX() + volumeSlider.getWidth() + 25, 400, 350, 25);
		effectLabel = new Label((int)(effectSlider.getValue() * 100) + "%", controlLabelStyle);
		effectLabel.setBounds(effectSlider.getX() + effectSlider.getWidth() + 25, 300, 100, 25);

		volumeTitle = new Label("Music Volume", controlLabelStyle);
		volumeTitle.setBounds(optionsWin.getWidth(), 450, 100, 25);
		effectTitle = new Label("Effect Volume", controlLabelStyle);
		effectTitle.setBounds(optionsWin.getWidth(), 350, 100, 25);

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
					PreferenceManager.savePreferences();
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
					PreferenceManager.savePreferences();
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
				if (paused) {
					Gdx.input.setInputProcessor(input);
					buttonToChange = ControlButton.UP;
					changingButton = true;
				}
			}
		});

		down = new TextButton(Keys.toString(Controls.DOWN), style);
		down.setBounds(controlsWin.getWidth() - 250, 300, 150, 45);
		down.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				if (paused) {
					Gdx.input.setInputProcessor(input);
					buttonToChange = ControlButton.DOWN;
					changingButton = true;
				}
			}
		});

		left = new TextButton(Keys.toString(Controls.LEFT), style);
		left.setBounds(controlsWin.getWidth() - 250, 250, 150, 45);
		left.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				if (paused) {
					Gdx.input.setInputProcessor(input);
					buttonToChange = ControlButton.LEFT;
					changingButton = true;
				}
			}
		});

		right = new TextButton(Keys.toString(Controls.RIGHT), style);
		right.setBounds(controlsWin.getWidth() - 250, 200, 150, 45);
		right.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				if (paused) {
					Gdx.input.setInputProcessor(input);
					buttonToChange = ControlButton.RIGHT;
					changingButton = true;
				}
			}
		});

		swing = new TextButton(Keys.toString(Controls.SWING), style);
		swing.setBounds(controlsWin.getWidth() - 250, 150, 150, 45);
		swing.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				if (paused) {
					Gdx.input.setInputProcessor(input);
					buttonToChange = ControlButton.SWING;
					changingButton = true;
				}
			}
		});

		save = new TextButton("Save", style);
		save.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				if (paused) {
					optionsWin.setVisible(true);
					controlsWin.setVisible(false);
					PreferenceManager.savePreferences();
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
					PreferenceManager.savePreferences();
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

		back.setBounds(win.getWidth()/2 - resume.getWidth()/2, 100, 150, 45);
		controls.setBounds(win.getWidth()/2 - menu.getWidth()/2, 175, 150, 45);
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
		optionsWin.addActor(effectLabel);
		optionsWin.addActor(effectSlider);
		optionsWin.addActor(effectTitle);

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

	public abstract void resetLevel();

	public abstract void nextLevel();

	public abstract String getCode();

}