package cs.games.hng;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import cs.games.hng.utils.Animator;

public class Assets {

	// Primary texture atlas containing all of the sprites for the game
	public static TextureAtlas atlas = new TextureAtlas("assets.txt");

	public static Sprite logo = atlas.createSprite("MenuHeader");  // Main Logo
	public static Sprite player = atlas.createSprite("player");    // Static player
	public static Sprite black = atlas.createSprite("black");      // Black square used only in fading between screens
	public static Sprite bg = atlas.createSprite("bg");            // Background sprite that is underneath each tile in the game
	public static Sprite blade = atlas.createSprite("single");     // "Single-hit" grass that makes up the primary obstacle
	public static Sprite bush = atlas.createSprite("double");      // "Double-hit" grass that is a "thicker" version of the single-hit grass, randomly scattered
	public static Sprite rock = atlas.createSprite("rock");        // The rock, an obstacle that could be hiding anywhere and prevents movement
	public static Sprite snake = atlas.createSprite("snake");      // The snake, an obstacle that required immediate reaction or will result in the "death" of the player
	public static Sprite portal = atlas.createSprite("portal");    // The portal, the goal of each level
	public static Sprite hole = atlas.createSprite("hole");        // A hole, a type of evil tile that sends the player to the front of the level
	
	public static Sprite switchOn = atlas.createSprite("switchOn");
	public static Sprite switchOff = atlas.createSprite("switchOff");
	
	// Switches
	public static Sprite redSwitchOn = atlas.createSprite("redSwitchOn");
	public static Sprite redSwitchOff = atlas.createSprite("redSwitchOff");
	public static Sprite blueSwitchOn = atlas.createSprite("blueSwitchOn");
	public static Sprite blueSwitchOff = atlas.createSprite("blueSwitchOff");
	public static Sprite orangeSwitchOn = atlas.createSprite("orangeSwitchOn");
	public static Sprite orangeSwitchOff = atlas.createSprite("orangeSwitchOff");
	public static Sprite purpleSwitchOn = atlas.createSprite("purpleSwitchOn");
	public static Sprite purpleSwitchOff = atlas.createSprite("purpleSwitchOff");
	public static Sprite yellowSwitchOn = atlas.createSprite("yellowSwitchOn");
	public static Sprite yellowSwitchOff = atlas.createSprite("yellowSwitchOff");
	public static Sprite greenSwitchOn = atlas.createSprite("greenSwitchOn");
	public static Sprite greenSwitchOff = atlas.createSprite("greenSwitchOff");
	
	// Switch Doors
	public static Sprite redDoorH = atlas.createSprite("redH");
	public static Sprite redDoorV = atlas.createSprite("redV");
	public static Sprite blueDoorH = atlas.createSprite("blueH");
	public static Sprite blueDoorV = atlas.createSprite("blueV");
	public static Sprite orangeDoorH = atlas.createSprite("orangeH");
	public static Sprite orangeDoorV = atlas.createSprite("orangeV");
	public static Sprite purpleDoorH = atlas.createSprite("purpleH");
	public static Sprite purpleDoorV = atlas.createSprite("purpleV");
	public static Sprite yellowDoorH = atlas.createSprite("yellowH");
	public static Sprite yellowDoorV = atlas.createSprite("yellowV");
	public static Sprite greenDoorH = atlas.createSprite("greenH");
	public static Sprite greenDoorV = atlas.createSprite("greenV");
	
	/*
	// Door walls
	public static Sprite redWallTop = atlas.createSprite("redWallTop");
	public static Sprite redWallBottom = atlas.createSprite("redWallBottom");
	public static Sprite redWallLeft = atlas.createSprite("redWallLeft");
	public static Sprite redWallRight = atlas.createSprite("redWallRight");
	
	public static Sprite blueWallTop = atlas.createSprite("blueWallTop");
	public static Sprite blueWallBottom = atlas.createSprite("blueWallBottom");
	public static Sprite blueWallLeft = atlas.createSprite("blueWallLeft");
	public static Sprite blueWallRight = atlas.createSprite("blueWallRight");
	
	public static Sprite orangeWallTop = atlas.createSprite("orangeWallTop");
	public static Sprite orangeWallBottom = atlas.createSprite("orangeWallBottom");
	public static Sprite orangeWallLeft = atlas.createSprite("orangeWallLeft");
	public static Sprite orangeWallRight = atlas.createSprite("orangeWallRight");
	
	public static Sprite purpleWallTop = atlas.createSprite("purpleWallTop");
	public static Sprite purpleWallBottom = atlas.createSprite("purpleWallBottom");
	public static Sprite purpleWallLeft = atlas.createSprite("purpleWallLeft");
	public static Sprite purpleWallRight = atlas.createSprite("purpleWallRight");
	
	public static Sprite yellowWallTop = atlas.createSprite("yellowWallTop");
	public static Sprite yellowWallBottom = atlas.createSprite("yellowWallBottom");
	public static Sprite yellowWallLeft = atlas.createSprite("yellowWallLeft");
	public static Sprite yellowWallRight = atlas.createSprite("yellowWallRight");
	
	public static Sprite greenWallTop = atlas.createSprite("greenWallTop");
	public static Sprite greenWallBottom = atlas.createSprite("greenWallBottom");
	public static Sprite greenWallLeft = atlas.createSprite("greenWallLeft");
	public static Sprite greenWallRight = atlas.createSprite("greenWallRight");
	*/

	// Fence Corners
	public static Sprite fenceTL = atlas.createSprite("fenceTL");
	public static Sprite fenceTR = atlas.createSprite("fenceTR");
	public static Sprite fenceBL = atlas.createSprite("fenceBL");
	public static Sprite fenceBR = atlas.createSprite("fenceBR");

	// Fences
	public static Sprite fenceVerticalLeft = atlas.createSprite("fenceVleft");
	public static Sprite fenceVerticalRight = atlas.createSprite("fenceVright");
	public static Sprite fenceHorizontalTop = atlas.createSprite("fenceHtop");
	public static Sprite fenceHorizontalBottom = atlas.createSprite("fenceHbottom");

	// Fence Posts
	public static Sprite postVerticalLeft = atlas.createSprite("postVleft");
	public static Sprite postVerticalRight = atlas.createSprite("postVright");
	public static Sprite postHorizontalTop = atlas.createSprite("postHtop");
	public static Sprite postHorizontalBottom = atlas.createSprite("postHbottom");

	// Fence ends which are solely used for fence placement next to a maze wall
	public static Sprite fenceLeftEnd = atlas.createSprite("fenceEndLeft");
	public static Sprite fenceRightEnd = atlas.createSprite("fenceEndRight");

	// Maze Walls
	public static Sprite mazeHorizontal = atlas.createSprite("mazeH");
	public static Sprite mazeVertical = atlas.createSprite("mazeV");
	
	// Maze Specials
	public static Sprite tTop = atlas.createSprite("tTop");
	public static Sprite tBottom = atlas.createSprite("tDown");
	public static Sprite tLeft = atlas.createSprite("tLeft");
	public static Sprite tRight = atlas.createSprite("tRight");
	public static Sprite wallEndLeft = atlas.createSprite("wallEndLeft");
	public static Sprite wallEndRight = atlas.createSprite("wallEndRight");
	public static Sprite wallEndTop = atlas.createSprite("wallEndTop");
	public static Sprite wallEndBottom = atlas.createSprite("wallEndBottom");
	
	// Maze Corners
	public static Sprite mazeBL = atlas.createSprite("mazeBL");
	public static Sprite mazeTL = atlas.createSprite("mazeTL");
	public static Sprite mazeBR = atlas.createSprite("mazeBR");
	public static Sprite mazeTR = atlas.createSprite("mazeTR");

	// Animated portal
	public static Animator portalAnimation = new Animator("portalAnimate.png", 0.25f, true);
	
	// Animated grass when cut
	public static Animator grassAnimation = new Animator("grassAnimate.png", 0.04f, false);

	// Background used for scrolling on any main menu
	public static Sprite mainBGlong = new Sprite(new Texture("mainbg.png"));

	// Splash logo
	public static Sprite csLogo = new Sprite(new Texture("CS.png"));

	// Font used outside of the GUI
	public static BitmapFont font = new BitmapFont(Gdx.files.internal("font.fnt"), false);

	// Background music and its volume
	public static float musicVolume = 0.0f;
	public static Music bgSong = Gdx.audio.newMusic(Gdx.files.internal("audio/bgmusic.ogg"));

	// Volume of any sound effects
	public static float effectVolume = 0.3f;
	public static Music splashSound = Gdx.audio.newMusic(Gdx.files.internal("audio/splash.ogg"));
	public static Music grassCut = Gdx.audio.newMusic(Gdx.files.internal("audio/grasscut.ogg"));
	public static Music levelComplete = Gdx.audio.newMusic(Gdx.files.internal("audio/levelcomplete.ogg"));
	public static Music portalSound = Gdx.audio.newMusic(Gdx.files.internal("audio/portalvibration.ogg"));
	public static Music rockHit = Gdx.audio.newMusic(Gdx.files.internal("audio/rockhit.ogg"));
	public static Music snakeHiss = Gdx.audio.newMusic(Gdx.files.internal("audio/snakehiss.ogg"));
	public static Music gameStart = Gdx.audio.newMusic(Gdx.files.internal("audio/gamestart.ogg"));
	public static Music tutorialStart = Gdx.audio.newMusic(Gdx.files.internal("audio/tutorialclick.ogg"));
	
	public static void setupSounds() {
		grassCut.setVolume(effectVolume);
		levelComplete.setVolume(effectVolume);
		portalSound.setVolume(effectVolume);
		rockHit.setVolume(effectVolume);
		snakeHiss.setVolume(effectVolume);
		gameStart.setVolume(effectVolume);
		tutorialStart.setVolume(effectVolume);
		
		grassCut.setLooping(false);
		levelComplete.setLooping(false);
		portalSound.setLooping(false);
		rockHit.setLooping(false);
		snakeHiss.setLooping(false);
		gameStart.setLooping(false);
		tutorialStart.setLooping(false);
	}

}
