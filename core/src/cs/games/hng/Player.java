package cs.games.hng;

import java.util.Random;
import java.util.TimerTask;
import java.util.Timer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import cs.games.hng.levels.Level;
import cs.games.hng.levels.LevelFive;
import cs.games.hng.utils.Animator;

public class Player {

	private int x, y, xTiles, yTiles;
	private Vector2 pos, startPos;
	private boolean spaceDown, enterDown, limitedSwings, isHole, isSwitch, processMovement;
	private Tile switchTile = null;
	private int switchesOn = 0;

	private Sprite hud = Assets.atlas.createSprite("customHud");

	// Timer for movement
	private int moveSpeed = 150000000; // 0.15 second
	private long timeSinceMove = TimeUtils.nanoTime();
	// Timer for snakes
	private int untilSnakeKills = 1250;//750;
	private long timeSinceSnakeFound;
	// Timer for animation
	private int animationLength = 225;
	private long timeSinceAnimationStarted = ((TimeUtils.nanoTime()/1000)/1000);

	// For generating QTEs for snakes
	private boolean newQTE;
	private char qteLetter;
	private int qteKey;
	private char[] qteLetters = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	private int[] qteKeys = {Keys.A, Keys.B, Keys.C, Keys.D, Keys.E, Keys.F, Keys.G, Keys.H, Keys.I, Keys.J, Keys.K, Keys.L, Keys.M, Keys.N, Keys.O, Keys.P, Keys.Q, Keys.R, Keys.S, Keys.T, Keys.U, Keys.V, Keys.W, Keys.X, Keys.Y, Keys.Z};
	private Random random = new Random();
	// End Snake QTE

	private int rotation, numSwings;
	private Animator animator;
	private boolean playAnimation, gameOver, levelComplete, showQTE;
	private String gameOverMessage;
	private Level level;
	private Tile[][] tiles;

	// Timer things
	private float TIME_LIMIT = 0.5f;
	private float timerTime = 0;
	// End timer things
	
	private Timer timer = new Timer();

	public Player(Level level, int num, int x, int y) {
		this(level, x, y);
		numSwings = num;
		limitedSwings = true;
	}

	public Player(Level level, int x, int y) {
		this.level = level;
		this.x = x;
		this.y = y;
		pos = new Vector2(x, y);
		startPos = new Vector2(x, y);
		rotation = 90;
		animator = new Animator("playerSwing.png", 0.03f, false);
		playAnimation = false;
		gameOver = false;
		gameOverMessage = "";
		levelComplete = false;
		showQTE = false;
		newQTE = true;
		spaceDown = true;
		enterDown = true;
		limitedSwings = false;
		processMovement = true;
		isHole = false;
		isSwitch = false;
		xTiles = yTiles = 0;
	}

	public int getSwingsLeft() {
		if (limitedSwings)
			return numSwings;
		else
			return -1;
	}

	public Vector2 getPosition() {
		return pos;
	}

	public void setMapSize(int x, int y) {
		xTiles = x;
		yTiles = y;
	}

	public void gameIsOver(String message) {
		gameOver = true;
		gameOverMessage = message;
	}

	public boolean isGameOver() {
		return (gameOver || levelComplete);
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		this.pos = new Vector2(x, y);
	}

	public Tile getSwitch() {
		return switchTile;
	}

	public int getSwitchCount() {
		return switchesOn;
	}

	public void update(Tile[][] tiles, float delta) { // If still interested in clicking a popup for QTE, pass the hud camera from the level here for unprojection of touch point

		this.tiles = tiles;

		/*
		 * If the game is not over
		 */
		if (!gameOver && !levelComplete) {

			for(int i = 0; i < xTiles; i++) {
				for (int j = 0; j < yTiles; j++) {
					Tile t = tiles[i][j];
					if (t.type() == 0 && t.getEvilType().equals("snake")) {
						showQTE = true;
						if (newQTE) {
							int q = random.nextInt(22);
							if (qteKeys[q] != Controls.DOWN && qteKeys[q] != Controls.UP && qteKeys[q] != Controls.LEFT && qteKeys[q] != Controls.RIGHT && qteKeys[q] != Controls.SWING) {
								qteKey = qteKeys[q];
								qteLetter = qteLetters[q];
								newQTE = false;
							}
						}
						
						if (Gdx.input.isKeyPressed(qteKey)) {
							t.setEvilType("none");
							showQTE = false;
							newQTE = true;
							break;
						}

						if (((TimeUtils.nanoTime()/1000)/1000) - timeSinceSnakeFound > untilSnakeKills) {
							gameOver = true;
							showQTE = false;
							gameOverMessage = "You were killed by a slithery snaaaaake";
							break;
						}
					}
				}
				if (gameOver) break;
			}

			if (!showQTE && processMovement)
				movement();

			if (Gdx.input.isKeyPressed(Controls.SWING) && !playAnimation && !spaceDown && !showQTE && processMovement && (((limitedSwings && numSwings > 0) || !limitedSwings) || portalClear())) {
				timeSinceAnimationStarted = ((TimeUtils.nanoTime()/1000)/1000);
				if ((limitedSwings && numSwings > 0) || !limitedSwings)
					playAnimation = true;
				swing();
			}

			else if (limitedSwings && numSwings <= 0 && !portalClear()) {
				gameOver = true;
				gameOverMessage = "You ran out of swings!";
			}

			if (((TimeUtils.nanoTime()/1000)/1000) - timeSinceAnimationStarted > animationLength && playAnimation) {
				playAnimation = false;
				animator.resetAnimation();
			}

			if (isHole) {
				timerTime += delta;
				if (timerTime >= TIME_LIMIT) {
					timerTime -= TIME_LIMIT;
					setPosition((int)startPos.x, (int)startPos.y);
					rotation = 90;
					isHole = false;
					processMovement = true;
				}
			}

		}

		/*
		 * If the game is over
		 */
		else if (gameOver) {
			Assets.bgSong.stop();
			if (Assets.portalSound.isPlaying())
				Assets.portalSound.stop();
			if (Gdx.input.isKeyPressed(Controls.ENTER) && !enterDown) level.resetLevel();
		}

		/*
		 * If the player completed the level
		 */
		else if (levelComplete) {
			Assets.bgSong.stop();
			if (Assets.portalSound.isPlaying())
				Assets.portalSound.stop();
			if (Gdx.input.isKeyPressed(Controls.ENTER) && !enterDown) level.nextLevel();
		}

		/*
		 * My personal way of creating "hit-once" keys
		 */
		if (!Gdx.input.isKeyPressed(Controls.SWING))
			spaceDown = false;
		if (!Gdx.input.isKeyPressed(Controls.ENTER))
			enterDown = false;

	}

	public void draw(SpriteBatch batch) {
		if (playAnimation)
			batch.draw(animator.getAnimation(), x, y, 64, 64, 128, 128, 1, 1, rotation, true);
		else
			batch.draw(animator.getFrame(0), x, y, 64, 64, 128, 128, 1, 1, rotation, true);
	}

	public void drawPaused(SpriteBatch batch) {
		batch.draw(animator.getCurrentFrame(), x, y, 64, 64, 128, 128, 1, 1, rotation, true);
	}

	public void drawHUD(SpriteBatch batch) {
		if (gameOver) {
			hud.setBounds(0, Main.height/2 - 112, Main.width, 200);
			hud.setColor(0.1f, 0.1f, 0.1f, 0.75f);
			hud.draw(batch);
			Assets.font.draw(batch, gameOverMessage, Main.width/2 - (Assets.font.getBounds(gameOverMessage).width/2), Main.height/2 + 50);
			Assets.font.draw(batch, "Press [Enter] to try again", Main.width/2 - (Assets.font.getBounds("Press [Enter] to try again").width/2), Main.height/2 - 50);
		}
		else if (levelComplete) {
			if (level.getCode().equals(LevelFive.code)) {
				hud.setBounds(0, Main.height/2 - 112, Main.width, 300);
				hud.setColor(0.1f, 0.1f, 0.1f, 0.75f);
				hud.draw(batch);
				Assets.font.draw(batch, "Level Complete!", Main.width/2 - (Assets.font.getBounds("Level Complete!").width/2), Main.height/2 + 150);
				Assets.font.draw(batch, "Code for next level: \"" + level.getCode() + "\"", Main.width/2 - (Assets.font.getBounds("Code for next level: " + level.getCode()).width/2), Main.height/2 + 50);
				Assets.font.draw(batch, "Press [Enter] to continue", Main.width/2 - (Assets.font.getBounds("Press [Enter] to continue").width/2), Main.height/2 - 50);
			}
			else {
				hud.setBounds(0, Main.height/2 - 100, Main.width, 200);
				hud.setColor(0.1f, 0.1f, 0.1f, 0.75f);
				hud.draw(batch);
				Assets.font.draw(batch, "Level Complete!", Main.width/2 - (Assets.font.getBounds("Level Complete!").width/2), Main.height/2 + 65);
				Assets.font.draw(batch, "Press [Enter] to continue", Main.width/2 - (Assets.font.getBounds("Press [Enter] to continue").width/2), Main.height/2 - 35);
			}
		}

		else if (showQTE) {
			hud.setBounds(0, Main.height/2 + 77, Main.width, 100);
			hud.setColor(0.5f, 0, 1, 0.5f);
			hud.draw(batch);
			Assets.font.setScale(0.6f);
			Assets.font.draw(batch, "PRESS [" + qteLetter + "]", Main.width/2 - Assets.font.getBounds("PRESS [" + qteLetter + "]").width/2, Main.height/2 + 150);
			Assets.font.setScale(0.3f);
		}
	}


	/**
	 * Check the tiles surrounding the player
	 * to determine whether or not he can move
	 */
	private boolean canMove(String loc) {
		boolean canMove = false;

		if (loc.equals("up")) {
			boolean hit = false;
			Tile t = null;
			for(int i = 0; i < xTiles; i++) {
				for (int j = 0; j < yTiles; j++) {
					t = tiles[i][j];
					if (t.getPosition().y == this.y + 128 && t.getPosition().x == this.x && !hit) {
						if (t.type() == 0 && t.getEvilType().equals("none"))
							canMove = true;
						else if (t.type() == 0 && t.getEvilType().equals("hole")) {
							isHole = true;
							canMove = true;
							processMovement = false;
						}
						else if (t.type() == -2 && !t.isHiddenSwitch()) {
							isSwitch = true;
							canMove = true;
							switchTile = t;
						}
						else if (t.type() == -3)
							canMove = true;
						hit = true;
						break;
					}
				}
				if (hit) break;
			}
		}
		else if (loc.equals("down")) {
			boolean hit = false;
			Tile t = null;
			for(int i = 0; i < xTiles; i++) {
				for (int j = 0; j < yTiles; j++) {
					t = tiles[i][j];
					if (t.getPosition().y + 128 == this.y && t.getPosition().x == this.x && !hit) {
						if (t.type() == 0 && t.getEvilType().equals("none"))
							canMove = true;
						else if (t.type() == 0 && t.getEvilType().equals("hole")) {
							isHole = true;
							canMove = true;
							processMovement = false;
						}
						else if (t.type() == -2 && !t.isHiddenSwitch()) {
							isSwitch = true;
							canMove = true;
							switchTile = t;
						}
						else if (t.type() == -3)
							canMove = true;
						hit = true;
						break;
					}
				}
				if (hit) break;
			}
		}
		else if (loc.equals("left")) {
			boolean hit = false;
			Tile t = null;
			for(int i = 0; i < xTiles; i++) {
				for (int j = 0; j < yTiles; j++) {
					t = tiles[i][j];
					if (t.getPosition().y == this.y && t.getPosition().x + 128 == this.x && !hit) {
						if (t.type() == 0 && t.getEvilType().equals("none"))
							canMove = true;
						else if (t.type() == 0 && t.getEvilType().equals("hole")) {
							isHole = true;
							canMove = true;
							processMovement = false;
						}
						else if (t.type() == -2 && !t.isHiddenSwitch()) {
							isSwitch = true;
							canMove = true;
							switchTile = t;
						}
						else if (t.type() == -3)
							canMove = true;
						hit = true;
						break;
					}
				}
				if (hit) break;
			}
		}
		else if (loc.equals("right")) {
			boolean hit = false;
			Tile t = null;
			for(int i = 0; i < xTiles; i++) {
				for (int j = 0; j < yTiles; j++) {
					t = tiles[i][j];
					if (t.getPosition().y == this.y && t.getPosition().x == this.x + 128 && !hit) {
						if (t.type() == 0 && t.getEvilType().equals("none"))
							canMove = true;
						else if (t.type() == 0 && t.getEvilType().equals("hole")) {
							isHole = true;
							canMove = true;
							processMovement = false;
						}
						else if (t.type() == -2 && !t.isHiddenSwitch()) {
							isSwitch = true;
							canMove = true;
							switchTile = t;
						}
						else if (t.type() == -3)
							canMove = true;
						hit = true;
						break;
					}
				}
				if (hit) break;
			}
		}
		return canMove;
	}


	/**
	 * Handle the user's input for determining movement
	 */
	private void movement() {
		if (Gdx.input.isKeyPressed(Controls.UP) && TimeUtils.nanoTime() - timeSinceMove > moveSpeed) {
			rotation = 90;
			if (y < yTiles*128 - 128) {
				if (canMove("up")) {
					y += 128;
					pos.y = y;
				}
				if (isSwitch) {
					switchTile.setType(-3);
					switchesOn++;
					isSwitch = false;
				}

			}
			timeSinceMove = TimeUtils.nanoTime();
		}
		else if (Gdx.input.isKeyPressed(Controls.DOWN) && TimeUtils.nanoTime() - timeSinceMove > moveSpeed) {
			rotation = 270;
			if (y > 90) {
				if (canMove("down")) {
					y -= 128;
					pos.y = y;
				}
				if (isSwitch) {
					switchTile.setType(-3);
					switchesOn++;
					isSwitch = false;
				}
			}
			timeSinceMove = TimeUtils.nanoTime();
		}
		else if (Gdx.input.isKeyPressed(Controls.LEFT) && TimeUtils.nanoTime() - timeSinceMove > moveSpeed) {
			rotation = 180;
			if (x > 0) {
				if (canMove("left")) {
					x -= 128;
					pos.x = x;
				}
				if (isSwitch) {
					switchTile.setType(-3);
					switchesOn++;
					isSwitch = false;
				}
			}
			timeSinceMove = TimeUtils.nanoTime();
		}
		else if (Gdx.input.isKeyPressed(Controls.RIGHT) && TimeUtils.nanoTime() - timeSinceMove > moveSpeed) {
			rotation = 0;
			if (x < xTiles*128 - 128) {
				if (canMove("right")) {
					x += 128;
					pos.x = x;
				}
				if (isSwitch) {
					switchTile.setType(-3);
					switchesOn++;
					isSwitch = false;
				}
			}
			timeSinceMove = TimeUtils.nanoTime();
		}
	}


	/**
	 * Handle the user's input for determining whether
	 * the user is swinging or not
	 */
	private void swing() {
		spaceDown = true;
		boolean hit = false;
		Tile t = null;
		for(int i = 0; i < xTiles; i++) {
			for (int j = 0; j < yTiles; j++) {
				t = tiles[i][j];
				if (rotation == 90 && t.getPosition().y == this.y + 128 && t.getPosition().x == this.x && !hit) {
					if ((t.type() == 1 || t.type() == 2) && ((limitedSwings && numSwings > 0) || !limitedSwings)) {
						killGrass(t);
						hit = true;
						if (Assets.grassCut.isPlaying()) {
							Assets.grassCut.stop();
							Assets.grassCut.play();
						}
						else {
							Assets.grassCut.play();
						}
					}
					else if ((t.type() == -2 && t.isHiddenSwitch()) && ((limitedSwings && numSwings > 0) || !limitedSwings)) {
						killGrass(t);
						hit = true;
						if (Assets.grassCut.isPlaying()) {
							Assets.grassCut.stop();
							Assets.grassCut.play();
						}
						else {
							Assets.grassCut.play();
						}
					}
					else if (t.type() == -1) {
						levelComplete = true;
						Assets.levelComplete.play();
					}
					else if (t.type() == 0 && t.getEvilType().equals("rock")) {
						if (Assets.rockHit.isPlaying()) {
							Assets.rockHit.stop();
							Assets.rockHit.play();
						}
						else {
							Assets.rockHit.play();
						}
					}
					break;
				}
				else if (rotation == 270 && t.getPosition().y + 128 == this.y && t.getPosition().x == this.x && !hit) {
					if ((t.type() == 1 || t.type() == 2) && ((limitedSwings && numSwings > 0) || !limitedSwings)) {
						killGrass(t);
						hit = true;
						if (Assets.grassCut.isPlaying()) {
							Assets.grassCut.stop();
							Assets.grassCut.play();
						}
						else {
							Assets.grassCut.play();
						}
					}
					else if ((t.type() == -2 && t.isHiddenSwitch()) && ((limitedSwings && numSwings > 0) || !limitedSwings)) {
						killGrass(t);
						hit = true;
						if (Assets.grassCut.isPlaying()) {
							Assets.grassCut.stop();
							Assets.grassCut.play();
						}
						else {
							Assets.grassCut.play();
						}
					}
					else if (t.type() == -1) {
						levelComplete = true;
						Assets.levelComplete.play();
					}
					else if (t.type() == 0 && t.getEvilType().equals("rock")) {
						if (Assets.rockHit.isPlaying()) {
							Assets.rockHit.stop();
							Assets.rockHit.play();
						}
						else {
							Assets.rockHit.play();
						}
					}
					break;
				}
				else if (rotation == 180 && t.getPosition().y == this.y && t.getPosition().x + 128 == this.x && !hit) {
					if ((t.type() == 1 || t.type() == 2) && ((limitedSwings && numSwings > 0) || !limitedSwings)) {
						killGrass(t);
						hit = true;
						if (Assets.grassCut.isPlaying()) {
							Assets.grassCut.stop();
							Assets.grassCut.play();
						}
						else {
							Assets.grassCut.play();
						}
					}
					else if ((t.type() == -2 && t.isHiddenSwitch()) && ((limitedSwings && numSwings > 0) || !limitedSwings)) {
						killGrass(t);
						hit = true;
						if (Assets.grassCut.isPlaying()) {
							Assets.grassCut.stop();
							Assets.grassCut.play();
						}
						else {
							Assets.grassCut.play();
						}
					}
					else if (t.type() == -1) {
						levelComplete = true;
						Assets.levelComplete.play();
					}
					else if (t.type() == 0 && t.getEvilType().equals("rock")) {
						if (Assets.rockHit.isPlaying()) {
							Assets.rockHit.stop();
							Assets.rockHit.play();
						}
						else {
							Assets.rockHit.play();
						}
					}
					break;
				}
				else if (rotation == 0 && t.getPosition().y == this.y && t.getPosition().x == this.x + 128 && !hit) {
					if ((t.type() == 1 || t.type() == 2) && ((limitedSwings && numSwings > 0) || !limitedSwings)) {
						killGrass(t);
						hit = true;
						if (Assets.grassCut.isPlaying()) {
							Assets.grassCut.stop();
							Assets.grassCut.play();
						}
						else {
							Assets.grassCut.play();
						}
					}
					else if ((t.type() == -2 && t.isHiddenSwitch()) && ((limitedSwings && numSwings > 0) || !limitedSwings)) {
						killGrass(t);
						hit = true;
						if (Assets.grassCut.isPlaying()) {
							Assets.grassCut.stop();
							Assets.grassCut.play();
						}
						else {
							Assets.grassCut.play();
						}
					}
					else if (t.type() == -1) {
						levelComplete = true;
						Assets.levelComplete.play();
					}
					else if (t.type() == 0 && t.getEvilType().equals("rock")) {
						if (Assets.rockHit.isPlaying()) {
							Assets.rockHit.stop();
							Assets.rockHit.play();
						}
						else {
							Assets.rockHit.play();
						}
					}
					break;
				}
			}
			if (hit) break;
		}
	}

	private void killGrass(final Tile t) {
		if (t.type() == 1) {
			t.setType(-10);
			timer.schedule(new TimerTask() {
				public void run() {
					if (t.getEvilType().equals("snake")) {
						timeSinceSnakeFound = ((TimeUtils.nanoTime()/1000)/1000);
						if (Assets.snakeHiss.isPlaying()) {
							Assets.snakeHiss.stop();
							Assets.snakeHiss.play();
						}
						else {
							Assets.snakeHiss.play();
						}
					}
					else if (t.getEvilType().equals("rock")) {
						if (Assets.rockHit.isPlaying()) {
							Assets.rockHit.stop();
							Assets.rockHit.play();
						}
						else {
							Assets.rockHit.play();
						}
					}
					t.getHit();
					Assets.grassAnimation.resetAnimation();
				}
			}, 160);
		}
		else if (t.type() == -2 && t.isHiddenSwitch()) {
			t.setType(-10);
			timer.schedule(new TimerTask() {
				public void run() {
					t.getHit();
					Assets.grassAnimation.resetAnimation();
				}
			}, 160);
		}
		else 
			t.getHit();
		if (limitedSwings)
			numSwings--;
	}


	/**
	 * Determines whether or not at least one tile is clear around the portal.
	 * This is important so that if the player clears the portal but runs out
	 * of swings, the game doesn't think he's lost.
	 */
	private boolean portalClear() {
		boolean portalClear = false;
		Tile t;
		for(int i = 0; i < xTiles; i++) {
			for (int j = 0; j < yTiles; j++) {
				t = tiles[i][j];
				if (t.type() == -1 && 
						(  (tiles[i-1][j].type() == 0 && tiles[i-1][j].getEvilType().equals("none")) 
								|| (tiles[i+1][j].type() == 0 && tiles[i+1][j].getEvilType().equals("none")) 
								|| (tiles[i][j-1].type() == 0 && tiles[i][j-1].getEvilType().equals("none")) 
								|| (tiles[i][j+1].type() == 0 && tiles[i][j+1].getEvilType().equals("none"))
								)) {
					portalClear = true;
					break;
				}
			}
			if (portalClear) break;
		}

		return portalClear;
	}

}