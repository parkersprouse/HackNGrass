package cs.games.hng.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import cs.games.hng.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.resizable = false;
		config.fullscreen = false;
		config.title = "Hack N' Grass";
		config.height = 768;
		config.width = 1024;
		config.addIcon("icon.png", Files.FileType.Internal);
		new LwjglApplication(new Main(), config);
	}
}
