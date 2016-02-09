package cs.games.hng.utils;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import cs.games.hng.Player;
import cs.games.hng.Tile;

public class MapGenerator {

	private static Random rand = new Random();
	private static int numSwitches = 0;

	// Switches/Triggers 1 = red
	// Switches/Triggers 2 = blue
	// Switches/Triggers 3 = orange
	// Switches/Triggers 4 = purple
	// Switches/Triggers 5 = yellow
	// Switches/Triggers 6 = green
	public static ArrayList<Tile> switches1, switches2, switches3, switches4, switches5, switches6;
	public static ArrayList<Tile> triggers1, triggers2, triggers3, triggers4, triggers5, triggers6;

	public static Tile[][] generateRandomMap(Player player, int xSize, int ySize, int maxDoubles, int maxSnakes, int maxRocks) {
		Tile[][] tiles = new Tile[xSize][ySize];
		int currentDoubles = 0;
		while (true) {
			for (int i = 0; i < xSize; i++) {
				for (int j = 0; j < ySize; j++) {
					Tile t;
					if (rand.nextInt(50) == 1 && currentDoubles < maxDoubles) {
						t = new Tile(2, i*128, j*128);
						currentDoubles++;
					}
					else
						if (tiles[i][j] == null)
							t = new Tile(1, i*128, j*128);
						else if (tiles[i][j].type() == 2)
							t = new Tile(2, i*128, j*128);
						else
							t = new Tile(1, i*128, j*128);
					tiles[i][j] = t;
				}
			}
			if (currentDoubles >= maxDoubles)
				break;
		}


		// Spawn rocks
		int currentRocks = 0;
		while (true) {
			for (int i = 0; i < xSize; i++) {
				for (int j = 0; j < ySize; j++) {
					Tile t = tiles[i][j];
					if (rand.nextInt(50) == 1 
							&& currentRocks < maxRocks 
							&& t.getEvilType().equals("none") 
							&& t.getPosition().x != player.getPosition().x 
							&& t.getPosition().y != player.getPosition().y) {
						t.setEvilType("rock");
						currentRocks++;
					}
				}
			}
			if (currentRocks >= maxRocks)
				break;
		}


		// Spawn snakes
		int currentSnakes = 0;
		while (true) {
			for (int i = 0; i < xSize; i++) {
				for (int j = 0; j < ySize; j++) {
					Tile t = tiles[i][j];
					if (rand.nextInt(50) == 1 
							&& currentSnakes < maxSnakes 
							&& t.getEvilType().equals("none") 
							&& t.getPosition().x != player.getPosition().x 
							&& t.getPosition().y != player.getPosition().y) {
						t.setEvilType("snake");
						currentSnakes++;
					}
				}
			}
			if (currentSnakes >= maxSnakes)
				break;
		}


		// We don't want the player spawning on top of an obstacle
		boolean typeSet = false;
		for (int i = 0; i < xSize; i++) {
			for (int j = 0; j < ySize; j++) {
				Tile t = tiles[i][j];
				if (t.getPosition().x == player.getPosition().x && t.getPosition().y == player.getPosition().y) {
					t.setType(0);
					t.setEvilType("none");
					typeSet = true;
					break;
				}
			}
			if (typeSet) break;
		}

		// randomly position the portal
		// array starts at the bottom left corner
		/*while (true) {
			int px = rand.nextInt(xSize - 2) + 1;
			int py = rand.nextInt(ySize - 2) + 1;
			if (px != (int)(player.getPosition().x/128) && py != (int)(player.getPosition().y/128)) {
				tiles[px][py] = new Tile(-1, 128*px, 128*py);
				break;
			}
		}*/

		// Creation of the surrounding fence
		for (int i = 0; i < ySize; i++) {
			tiles[0][i].setType(10);
			tiles[xSize-1][i].setType(11);
		}
		for (int i = 0; i < xSize; i++) {
			tiles[i][0].setType(13);
			tiles[i][ySize-1].setType(12);
		}

		// Posts
		tiles[0][4].setType(18);
		tiles[xSize-1][4].setType(19);
		tiles[4][0].setType(21);
		tiles[4][ySize-1].setType(20);

		// Corners
		tiles[0][0].setType(16);
		tiles[0][ySize-1].setType(14);
		tiles[xSize-1][0].setType(17);
		tiles[xSize-1][ySize-1].setType(15);

		return tiles;
	}

	public static Tile[][] generateTutorialMap(int xSize, int ySize) {
		Tile[][] tiles = new Tile[21][21];

		// Initialize all tiles to single hit grass
		for (int x = 0; x < xSize; x++)
			for (int y = 0; y < ySize; y++)
				tiles[x][y] = new Tile(1, 128*x, 128*y);

		// Creation of the surrounding fence
		for (int i = 0; i < ySize; i++) {
			tiles[0][i].setType(10);
			tiles[xSize-1][i].setType(11);
		}
		for (int i = 0; i < xSize; i++) {
			tiles[i][0].setType(13);
			tiles[i][ySize-1].setType(12);
		}

		// Corners
		tiles[0][0].setType(16);
		tiles[0][ySize-1].setType(14);
		tiles[xSize-1][0].setType(17);
		tiles[xSize-1][ySize-1].setType(15);

		// Fence Posts
		tiles[4][0].setType(21); // Bottom Wall
		tiles[16][0].setType(21);
		tiles[0][4].setType(18); // Left Wall
		tiles[0][8].setType(18);
		tiles[0][12].setType(18);
		tiles[0][16].setType(18);
		tiles[xSize-1][4].setType(19); // Right Wall
		tiles[xSize-1][8].setType(19);
		tiles[xSize-1][12].setType(19);
		tiles[xSize-1][16].setType(19);
		tiles[4][ySize-1].setType(20); // Top Wall
		tiles[8][ySize-1].setType(20);
		tiles[12][ySize-1].setType(20);
		tiles[16][ySize-1].setType(20);

		tiles[7][0].setType(22);
		tiles[13][0].setType(23);

		// Make sure player is not on a fence
		tiles[10][0].setType(0);

		// Replant the grass next to the player
		tiles[9][0].setType(1);
		tiles[11][0].setType(1);

		// Maze setup
		for (int i = 0; i < 6; i++)
			tiles[8][i].setType(30);
		tiles[8][6].setType(33);
		for (int i = 0; i < 6; i++)
			tiles[12][i].setType(30);
		tiles[12][6].setType(32);

		for (int i = 3; i < 8; i++)
			tiles[i][6].setType(31);
		tiles[2][6].setType(34);
		for (int i = 13; i < 18; i++)
			tiles[i][6].setType(31);
		tiles[18][6].setType(35);

		for (int i = 7; i < 18; i++)
			tiles[2][i].setType(30);
		tiles[2][18].setType(32);
		for (int i = 7; i < 18; i++)
			tiles[18][i].setType(30);
		tiles[18][18].setType(33);

		for (int i = 3; i < 18; i++)
			tiles[i][18].setType(31);

		for (int i = 7; i < 14; i++)
			tiles[i][10].setType(31);
		for (int i = 7; i < 14; i++)
			tiles[i][14].setType(31);
		for (int i = 11; i < 14; i++)
			tiles[6][i].setType(30);
		for (int i = 11; i < 14; i++)
			tiles[14][i].setType(30);

		tiles[6][14].setType(32);
		tiles[6][10].setType(34);
		tiles[14][14].setType(33);
		tiles[14][10].setType(35);

		// Portal
		tiles[10][16].setType(-1);

		// First Snake
		tiles[10][1].setEvilType("snake");

		// Other obstacles
		tiles[9][6].setEvilType("snake");
		tiles[11][6].setEvilType("rock");
		tiles[3][10].setEvilType("rock");
		tiles[17][10].setEvilType("rock");
		tiles[16][12].setEvilType("snake");
		tiles[3][15].setEvilType("rock");
		tiles[6][16].setEvilType("snake");
		tiles[11][16].setEvilType("rock");
		tiles[15][16].setEvilType("snake");
		tiles[10][17].setEvilType("snake");		

		return tiles;
	}

	public static Tile[][] generateLevel(String mapFile, int maxSnakes) {
		numSwitches = 0;

		TiledMap map = new TmxMapLoader().load(mapFile);
		TiledMapTileLayer baseLayer = (TiledMapTileLayer)map.getLayers().get("Tile Layer 1");
		TiledMapTileLayer switchLayer = (TiledMapTileLayer)map.getLayers().get("Tile Layer 2");

		int xSize = baseLayer.getWidth();
		int ySize = baseLayer.getHeight();

		Tile[][] tiles = new Tile[xSize][ySize];

		// Initialize all tiles to single hit grass
		for (int x = 0; x < xSize; x++)
			for (int y = 0; y < ySize; y++)
				tiles[x][y] = new Tile(1, 128*x, 128*y);

		// Spread random double hit grass
		int maxDoubles = 60;
		int currentDoubles = 0;
		Tile tile;
		while (true) {
			for (int i = 0; i < xSize; i++) {
				for (int j = 0; j < ySize; j++) {
					if (rand.nextInt(50) == 1 && currentDoubles < maxDoubles) {
						tile = new Tile(2, i*128, j*128);
						currentDoubles++;
					}
					else
						if (tiles[i][j] == null)
							tile = new Tile(1, i*128, j*128);
						else if (tiles[i][j].type() == 2)
							tile = new Tile(2, i*128, j*128);
						else
							tile = new Tile(1, i*128, j*128);
					tiles[i][j] = tile;
				}
			}
			if (currentDoubles >= maxDoubles)
				break;
		}

		// Fence Post Corners
		// Corners will always be these coordinates, so no need to do any special checks
		tiles[0][ySize-1].setType(14);
		tiles[xSize-1][ySize-1].setType(15);
		tiles[0][0].setType(16);
		tiles[xSize-1][0].setType(17);

		Cell c = null;
		TiledMapTile t = null;
		MapProperties p = null;
		for (int i = 0; i < baseLayer.getWidth(); i++) {
			for (int j = 0; j < baseLayer.getHeight(); j++) {
				c = baseLayer.getCell(i, j);
				if (c != null) {
					t = c.getTile();
					p = t.getProperties();
					if (p.containsKey("name")) {
						if (p.get("name").equals("mazeV")) {
							tiles[i][j].setType(30);
						}
						else if (p.get("name").equals("mazeH")) {
							tiles[i][j].setType(31);
						}
						else if (p.get("name").equals("mazeTL")) {
							tiles[i][j].setType(32);
						}
						else if (p.get("name").equals("mazeTR")) {
							tiles[i][j].setType(33);
						}
						else if (p.get("name").equals("mazeBL")) {
							tiles[i][j].setType(34);
						}
						else if (p.get("name").equals("mazeBR")) {
							tiles[i][j].setType(35);
						}
						else if (p.get("name").equals("snake")) {
							tiles[i][j].setEvilType("snake");
						}
						else if (p.get("name").equals("rock")) {
							tiles[i][j].setEvilType("rock");
						}
						else if (p.get("name").equals("postR")) {
							tiles[i][j].setType(19);
						}
						else if (p.get("name").equals("postL")) {
							tiles[i][j].setType(18);
						}
						else if (p.get("name").equals("postT")) {
							tiles[i][j].setType(20);
						}
						else if (p.get("name").equals("postB")) {
							tiles[i][j].setType(21);
						}
						else if (p.get("name").equals("portal")) {
							tiles[i][j].setType(-1);
						}
						else if (p.get("name").equals("player")) {
							tiles[i][j].setType(0);
							tiles[i][j].setEvilType("none");
						}
						else if (p.get("name").equals("fenceL")) {
							tiles[i][j].setType(10);
						}
						else if (p.get("name").equals("fenceR")) {
							tiles[i][j].setType(11);
						}
						else if (p.get("name").equals("fenceT")) {
							tiles[i][j].setType(12);
						}
						else if (p.get("name").equals("fenceB")) {
							tiles[i][j].setType(13);
						}
						else if (p.get("name").equals("postEndLeft")) {
							tiles[i][j].setType(22);
						}
						else if (p.get("name").equals("postEndRight")) {
							tiles[i][j].setType(23);
						}
						else if (p.get("name").equals("tTop")) {
							tiles[i][j].setType(40);
						}
						else if (p.get("name").equals("tBottom")) {
							tiles[i][j].setType(41);
						}
						else if (p.get("name").equals("tLeft")) {
							tiles[i][j].setType(42);
						}
						else if (p.get("name").equals("tRight")) {
							tiles[i][j].setType(43);
						}
						else if (p.get("name").equals("wallEndTop")) {
							tiles[i][j].setType(44);
						}
						else if (p.get("name").equals("wallEndBottom")) {
							tiles[i][j].setType(45);
						}
						else if (p.get("name").equals("wallEndLeft")) {
							tiles[i][j].setType(46);
						}
						else if (p.get("name").equals("wallEndRight")) {
							tiles[i][j].setType(47);
						}
						else if (p.get("name").equals("hole")) {
							tiles[i][j].setEvilType("hole");
						}

						// Switches
						else if (p.get("name").equals("redSwitch")) {
							tiles[i][j].setType(-2);
							tiles[i][j].setSwitchColor("red");
							numSwitches++;
						}
						else if (p.get("name").equals("blueSwitch")) {
							tiles[i][j].setType(-2);
							tiles[i][j].setSwitchColor("blue");
							numSwitches++;
						}
						else if (p.get("name").equals("greenSwitch")) {
							tiles[i][j].setType(-2);
							tiles[i][j].setSwitchColor("green");
							numSwitches++;
						}
						else if (p.get("name").equals("orangeSwitch")) {
							tiles[i][j].setType(-2);
							tiles[i][j].setSwitchColor("orange");
							numSwitches++;
						}
						else if (p.get("name").equals("purpleSwitch")) {
							tiles[i][j].setType(-2);
							tiles[i][j].setSwitchColor("purple");
							numSwitches++;
						}
						else if (p.get("name").equals("yellowSwitch")) {
							tiles[i][j].setType(-2);
							tiles[i][j].setSwitchColor("yellow");
							numSwitches++;
						}

						// Hidden Switches
						else if (p.get("name").equals("redSwitchHidden")) {
							tiles[i][j].setType(-2);
							tiles[i][j].setSwitchColor("red");
							tiles[i][j].setHiddenSwitch(true);
							numSwitches++;
						}
						else if (p.get("name").equals("blueSwitchHidden")) {
							tiles[i][j].setType(-2);
							tiles[i][j].setSwitchColor("blue");
							tiles[i][j].setHiddenSwitch(true);
							numSwitches++;
						}
						else if (p.get("name").equals("greenSwitchHidden")) {
							tiles[i][j].setType(-2);
							tiles[i][j].setSwitchColor("green");
							tiles[i][j].setHiddenSwitch(true);
							numSwitches++;
						}
						else if (p.get("name").equals("orangeSwitchHidden")) {
							tiles[i][j].setType(-2);
							tiles[i][j].setSwitchColor("orange");
							tiles[i][j].setHiddenSwitch(true);
							numSwitches++;
						}
						else if (p.get("name").equals("purpleSwitchHidden")) {
							tiles[i][j].setType(-2);
							tiles[i][j].setSwitchColor("purple");
							tiles[i][j].setHiddenSwitch(true);
							numSwitches++;
						}
						else if (p.get("name").equals("yellowSwitchHidden")) {
							tiles[i][j].setType(-2);
							tiles[i][j].setSwitchColor("yellow");
							tiles[i][j].setHiddenSwitch(true);
							numSwitches++;
						}

						// Switch Doors
						else if (p.get("name").equals("redH")) {
							tiles[i][j].setType(-20);
						}
						else if (p.get("name").equals("redV")) {
							tiles[i][j].setType(-21);
						}
						else if (p.get("name").equals("blueH")) {
							tiles[i][j].setType(-22);
						}
						else if (p.get("name").equals("blueV")) {
							tiles[i][j].setType(-23);
						}
						else if (p.get("name").equals("orangeH")) {
							tiles[i][j].setType(-24);
						}
						else if (p.get("name").equals("orangeV")) {
							tiles[i][j].setType(-25);
						}
						else if (p.get("name").equals("purpleH")) {
							tiles[i][j].setType(-26);
						}
						else if (p.get("name").equals("purpleV")) {
							tiles[i][j].setType(-27);
						}
						else if (p.get("name").equals("yellowH")) {
							tiles[i][j].setType(-28);
						}
						else if (p.get("name").equals("yellowV")) {
							tiles[i][j].setType(-29);
						}
						else if (p.get("name").equals("greenH")) {
							tiles[i][j].setType(-30);
						}
						else if (p.get("name").equals("greenV")) {
							tiles[i][j].setType(-31);
						}
					}
				}
			}
		}

		// Set up all of the switches
		if (switchLayer != null) {
			Cell c2 = null;
			TiledMapTile t2 = null;
			MapProperties p2 = null;
			for (int i = 0; i < switchLayer.getWidth(); i++)
				for (int j = 0; j < switchLayer.getHeight(); j++) {
					c2 = switchLayer.getCell(i, j);
					if (c2 != null) {
						t2 = c2.getTile();
						p2 = t2.getProperties();
						if (p2.containsKey("name")) {
							if (p2.get("name").equals("redTrigger")) {
								if (baseLayer.getCell(i, j).getTile().getProperties().get("name").equals("redSwitch") || baseLayer.getCell(i, j).getTile().getProperties().get("name").equals("redSwitchHidden")) {
									if (switches1 == null)
										switches1 = new ArrayList<Tile>();
									switches1.add(tiles[i][j]);
								}
								else {
									if (triggers1 == null)
										triggers1 = new ArrayList<Tile>();
									triggers1.add(tiles[i][j]);
								}
							}
							else if (p2.get("name").equals("blueTrigger")) {
								if (baseLayer.getCell(i, j).getTile().getProperties().get("name").equals("blueSwitch") || baseLayer.getCell(i, j).getTile().getProperties().get("name").equals("blueSwitchHidden")) {
									if (switches2 == null)
										switches2 = new ArrayList<Tile>();
									switches2.add(tiles[i][j]);
								}
								else {
									if (triggers2 == null)
										triggers2 = new ArrayList<Tile>();
									triggers2.add(tiles[i][j]);
								}
							}
							else if (p2.get("name").equals("orangeTrigger")) {
								if (baseLayer.getCell(i, j).getTile().getProperties().get("name").equals("orangeSwitch") || baseLayer.getCell(i, j).getTile().getProperties().get("name").equals("orangeSwitchHidden")) {
									if (switches3 == null)
										switches3 = new ArrayList<Tile>();
									switches3.add(tiles[i][j]);
								}
								else {
									if (triggers3 == null)
										triggers3 = new ArrayList<Tile>();
									triggers3.add(tiles[i][j]);
								}
							}
							else if (p2.get("name").equals("purpleTrigger")) {
								if (baseLayer.getCell(i, j).getTile().getProperties().get("name").equals("purpleSwitch") || baseLayer.getCell(i, j).getTile().getProperties().get("name").equals("purpleSwitchHidden")) {
									if (switches4 == null)
										switches4 = new ArrayList<Tile>();
									switches4.add(tiles[i][j]);
								}
								else {
									if (triggers4 == null)
										triggers4 = new ArrayList<Tile>();
									triggers4.add(tiles[i][j]);
								}
							}
							else if (p2.get("name").equals("yellowTrigger")) {
								if (baseLayer.getCell(i, j).getTile().getProperties().get("name").equals("yellowSwitch") || baseLayer.getCell(i, j).getTile().getProperties().get("name").equals("yellowSwitchHidden")) {
									if (switches5 == null)
										switches5 = new ArrayList<Tile>();
									switches5.add(tiles[i][j]);
								}
								else {
									if (triggers5 == null)
										triggers5 = new ArrayList<Tile>();
									triggers5.add(tiles[i][j]);
								}
							}
							else if (p2.get("name").equals("greenTrigger")) {
								if (baseLayer.getCell(i, j).getTile().getProperties().get("name").equals("greenSwitch") || baseLayer.getCell(i, j).getTile().getProperties().get("name").equals("greenSwitchHidden")) {
									if (switches6 == null)
										switches6 = new ArrayList<Tile>();
									switches6.add(tiles[i][j]);
								}
								else {
									if (triggers6 == null)
										triggers6 = new ArrayList<Tile>();
									triggers6.add(tiles[i][j]);
								}
							}
						}
					}
				}
		}

		// Spawn snakes
		int currentSnakes = 0;
		while (true) {
			for (int i = 0; i < xSize; i++) {
				for (int j = 0; j < ySize; j++) {
					Tile st = tiles[i][j];
					if (rand.nextInt(50) == 1 && currentSnakes < maxSnakes && st.getEvilType().equals("none") && (st.type() == 1 || st.type() == 2)) {
						st.setEvilType("snake");
						currentSnakes++;
					}
				}
			}
			if (currentSnakes >= maxSnakes)
				break;
		}


		return tiles;
	}

	public static int getNumSwitches() {
		if (numSwitches > 0)
			return numSwitches;
		else
			return -1;
	}


}
