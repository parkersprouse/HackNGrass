package cs.games.hng.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import cs.games.hng.Main;
import cs.games.hng.Player;
import cs.games.hng.Tile;
import cs.games.hng.levels.Level;

/*
 * Was used when levels were read in from files,
 * but since that doesn't happen anymore,
 * this file in unused.
 */

public class LevelFileReader {

	private Scanner scanner;
	private Tile[][] tiles;
	private boolean useDefaultSetup;
	private ArrayList<String> fromFile;

	public LevelFileReader(String file) {
		File filepath = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
		String path = filepath.getParentFile().getAbsolutePath();
		File levelFile = new File(path + "\\" + file);
		fromFile = new ArrayList<String>();
		useDefaultSetup = false;
		try {
			this.scanner = new Scanner(levelFile);
		} catch (Exception e) {
			useDefaultSetup = true;
		}
		tiles = new Tile[16][12];
	}

	public Tile[][] createMap() {

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 12; j++) {
				tiles[i][j] = new Tile(1, i*128, j*128);
			}
		}

		if (!useDefaultSetup) {

			try {

				while (scanner.hasNextLine())
					fromFile.add(scanner.nextLine());

				// Thick grass
				for (int i = fromFile.indexOf("thick"); i < fromFile.size(); i++) {
					int j = i + 1;
					String s = fromFile.get(j);
					while (!s.equals("snake") && !s.equals("rock") && !s.equals("portal") && !s.equals("numswings") && !s.equals("player")) {
						int x = Integer.parseInt(s.substring(0, s.indexOf(" ")));
						int y = Integer.parseInt(s.substring(s.indexOf(" ") + 1, s.length()));
						tiles[x][y].setType(2);
						tiles[x][y].setEvilType("none");
						j++;
						s = fromFile.get(j);
					}
					if (s.equals("snake") || s.equals("rock") || s.equals("portal") || s.equals("numswings") || s.equals("player")) break;
				}

				// Snakes
				for (int i = fromFile.indexOf("snake"); i < fromFile.size(); i++) {
					int j = i + 1;
					String s = fromFile.get(j);
					while (!s.equals("thick") && !s.equals("rock") && !s.equals("portal") && !s.equals("numswings") && !s.equals("player")) {
						int x = Integer.parseInt(s.substring(0, s.indexOf(" ")));
						int y = Integer.parseInt(s.substring(s.indexOf(" ") + 1, s.length()));
						tiles[x][y].setEvilType("snake");
						j++;
						s = fromFile.get(j);
					}
					if (s.equals("thick") || s.equals("rock") || s.equals("portal") || s.equals("numswings") || s.equals("player")) break;
				}

				// Rocks
				for (int i = fromFile.indexOf("rock"); i < fromFile.size(); i++) {
					int j = i + 1;
					String s = fromFile.get(j);
					while (!s.equals("thick") && !s.equals("snake") && !s.equals("portal") && !s.equals("numswings") && !s.equals("player")) {
						int x = Integer.parseInt(s.substring(0, s.indexOf(" ")));
						int y = Integer.parseInt(s.substring(s.indexOf(" ") + 1, s.length()));
						tiles[x][y].setEvilType("rock");
						j++;
						s = fromFile.get(j);
					}
					if (s.equals("thick") || s.equals("snake") || s.equals("portal") || s.equals("numswings") || s.equals("player")) break;
				}

				// Portal
				for (int i = fromFile.indexOf("portal"); i < fromFile.size(); i++) {
					int j = i + 1;
					String s = fromFile.get(j);
					while (!s.equals("thick") && !s.equals("snake") && !s.equals("rock") && !s.equals("numswings") && !s.equals("player")) {
						int x = Integer.parseInt(s.substring(0, s.indexOf(" ")));
						int y = Integer.parseInt(s.substring(s.indexOf(" ") + 1, s.length()));
						tiles[x][y].setType(-1);
						j++;
						s = fromFile.get(j);
					}
					if (s.equals("thick") || s.equals("snake") || s.equals("rock") || s.equals("numswings") || s.equals("player")) break;
				}

			}

			catch (Exception e) {
				for (int i = 0; i < 16; i++) {
					for (int j = 0; j < 12; j++) {
						tiles[i][j] = new Tile(1, i*128, j*128);
					}
				}
				return tiles;
			}
		}

		return tiles;

	}

	
	public Player setPlayer(final Main game, Level level, int numSwings) {
		Player p;
		
		try {
			int i = fromFile.indexOf("player");
			String coord = fromFile.get(i + 1);
			int xtile = Integer.parseInt(coord.substring(0, coord.indexOf(" ")));
			int ytile = Integer.parseInt(coord.substring(coord.indexOf(" ") + 1, coord.length()));
			
			int xpos = (int) tiles[xtile][ytile].getPosition().x;
			int ypos = (int) tiles[xtile][ytile].getPosition().y;
			
			p = new Player(level, numSwings, xpos, ypos);
		}
		catch(Exception e) {
			p = new Player(level, numSwings, 1024, 0);
		}
		
		return p;
	}
	

	public int setNumSwings() {
		int numSwings = 30;

		try {
			int i = fromFile.indexOf("numswings");
			numSwings = Integer.parseInt(fromFile.get(i + 1));
		}
		catch (Exception e) {
			return numSwings;
		}

		return numSwings;
	}

}
