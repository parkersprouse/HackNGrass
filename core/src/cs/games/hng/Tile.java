package cs.games.hng;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Tile {

	private int type, x, y;
	private Vector2 pos;
	private Sprite sprite;
	private String typeOfEvil;
	private String switchColor;
	private boolean hiddenSwitch = false;

	public Tile() {
		this(0, 0, 0);
	}

	public Tile(int type, int x, int y) {
		this.type = type;
		this.x = x;
		this.y = y;
		pos = new Vector2(x, y);
		typeOfEvil = "none";
		switchColor = "";
		updateTileSprite();
	}

	public void draw(SpriteBatch batch) {
		if (type == -10) 
			batch.draw(Assets.grassAnimation.getAnimation(), x, y);

		else if (type == -1)
			batch.draw(Assets.portalAnimation.getAnimation(), x, y);
			
		else 
            batch.draw(sprite, x, y);
	}

	public void drawPaused(SpriteBatch batch) {        
        if (type == -10) 
			batch.draw(Assets.grassAnimation.getCurrentFrame(), x, y);

		else if (type == -1)
			batch.draw(Assets.portalAnimation.getCurrentFrame(), x, y);
			
		else
            batch.draw(sprite, x, y);
	}

	public void getHit() {
		if (type == -10 && hiddenSwitch) {
			type = -2;
			hiddenSwitch = false;
		}

		if (type == 2)
			type = 1;
		else if ((type == 1 || type == -10) && !hiddenSwitch)
			type = 0;
			
		updateTileSprite();
	}

	public Vector2 getPosition() {
		return pos;
	}

	public void setHiddenSwitch(boolean h) {
		hiddenSwitch = h;
		updateTileSprite();
	}

	public boolean isHiddenSwitch() {
		return hiddenSwitch;
	}

	public void setType(int t) {
		type = t;
		updateTileSprite();
	}

	public int type() {
		return type;
	}

	public void setSwitchColor(String c) {
		switchColor = c;
		updateTileSprite();
	}

	public String getSwitchColor() {
		return switchColor;
	}

	public void setEvilType(String type) {
		typeOfEvil = type;
		updateTileSprite();
	}

	public String getEvilType() {
		return typeOfEvil;
	}
	
	private void updateTileSprite() {
        if (type == 2)
			sprite = Assets.bush;
		else if (type == 1)
			sprite = Assets.blade;
		else if (type == 0) {
			if (typeOfEvil.equals("none"))
				sprite = Assets.bg;
			else if (typeOfEvil.equals("rock"))
				sprite = Assets.rock;
			else if (typeOfEvil.equals("snake"))
				sprite = Assets.snake;
			else if (typeOfEvil.equals("hole"))
				sprite = Assets.hole;
		}

		else if (type == -2) {
			if (!hiddenSwitch) {
				if (switchColor.equals("red"))
					sprite = Assets.redSwitchOff;
				else if (switchColor.equals("blue"))
					sprite = Assets.blueSwitchOff;
				else if (switchColor.equals("orange"))
					sprite = Assets.orangeSwitchOff;
				else if (switchColor.equals("purple"))
					sprite = Assets.purpleSwitchOff;
				else if (switchColor.equals("yellow"))
					sprite = Assets.yellowSwitchOff;
				else if (switchColor.equals("green"))
					sprite = Assets.greenSwitchOff;
			}
			else
				sprite = Assets.blade;
		}
		else if (type == -3) {
			if (switchColor.equals("red"))
				sprite = Assets.redSwitchOn;
			else if (switchColor.equals("blue"))
				sprite = Assets.blueSwitchOn;
			else if (switchColor.equals("orange"))
				sprite = Assets.orangeSwitchOn;
			else if (switchColor.equals("purple"))
				sprite = Assets.purpleSwitchOn;
			else if (switchColor.equals("yellow"))
				sprite = Assets.yellowSwitchOn;
			else if (switchColor.equals("green"))
				sprite = Assets.greenSwitchOn;
			System.out.println("switch activated");
		}
        
		else if (type == -20)
			sprite = Assets.redDoorH;
		else if (type == -21)
			sprite = Assets.redDoorV;
		else if (type == -22)
			sprite = Assets.blueDoorH;
		else if (type == -23)
			sprite = Assets.blueDoorV;
		else if (type == -24)
			sprite = Assets.orangeDoorH;
		else if (type == -25)
			sprite = Assets.orangeDoorV;
		else if (type == -26)
			sprite = Assets.purpleDoorH;
		else if (type == -27)
			sprite = Assets.purpleDoorV;
		else if (type == -28)
			sprite = Assets.yellowDoorH;
		else if (type == -29)
			sprite = Assets.yellowDoorV;
		else if (type == -30)
			sprite = Assets.greenDoorH;
		else if (type == -31)
			sprite = Assets.greenDoorV;

		else if (type == 10)
			sprite = Assets.fenceVerticalLeft;
		else if (type == 11)
			sprite = Assets.fenceVerticalRight;
		else if (type == 12)
			sprite = Assets.fenceHorizontalTop;
		else if (type == 13)
			sprite = Assets.fenceHorizontalBottom;
		else if (type == 14)
			sprite = Assets.fenceTL;
		else if (type == 15)
			sprite = Assets.fenceTR;
		else if (type == 16)
			sprite = Assets.fenceBL;
		else if (type == 17)
			sprite = Assets.fenceBR;
		else if (type == 18)
			sprite = Assets.postVerticalLeft;
		else if (type == 19)
			sprite = Assets.postVerticalRight;
		else if (type == 20)
			sprite = Assets.postHorizontalTop;
		else if (type == 21)
			sprite = Assets.postHorizontalBottom;
		else if (type == 22)
			sprite = Assets.fenceLeftEnd;
		else if (type == 23)
			sprite = Assets.fenceRightEnd;

		else if (type == 30)
			sprite = Assets.mazeVertical;
		else if (type == 31)
			sprite = Assets.mazeHorizontal;
		else if (type == 32)
			sprite = Assets.mazeTL;
		else if (type == 33)
			sprite = Assets.mazeTR;
		else if (type == 34)
			sprite = Assets.mazeBL;
		else if (type == 35)
			sprite = Assets.mazeBR;

		else if (type == 40) 
			sprite = Assets.tTop;
		else if (type == 41) 
			sprite = Assets.tBottom;
		else if (type == 42) 
			sprite = Assets.tLeft;
		else if (type == 43) 
			sprite = Assets.tRight;
		else if (type == 44) 
			sprite = Assets.wallEndTop;
		else if (type == 45) 
			sprite = Assets.wallEndBottom;
		else if (type == 46) 
			sprite = Assets.wallEndLeft;
		else if (type == 47) 
			sprite = Assets.wallEndRight;
	}

}
