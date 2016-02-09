package cs.games.hng;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import cs.games.hng.levels.Tutorial;
import cs.games.hng.utils.Animator;

public class PlayerTutorial {

	private int x, y, xTiles, yTiles;
	private Vector2 pos;
	private boolean spaceDown, enterDown, limitedSwings;
	private int moveSpeed = 150000000; // 0.15 second
	private long timeSinceMove = TimeUtils.nanoTime();
	private int untilSnakeKills = 1250;//750;
	private long timeSinceSnakeFound;
	private int animationLength = 225;
	private long timeSinceAnimationStarted = ((TimeUtils.nanoTime()/1000)/1000);
	private boolean newQTE;
	private char qteLetter;
	private int qteKey;
	private char[] qteLetters = {'B', 'C', 'E', 'F', 'G', 'H', 'I', 'J','K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'T', 'U', 'V', 'X', 'Y', 'Z'};
	private int[] qteKeys = {Keys.B, Keys.C, Keys.E, Keys.F, Keys.G, Keys.H, Keys.I, Keys.J, Keys.K, Keys.L, Keys.M, Keys.N, Keys.O, Keys.P, Keys.Q, Keys.R, Keys.T, Keys.U, Keys.V, Keys.X, Keys.Y, Keys.Z};
	private int rotation, numSwings;
	private Animator animator;
	private boolean playAnimation, gameOver, levelComplete, showQTE;
	private String gameOverMessage;
	private Tutorial level;
	private Tile[][] tiles;
	private Random random = new Random();

	private Sprite hud = Assets.atlas.createSprite("customHud");

	private boolean stageOneComplete = false; // Stage one is when the game is waiting for the player to press space for the first time and hit the grass
	private boolean stageTwoComplete = false; // Stage two is when the player has hit the grass and the snake is exposed, triggering the QTE
	private boolean stageThreeComplete = false;

	public PlayerTutorial(Tutorial level, int num, int x, int y) {
		this(level, x, y);
		numSwings = num;
		limitedSwings = true;
	}
	
	public PlayerTutorial(Tutorial level, int x, int y) {
		this.level = level;
		this.x = x;
		this.y = y;
		pos = new Vector2(x, y);
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

		hud.setColor(0, 0, 0, 0.75f);

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

	public boolean stageOneComplete() {
		return stageOneComplete;
	}

	public boolean stageTwoComplete() {
		return stageTwoComplete;
	}

	public boolean stageThreeComplete() {
		return stageThreeComplete;
	}

	public void update(Tile[][] tiles, int state) {

		this.tiles = tiles;

		if (state == 1) {
			if (Gdx.input.isKeyPressed(Controls.SWING) && !spaceDown && !showQTE) {
				timeSinceAnimationStarted = ((TimeUtils.nanoTime()/1000)/1000);
				playAnimation = true;
				stageOneComplete = true;
				swing();
			}
		}

		else if (state == 2) {
			if (((TimeUtils.nanoTime()/1000)/1000) - timeSinceAnimationStarted > animationLength && playAnimation) {
				playAnimation = false;
				animator.resetAnimation();
			}

			if (tiles[10][1].type() == 0 && tiles[10][1].getEvilType().equals("snake")) {
				showQTE = true;
				if (Gdx.input.isKeyPressed(Keys.E)) {
					tiles[10][1].setEvilType("none");
					showQTE = false;
					stageTwoComplete = true;
				}
			}
		}

		else if (state == 3) {

			if (!gameOver && !levelComplete) {

				for(int i = 0; i < xTiles; i++) {
					for (int j = 0; j < yTiles; j++) {
						Tile t = tiles[i][j];
						if (t.type() == 0 && t.getEvilType().equals("snake")) {
							showQTE = true;
							if (newQTE) {
								int q = random.nextInt(22);
								qteKey = qteKeys[q];
								qteLetter = qteLetters[q];
								newQTE = false;
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

				if (!showQTE)
					movement();

				if (Gdx.input.isKeyPressed(Controls.SWING) && !playAnimation && !spaceDown && !showQTE && (((limitedSwings && numSwings > 0) || !limitedSwings) || portalClear())) {
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

			}
			
			else if (gameOver) {
				Assets.bgSong.stop();
				if (Gdx.input.isKeyPressed(Controls.ENTER) && !enterDown) level.resetLevel();
			}

			else if (levelComplete) {
				Assets.bgSong.stop();
				if (Gdx.input.isKeyPressed(Controls.ENTER) && !enterDown) level.nextLevel();
			}

		}

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

	public void drawHUD(SpriteBatch batch, int stage) {
		if (stage == 2) {
			hud.setBounds(0, Main.height/2 + 77, Main.width, 100);
			hud.setColor(0.5f, 0, 1, 0.5f);
			hud.draw(batch);
			Assets.font.setScale(0.6f);
			Assets.font.draw(batch, "Press [E]", Main.width/2 - Assets.font.getBounds("Press [E]").width/2, Main.height/2 + 150);
			Assets.font.setScale(0.3f);
		}
		
		else if (stage == 3) {
			if (gameOver) {
				hud.setBounds(0, Main.height/2 - 112, Main.width, 200);
				hud.setColor(0.1f, 0.1f, 0.1f, 0.75f);
				hud.draw(batch);
				Assets.font.draw(batch, gameOverMessage, Main.width/2 - (Assets.font.getBounds(gameOverMessage).width/2), Main.height/2 + 50);
				Assets.font.draw(batch, "Press [Enter] to try again", Main.width/2 - (Assets.font.getBounds("Press Enter to try again").width/2), Main.height/2 - 50);
			}
			else if (levelComplete) {
				hud.setBounds(0, Main.height/2 - 112, Main.width, 200);
				hud.setColor(0.1f, 0.1f, 0.1f, 0.75f);
				hud.draw(batch);
				Assets.font.draw(batch, "Tutorial Complete!", Main.width/2 - (Assets.font.getBounds("Tutorial Complete!").width/2), Main.height/2 + 50);
				Assets.font.draw(batch, "Press [Enter] to continue", Main.width/2 - (Assets.font.getBounds("Press [Enter] to continue").width/2), Main.height/2 - 50);
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
	}

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
						hit = true;
						break;
					}
				}
				if (hit) break;
			}
		}
		return canMove;
	}

	private void movement() {
		if (Gdx.input.isKeyPressed(Controls.UP) && TimeUtils.nanoTime() - timeSinceMove > moveSpeed) {
			rotation = 90;
			if (y < yTiles*128 - 128) {
				if (canMove("up")) {
					y += 128;
					pos.y = y;
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
			}
			timeSinceMove = TimeUtils.nanoTime();
		}
	}

	private void swing() {
		spaceDown = true;
		boolean hit = false;
		Tile t = null;
		for(int i = 0; i < xTiles; i++) {
			for (int j = 0; j < yTiles; j++) {
				t = tiles[i][j];
				if (rotation == 90 && t.getPosition().y == this.y + 128 && t.getPosition().x == this.x && !hit) {
					if ((t.type() == 1 || t.type() == 2) && ((limitedSwings && numSwings > 0) || !limitedSwings)) {
						if (t.type() == 1 && t.getEvilType().equals("snake"))
							timeSinceSnakeFound = ((TimeUtils.nanoTime()/1000)/1000);
						killGrass(t);
						hit = true;
					}
					else if (t.type() == -1) {
						levelComplete = true;
					}
					break;
				}
				else if (rotation == 270 && t.getPosition().y + 128 == this.y && t.getPosition().x == this.x && !hit) {
					if ((t.type() == 1 || t.type() == 2) && ((limitedSwings && numSwings > 0) || !limitedSwings)) {
						if (t.type() == 1 && t.getEvilType().equals("snake"))
							timeSinceSnakeFound = ((TimeUtils.nanoTime()/1000)/1000);
						killGrass(t);
						hit = true;
					}
					else if (t.type() == -1) {
						levelComplete = true;
					}
					break;
				}
				else if (rotation == 180 && t.getPosition().y == this.y && t.getPosition().x + 128 == this.x && !hit) {
					if ((t.type() == 1 || t.type() == 2) && ((limitedSwings && numSwings > 0) || !limitedSwings)) {
						if (t.type() == 1 && t.getEvilType().equals("snake"))
							timeSinceSnakeFound = ((TimeUtils.nanoTime()/1000)/1000);
						killGrass(t);
						hit = true;
					}
					else if (t.type() == -1) {
						levelComplete = true;
					}
					break;
				}
				else if (rotation == 0 && t.getPosition().y == this.y && t.getPosition().x == this.x + 128 && !hit) {
					if ((t.type() == 1 || t.type() == 2) && ((limitedSwings && numSwings > 0) || !limitedSwings)) {
						if (t.type() == 1 && t.getEvilType().equals("snake"))
							timeSinceSnakeFound = ((TimeUtils.nanoTime()/1000)/1000);
						killGrass(t);
						hit = true;
					}
					else if (t.type() == -1) {
						levelComplete = true;
					}
					break;
				}
			}
			if (hit) break;
		}
	}

	private void killGrass(final Tile t) {
		t.getHit();
		if (limitedSwings)
			numSwings--;
	}

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