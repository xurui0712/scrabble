/*
 * Dictionary.java
 * Version: 1.0
 * Date: 31 Oct 2013
 * Author: Aaron M. Reyes
 *
 * This content is released under the (http://opensource.org/licenses/MIT) MIT License.
 */

package edu.cmu.andrew.areyes.scrabble.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import edu.cmu.andrew.areyes.scrabble.core.tiles.AbilityTile;
import edu.cmu.andrew.areyes.scrabble.core.tiles.AbstractTile;
import edu.cmu.andrew.areyes.scrabble.core.tiles.NormalTile;

/**
 * This class is used to perform validations on a given word that a given play
 * is attempting to place. checks if that word is in the dictionary, checks if
 * the move is valid, and calculates the move's score.
 * 
 * @author Aaron Reyes
 * 
 */
class Dictionary {
	final private int LETTER_BANK_SIZE;
	/* the generator to draw tiles from bag */
	final private Random letterGen = new Random();
	/* the list containing the tiles in letterBank */
	private List<AbstractTile> letterBank;
	/* the set containing the words in dictionary.txt */
	private Set<String> dictionary;

	/* all the locations for travel on the board */
	private enum directions {
		NORTH, SOUTH, EAST, WEST
	};

	/**
	 * Constructor method to set up the dictionary
	 * 
	 * @param letterBank
	 *            - the letterBank created by the {@link Board} class
	 * @param dictionary
	 *            - the list of words created by the {@link Board} class
	 */
	public Dictionary(List<AbstractTile> letterBank, HashSet<String> dictionary) {
		this.dictionary = dictionary;
		this.letterBank = letterBank;
		/* store original letterBank size */
		this.LETTER_BANK_SIZE = letterBank.size();
	}

	/**
	 * Method to get a tile form the letterBank
	 * 
	 * @return an {@link AbstractTile}
	 */
	protected AbstractTile draw() {
		if (letterBank.size() > 0) {
			/* get a pseudo-random index in the letterBank */
			int index = letterGen.nextInt(letterBank.size());
			/* remove and return this tile */
			AbstractTile tile = letterBank.get(index);
			letterBank.remove(index);
			return tile;
		}
		return null;
	}

	/**
	 * Method to place a tile back in the letterBank (for exchanging tiles)
	 * 
	 * @param tile
	 *            - the tile to place in the {@link letterBank}
	 */
	protected void put(AbstractTile tile) {
		letterBank.add(tile);
	}

	/**
	 * Method to get how many tiles are left int he letterBank
	 */
	protected int getTilesLeft() {
		return letterBank.size();
	}

	/**
	 * Checks that the list of moves the player has made so far are valid.
	 * 
	 * @param player
	 *            - The currently playing player
	 * @param board
	 *            - the board the move was played on
	 * 
	 * @return true or false if the move is still valid
	 */
	protected boolean validateStep(Player player, AbstractTile[][] board) {
		int row, col;
		for (AbstractTile tile : player.getMoves()) {
			row = tile.getLocation().getRow();
			col = tile.getLocation().getCol();
			/* 1) check that player's move is in one direction only */
			int targetRow, targetCol;
			for (AbstractTile check : player.getMoves()) {
				targetRow = check.getLocation().getRow();
				targetCol = check.getLocation().getCol();
				if ((row != targetRow) && (col != targetCol)) {
					return false;
				}
			}
			/* 2) check that placed location is not taken already */
			AbstractTile selected = board[row][col];
			if (!(selected instanceof AbilityTile) && (selected != null)) {
				return false; // we only care about normal tiles
			}
			/* 3) a placed location cannot already be part of the move */
			int checkRow, checkCol, currIndex, checkIndex;
			for (AbstractTile check : player.getMoves()) {
				currIndex = player.getMoves().indexOf(tile);
				checkIndex = player.getMoves().indexOf(check);
				checkRow = check.getLocation().getRow();
				checkCol = check.getLocation().getCol();
				if ((currIndex != checkIndex) && (row == checkRow)
						&& (col == checkCol)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Method to see if a player's move is still valid.
	 * 
	 * @param player
	 *            - the player making the move
	 * @param board
	 *            - the board the move was played on
	 * @param numPlayers
	 *            - the number of players playing the game
	 * 
	 * @return true of false if the move is valid or not
	 */
	protected boolean validateWord(Player player, AbstractTile[][] board,
			int numPlayers) {
		/* check that each tile in player's move is touching another tile */
		boolean flag = false;
		for (AbstractTile move : player.getMoves()) {
			int row = move.getLocation().getRow();
			int col = move.getLocation().getCol();
			/* make sure AT LEAST ONE is next to a tile ON THE BOARD */
			for (directions dir : directions.values()) {
				int drow = row;
				int dcol = col;
				switch (dir) {
				case NORTH:
					drow -= 1;
					break;
				case SOUTH:
					drow += 1;
					break;
				case EAST:
					dcol += 1;
					break;
				case WEST:
					dcol -= 1;
					break;
				}
				/* check out of bounds access */
				if ((drow < 0) || (drow >= Board.BOARD_SIZE) || (dcol < 0)
						|| (dcol >= Board.BOARD_SIZE)) {
					continue;
				}
				/* get tile in that direction */
				AbstractTile next = board[drow][dcol];
				/* check for non-empty and non-ability tile spaces */
				if ((next != null) && !(next instanceof AbilityTile)) {
					flag = true; /* set flag */
					break;
				}
			}
			if (flag) {
				break; /* stop looping */
			}
		}
		int check = LETTER_BANK_SIZE - (numPlayers * Game.HAND_LIMIT);
		/* if flag was not set and board is not new... */
		if ((!flag) && (letterBank.size() != check)) {
			/* then the word was placed randomly */
			return false;
		}
		/* make a new 2d board and, for now, make the player's move */
		AbstractTile[][] temp = new AbstractTile[Board.BOARD_SIZE][];
		for (int i = 0; i < Board.BOARD_SIZE; i++) {
			temp[i] = board[i].clone();
		}
		/* make move in board copy */
		int row, col;
		for (AbstractTile tile : player.getMoves()) {
			row = tile.getLocation().getRow();
			col = tile.getLocation().getCol();
			temp[row][col] = tile;
		}
		/* loop through board and check that each word placed is valid */
		List<String> found = new ArrayList<String>();
		for (int i = 0; i < Board.BOARD_SIZE; i++) {
			for (int j = 0; j < Board.BOARD_SIZE; j++) {
				/* no tile above and/or to left means start of word */
				if ((i == 0) || (temp[i - 1][j] == null)) {
					found.add(getWord(i, j, directions.SOUTH, temp));
				}
				if ((j == 0) || (temp[i][j - 1] == null)) {
					found.add(getWord(i, j, directions.EAST, temp));
				}
			}
		}
		/* now check that all the words found are in dictionary */
		for (String word : found) {
			if ((word != null) && (!dictionary.contains(word))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Method that takes a (row, column) on board and goes in a direction until
	 * hits null and returns word found. returns null if the word is only 1 char
	 * long.
	 * 
	 * @param row
	 *            - the row on the board
	 * @param col
	 *            - the column on the board
	 * @param dir
	 *            - the direction in {@link directions}
	 * @param boardCopy
	 *            - a copy of the board to temporary place player word on
	 * 
	 * @return a string of the word found. null if none found.
	 */
	protected String getWord(int row, int col, directions dir,
			AbstractTile[][] boardCopy) {
		String word = "";
		AbstractTile letter = boardCopy[row][col];
		/* loop until we hit null or an ability tile with no letter value */
		while ((letter != null) && !(letter instanceof AbilityTile)) {
			/* get our letter */
			word += boardCopy[row][col].getValue();
			/* make sure we don't loop off board and update location on board */
			if (dir == directions.SOUTH) {
				if ((row + 1) < Board.BOARD_SIZE) {
					letter = boardCopy[++row][col];
				} else {
					break;
				}
			} else if (dir == directions.EAST) {
				if ((col + 1) < Board.BOARD_SIZE) {
					letter = boardCopy[row][++col];
				} else {
					break;
				}
			}
		}
		if ((word.length() == 1) || (word.length() == 0)) {
			return null;
		}
		return word.toLowerCase();
	}

	/**
	 * Gets nearby tiles of a players move. Basically all surrounding tiles that
	 * could be considered in the move.
	 * 
	 * @param move
	 *            - player move
	 * @param board
	 *            - board move is being played on
	 * 
	 * @return the list of abstract tiles nearby in the player's word
	 */
	protected List<AbstractTile> getNearbyTiles(List<AbstractTile> move,
			AbstractTile[][] board) {
		List<AbstractTile> found = new ArrayList<AbstractTile>();
		int row, col, drow, dcol;
		for (AbstractTile tile : move) {
			/* keep track of what we have seen */
			if (!found.contains(tile)) {
				found.add(tile);
			}
			row = tile.getLocation().getRow();
			col = tile.getLocation().getCol();
			/* loop in all directions */
			for (directions dir : directions.values()) {
				drow = row;
				dcol = col;
				/* continue in a direction until end of word */
				while (true) {
					switch (dir) {
					case NORTH:
						drow -= 1;
						break;
					case SOUTH:
						drow += 1;
						break;
					case EAST:
						dcol += 1;
						break;
					case WEST:
						dcol -= 1;
						break;
					}
					/* check out of bounds access */
					if ((drow < 0) || (drow >= Board.BOARD_SIZE) || (dcol < 0)
							|| (dcol >= Board.BOARD_SIZE)) {
						break;
					}
					/* get tile in that direction */
					AbstractTile next = board[drow][dcol];
					/* ignore empty spaces and ability tiles found */
					if ((next == null) || (next instanceof AbilityTile)) {
						break;
					}
					/* don't add in tiles we have already seen */
					if (!found.contains(next)) {
						found.add(next);
					}
				}
			}
		}
		return found;
	}

	/**
	 * takes nearby tiles and filters out all traps found.
	 * 
	 * @param move
	 *            - the player move
	 * @param board
	 *            - the board the move is being played on
	 * 
	 * @return the list of trap tiles found nearby
	 */
	protected List<AbstractTile> findTrapTiles(List<AbstractTile> move,
			AbstractTile[][] board) {
		List<AbstractTile> found = getNearbyTiles(move, board);
		List<AbstractTile> trapsFound = new ArrayList<AbstractTile>();
		/* find traps in nearby tiles */
		for (AbstractTile tile : found) {
			/* check for trap tile */
			if (!move.contains(tile) && !(tile instanceof AbilityTile)
					&& !(tile instanceof NormalTile)) {
				trapsFound.add(tile);
			}
		}
		return trapsFound;
	}

	/**
	 * takes all nearby tiles used in move and calculates up base score
	 * 
	 * @param move
	 *            - the player move
	 * @param board
	 *            - the board the move is played on
	 * 
	 * @return the base score for the move without ability or trap tiles
	 */
	protected int calculateMoveScore(List<AbstractTile> move,
			AbstractTile[][] board) {
		List<AbstractTile> found = getNearbyTiles(move, board);
		int score = 0;
		/* get score of each tile */
		for (AbstractTile tile : found) {
			score += tile.getPoints();
		}
		return score;
	}

}
