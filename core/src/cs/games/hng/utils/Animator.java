package cs.games.hng.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Animator {

	private int FRAME_COLS;
	private int FRAME_ROWS;

	private Animation walkAnimation;
	private Texture walkSheet;
	private TextureRegion[] walkFrames;
	private TextureRegion currentFrame;
	private boolean loop;

	private float stateTime;

	public Animator(String texture, float speed, boolean loop) {
		walkSheet = new Texture(Gdx.files.internal(texture));
		FRAME_COLS = walkSheet.getWidth()/128;
		FRAME_ROWS = walkSheet.getHeight()/128;
		TextureRegion[][] tmp = TextureRegion.split(walkSheet, walkSheet.getWidth()/FRAME_COLS, walkSheet.getHeight()/FRAME_ROWS);
		walkFrames = new TextureRegion[FRAME_ROWS * FRAME_COLS];
		int index = 0;
		for (int i = 0; i < FRAME_ROWS; i++)
			for (int j = 0; j < FRAME_COLS; j++)
				walkFrames[index++] = tmp[i][j];
		walkAnimation = new Animation(speed, walkFrames);
		currentFrame = walkFrames[0];
		stateTime = 0f;
		this.loop = loop;
	}
	
	public TextureRegion getFrame(int frame) {
		return walkFrames[frame];
	}
	
	public TextureRegion getAnimation() {
		stateTime += Gdx.graphics.getDeltaTime();
		currentFrame = walkAnimation.getKeyFrame(stateTime, loop);
		return currentFrame;
	}
	
	public TextureRegion getCurrentFrame() {
		return currentFrame;
	}
	
	public void resetAnimation() {
		stateTime = 0f;
		currentFrame = walkFrames[0];
	}
	
	public void setLooping(boolean loop) {
		this.loop = loop;
	}
	
	public int getLength() {
		return walkFrames.length;
	}

}