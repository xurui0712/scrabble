/*
 * Board.java
 * Version: 1.0
 * Date: 21 Oct 2013
 * Author: Aaron M. Reyes
 *
 * This content is released under the (http://opensource.org/licenses/MIT) MIT License.
 */

package edu.cmu.andrew.areyes.scrabble.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Random;

import edu.cmu.andrew.areyes.scrabble.core.tiles.AbilityTile;
import edu.cmu.andrew.areyes.scrabble.core.tiles.AbstractTile;
import edu.cmu.andrew.areyes.scrabble.core.tiles.DoubleLetter;
import edu.cmu.andrew.areyes.scrabble.core.tiles.DoubleWord;
import edu.cmu.andrew.areyes.scrabble.core.tiles.LetterBombTile;
import edu.cmu.andrew.areyes.scrabble.core.tiles.LoseWordTile;
import edu.cmu.andrew.areyes.scrabble.core.tiles.NegativePointTile;
import edu.cmu.andrew.areyes.scrabble.core.tiles.NormalTile;
import edu.cmu.andrew.areyes.scrabble.core.tiles.StealWordTile;
import edu.cmu.andrew.areyes.scrabble.core.tiles.TripleLetter;
import edu.cmu.andrew.areyes.scrabble.core.tiles.TripleWord;
import edu.cmu.andrew.areyes.scrabble.core.tiles.AbstractTile.color;

/**
 * The class that contains all functionality required to handle operations on
 * the board and sets up the board by reading the various files in assets
 * required to initialize the board.
 * 
 * @author Aaron Reyes
 * 
 */
final class Board {
	/* default constants: */
	static final int BOARD_SIZE = 15;
	static final int NUM_SPECIAL_TILES = 4;
	static final int SPECIAL_TILES_ALLOWED = 15;
	static final int HIGH_PRIORITY = 0;
	static final int LOW_PRIORITY = 10;
	/* our reference to the dictionary for validity checking */
	private final Dictionary dict;
	/* the currently selected board tile by the player */
	protected Location selectedBoardTile;
	/* the board of tiles */
	protected AbstractTile[][] board;

	/**
	 * The constructor method for a Board. sets up default ability tiles,
	 * letterBank, and special tiles if requested to
	 * 
	 * @param flag
	 *            - a flag whether or not we are playing with special tiles
	 */
	protected Board(boolean flag) {
		/* set up board as standard a 15 by 15 2d list */
		board = new AbstractTile[BOARD_SIZE][BOARD_SIZE];
		/* Initialize default ability tiles */
		setUpAbilityTiles();
		/* set up dictionary of words */
		HashSet<String> dictionary = setUpDictionary();
		/* set up letter bank */
		List<AbstractTile> letterBank = setUpLetterBank();
		/* add special abilities to random tiles if players want */
		if (flag) {
			setUpSpecialTiles(letterBank);
		}
		/* set up dictionary */
		dict = new Dictionary(letterBank, dictionary);
	}

	/**
	 * This method places tiles on the board. takes the current plauer's move
	 * list and places each on board. Assumes that player move is valid at this
	 * point.
	 * 
	 * @param player
	 *            - the current playing player
	 * @param players
	 *            - the list of all player in the game
	 */
	protected void placeTiles(Player player, List<Player> players) {
		/* a list to hold any special tiles we encounter */
		List<AbilityTile> specialTiles = new ArrayList<AbilityTile>();
		/* loop through the stack of moves made and pop onto board */
		for (AbstractTile tile : player.getMoves()) {
			/* check position on board see if an ability tile was placed */
			int row = tile.getLocation().getRow();
			int col = tile.getLocation().getCol();
			AbstractTile selected = board[row][col];
			if (selected instanceof AbilityTile) {
				specialTiles.add((AbilityTile) selected);
			}
			/* place step in move on board */
			board[row][col] = tile;
		}
		/* calculate base move score (without abilities) for player */
		int score = dict.calculateMoveScore(player.getMoves(), board);
		/* get all nearby special tiles used in the move */
		List<AbstractTile> traps = dict.findTrapTiles(player.getMoves(), board);
		/* apply any ability tiles found */
		if (specialTiles.size() != 0) {
			/* sort ability tiles by their priority */
			Collections.sort(specialTiles, new Comparator<AbilityTile>() {
				@Override
				public int compare(AbilityTile o1, AbilityTile o2) {
					return o1.getPriority() - o2.getPriority();
				}
			});
			/* perform ability of all special tiles found on the score */
			for (AbilityTile tile : specialTiles) {
				/* keep the current running score updated for the move */
				score = tile.doAbility(players, score);
			}
		}
		/* deal with all special tiles found/used in move */
		if (traps.size() != 0) {
			/* perform abilities of these traps */
			for (AbstractTile tile : traps) {
				tile.doAbility(players, score);
			}
		} else {
			/* update main player score */
			player.changeScore(score);
		}
	}

	/**
	 * This method draws a random tile from the letterBank
	 * 
	 * @return an {@link AbstractTile}
	 */
	protected AbstractTile getTile() {
		return dict.draw();
	}

	/**
	 * When a tile needs to be put back in the letterBank
	 * 
	 * @param tile
	 *            - an {@link AnstractTile}
	 */
	protected void putTile(AbstractTile tile) {
		/* put a tile back in the letterBank */
		dict.put(tile);
	}

	/**
	 * Returns the length of the letterBank
	 */
	protected int getLetterBankTotal() {
		return dict.getTilesLeft();
	}

	/**
	 * Checks that the list of moves the player has made so far are valid.
	 * 
	 * @param player
	 *            - The currently playing player
	 * @return true or false if the move is still valid
	 */
	protected boolean isValidStep(Player player) {
		return dict.validateStep(player, board);
	}

	/**
	 * Checks if an entire move matches a given word played by the player is in
	 * the dictionary.txt file.
	 * 
	 * @param player
	 *            - the currently playing player
	 * @param numPlayers
	 *            - the number of players in the current game
	 * 
	 * @return true or false if the entire move is valid
	 */
	protected boolean isValidWord(Player player, int numPlayers) {
		return dict.validateWord(player, board, numPlayers);
	}

	/**
	 * This method is called when the game ends and all remaining tiles in each
	 * players hands must be deducted from their score total.
	 * 
	 * @param players
	 */
	protected void setFinalScores(List<Player> players) {
		/* loop through players and their hands collecting the deduction */
		for (Player player : players) {
			int deduction = 0;
			for (AbstractTile tile : player.hand) {
				deduction += tile.getPoints();
			}
			/* subtract the deduction from the player score */
			player.changeScore(-deduction);
		}
	}

	/*
	 * uses assets/ability.txt to know where default ability tiles are.
	 */
	private void setUpAbilityTiles() {
		/* set up file descriptor */
		try {
			InputStream file = getClass().getResourceAsStream(
					"/assets/ability.txt");
			Scanner data = new Scanner(file);
			/* loop through each line */
			int row = 0;
			int col = 0;
			while (data.hasNextLine()) {
				String line = data.nextLine();
				String[] values = line.split(" ");
				/* check that line was parsed correctly */
				if (values.length != 15) {
					String err = "ability.txt: parse failed";
					data.close();
					throw new IllegalStateException(err);
				}
				Location loc;
				for (String str : values) {
					/* figure out which ability tile we have */
					switch (str) {
					case "TW":
						loc = new Location(row, col);
						board[row][col] = new TripleWord(loc, LOW_PRIORITY,
								color.RED);
						break;
					case "DW":
						loc = new Location(row, col);
						board[row][col] = new DoubleWord(loc, LOW_PRIORITY,
								color.MAGENTA);
						break;
					case "TL":
						loc = new Location(row, col);
						board[row][col] = new TripleLetter(loc, HIGH_PRIORITY,
								color.BLUE);
						break;
					case "DL":
						loc = new Location(row, col);
						board[row][col] = new DoubleLetter(loc, HIGH_PRIORITY,
								color.CYAN);
						break;
					case "--":
						break;
					default:
						String err = str + " invalid ability tile value";
						data.close();
						throw new IllegalStateException("ability.txt: " + err);
					}
					/* update our location on board */
					col += 1;
				}
				/* update our row and reset the column */
				row += 1;
				col = 0;
			}
			data.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(2); /* abort */
		}
	}

	/*
	 * uses assets/dictioanry.txt to set up set of words in our dictionary
	 */
	private HashSet<String> setUpDictionary() {
		/* set up hash table for constant lookup */
		HashSet<String> dictionary = new HashSet<String>();
		/* set up file descriptor */
		try {
			InputStream file = getClass().getResourceAsStream(
					"/assets/dictionary.txt");
			Scanner data = new Scanner(file);
			/* read in each word and add to hash table */
			while (data.hasNextLine()) {
				String word = data.nextLine();
				dictionary.add(word);
			}
			data.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(2); /* abort */
		}
		return dictionary;
	}

	/*
	 * sets up letterBank with assets/values.txt
	 */
	private List<AbstractTile> setUpLetterBank() {
		List<AbstractTile> letterBank = new ArrayList<AbstractTile>();
		/* set up file descriptor */
		try {
			InputStream file = getClass().getResourceAsStream(
					"/assets/values.txt");
			Scanner data = new Scanner(file);
			/* read each line as "<letter> <how many> <point value>" */
			while (data.hasNextLine()) {
				String line = data.nextLine();
				String[] values = line.split(" ");
				/* check that line was parsed correctly */
				if (values.length != 3) {
					String err = "<letter> <amount> <points> parse failed";
					data.close();
					throw new IllegalStateException("values.txt: " + err);
				}
				/* get char value */
				char val = values[0].charAt(0);
				/* check that letter input is valid */
				if (((int) val < 65) || ((int) val > 90)) {
					String err = val + " is not an uppercase letter";
					data.close();
					throw new IllegalStateException("values.txt: " + err);
				}
				/* get point data */
				int pnts = Integer.parseInt(values[2]);
				/* get how many of them to add */
				int amount = Integer.parseInt(values[1]);
				/* check that number inputs are valid */
				if ((pnts < 0) || (amount < 0)) {
					String err = "amount/points must be >= 0";
					data.close();
					throw new IllegalStateException("values.txt: " + err);
				}
				/* loop through and add that many to the letterBank */
				for (int i = 0; i < amount; i++) {
					letterBank
							.add(new NormalTile(val, pnts, null, color.BLACK));
				}
			}
			data.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(2); /* abort */
		}
		return letterBank;
	}

	/*
	 * if special tiles are enabled, then picks SPECIAL_TILES_ALLOWED tiles to
	 * be made into special tiles and shuffled bank into letterBank.
	 */
	private void setUpSpecialTiles(List<AbstractTile> letterBank) {
		Random gen = new Random();
		int index;
		/* for the number of special tiles allowed, make them */
		for (int count = 0; count < SPECIAL_TILES_ALLOWED; count++) {
			/* get a pseudo-random tile in letter bank */
			index = gen.nextInt(letterBank.size());
			AbstractTile tile = letterBank.get(index);
			while (!(tile instanceof NormalTile)) {
				/* make sure it isn't already a special tile */
				index = gen.nextInt(letterBank.size());
				tile = letterBank.get(index);
			}
			char val = tile.getValue();
			int pnts = tile.getPoints();
			/* pick a pseudo-random special tile to add */
			switch (gen.nextInt(NUM_SPECIAL_TILES)) {
			case 0:
				tile = new NegativePointTile(val, pnts, null, color.RED);
				break;
			case 1:
				tile = new StealWordTile(val, pnts, null, color.CYAN);
				break;
			case 2:
				tile = new LoseWordTile(val, pnts, null, color.GREEN);
				break;
			case 3:
				tile = new LetterBombTile(val, pnts, null, color.MAGENTA);
				break;
			}
			/* add new special tile into the letterBank */
			letterBank.remove(index);
			letterBank.add(tile);
		}
	}
}
