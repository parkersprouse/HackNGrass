/*
package cs.games.hng.utils;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import com.badlogic.gdx.Input.Keys;

import cs.games.hng.Assets;
import cs.games.hng.Controls;

public class PreferenceManager {

	private static FileOutputStream fos = null;
	private static FileInputStream fis = null;
	private static ObjectOutputStream oos = null;
	private static ObjectInputStream ois = null;
	private static File file = null;
	
	public static void createPreferences() {
		try {
			file = new File("savedata.dat");
			fos = new FileOutputStream(file);
			fis = new FileInputStream(file);
			oos = new ObjectOutputStream(fos);
			ois = new ObjectInputStream(fis);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void savePreferences() {
		createPreferences();
	
		// Format is as follows:
		// -Music Volume
		// -Sound Volume
		// -Left Keycode
		// -Right Keycode
		// -Up Keycode
		// -Down Keycode
		// -Swing Keycode
	
		try {
			/*oos.writeFloat(Assets.musicVolume);
			oos.writeFloat(Assets.effectVolume);
			oos.writeInt(Controls.LEFT);
			oos.writeInt(Controls.RIGHT);
			oos.writeInt(Controls.UP);
			oos.writeInt(Controls.DOWN);
			oos.writeInt(Controls.SWING);
			oos.flush();
			oos.close();
			/
			
			System.out.println(Assets.musicVolume);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		
		Assets.setupSounds();
	}
	
	public static void loadPreferences() {
		createPreferences();
			
		try {
			while (ois.available() > 0) {
				Assets.musicVolume = ois.readFloat();
				Assets.effectVolume = ois.readFloat();
				Controls.LEFT = ois.readInt();
				Controls.RIGHT = ois.readInt();
				Controls.UP = ois.readInt();
				Controls.DOWN = ois.readInt();
				Controls.SWING = ois.readInt();
				
				System.out.println(Assets.musicVolume);
			}
		}
		catch(Exception e) {
			Assets.musicVolume = 0.5f;
			Assets.effectVolume = 0.5f;
			Controls.LEFT = Keys.A;
			Controls.RIGHT = Keys.D;
			Controls.UP = Keys.W;
			Controls.DOWN = Keys.S;
			Controls.SWING = Keys.SPACE;
		}
	
		Assets.setupSounds();
		
		try {ois.close();}
		catch (IOException e) {e.printStackTrace();}
	}

}
*/

package cs.games.hng.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;

import cs.games.hng.Assets;
import cs.games.hng.Controls;

public class PreferenceManager {

	private static Preferences pref;
	
	public static void createPrefences() {
		pref = Gdx.app.getPreferences("HackNGrass");
	}
	
	public static void savePreferences() {
		pref.putFloat("musicVolume", Assets.musicVolume);
		pref.putFloat("soundVolume", Assets.effectVolume);
		pref.putInteger("left", Controls.LEFT);
		pref.putInteger("right", Controls.RIGHT);
		pref.putInteger("up", Controls.UP);
		pref.putInteger("down", Controls.DOWN);
		pref.putInteger("swing", Controls.SWING);
		pref.flush();
		Assets.setupSounds();
	}
	
	public static void loadPreferences() {
		Assets.musicVolume = pref.getFloat("musicVolume", 0.1f);
		if (Assets.musicVolume > 1)
			Assets.musicVolume = 1;
		else if (Assets.musicVolume < 0)
			Assets.musicVolume = 0;
		
		Assets.effectVolume = pref.getFloat("soundVolume", 0.3f);
		if (Assets.effectVolume > 1)
			Assets.effectVolume = 1;
		else if (Assets.effectVolume < 0)
			Assets.effectVolume = 0;
		
		Controls.LEFT = pref.getInteger("left", Keys.A);
		Controls.RIGHT = pref.getInteger("right", Keys.D);
		Controls.UP = pref.getInteger("up", Keys.W);
		Controls.DOWN = pref.getInteger("down", Keys.S);
		Controls.SWING = pref.getInteger("swing", Keys.SPACE);
		
		Assets.setupSounds();
	}
	
}