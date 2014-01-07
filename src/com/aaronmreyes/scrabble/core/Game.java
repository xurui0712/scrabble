/*
 * Game.java
 * Version: 1.0
 * Date: 21 Oct 2013
 * Author: Aaron M. Reyes
 *
 * This content is released under the (http://opensource.org/licenses/MIT) MIT License.
 */

package com.aaronmreyes.scrabble.core;

import java.util.ArrayList;
import java.util.List;

import com.aaronmreyes.scrabble.core.tiles.AbstractTile;
import com.aaronmreyes.scrabble.core.tiles.NegativePointTile;
import com.aaronmreyes.scrabble.core.tiles.NormalTile;
import com.aaronmreyes.scrabble.core.tiles.StealWordTile;
import com.aaronmreyes.scrabble.core.tiles.AbstractTile.color;

/**
 * The Game class which contains all methods needed by the GUI to react to
 * changes in the game state. Contains its own instances of a {@link Board} and
 * list of {@link Player}s
 * 
 * @author Aaron Reyes
 * 
 */
final public class Game {
	/* The max number of tiles a player can hold: */
	public static final int HAND_LIMIT = 7;
	/* the maximum number of players allowed to play: */
	public static final int MAX_PLAYERS = 4;
	/* the minimum number of players allowed to play: */
	public static final int MIN_PLAYERS = 2;
	/* list used when a player wants to exchange tiles with the letterBank */
	final private List<AbstractTile> exchange = new ArrayList<AbstractTile>();
	/* the data needed to understand the current state of the game : */
	final private List<Player> players = new ArrayList<Player>();
	private Board board;
	private Player currPlayer;

	// **************** TESTING METHODS ****************************

	/**
	 * Used to set players hands to default tile sets for testing
	 */
	public void testingMethodNormal() {

		for (Player player : players) {
			player.hand.clear();
			player.hand.add(new NormalTile('S', 1, null, color.BLACK));
			player.hand.add(new NormalTile('A', 1, null, color.BLACK));
			player.hand.add(new NormalTile('N', 1, null, color.BLACK));
			player.hand.add(new NormalTile('D', 1, null, color.BLACK));
			player.hand.add(new NormalTile('A', 1, null, color.BLACK));
			player.hand.add(new NormalTile('P', 1, null, color.BLACK));
			player.hand.add(new NormalTile('S', 1, null, color.BLACK));
		}
	}

	/**
	 * Testing method: used to set players hands to default tile sets
	 */
	public void testingMethodSpecial() {
		for (Player player : players) {
			player.hand.clear();
			player.hand.add(new StealWordTile('S', 1, null, color.CYAN));
			player.hand.add(new NegativePointTile('A', 1, null, color.RED));
			player.hand.add(new NormalTile('N', 1, null, color.BLACK));
			player.hand.add(new NormalTile('D', 1, null, color.BLACK));
			player.hand.add(new NormalTile('A', 1, null, color.BLACK));
			player.hand.add(new NormalTile('P', 1, null, color.BLACK));
			player.hand.add(new NormalTile('S', 1, null, color.BLACK));
		}
	}

	/**
	 * Testing method: to see what the board looks like for debugging
	 */
	static public void printBoard(AbstractTile[][] board) {
		System.out.println("BOARD:");
		for (int i = 0; i < Board.BOARD_SIZE; i++) {
			for (int j = 0; j < Board.BOARD_SIZE; j++) {
				if (board[i][j] != null) {
					if (board[i][j].getValue() != '\0') {
						/* normal tiles */
						System.out.print(" " + board[i][j].getValue());
					} else {
						/* ability tiles */
						System.out.print(" *");
					}
				} else {
					/* empty spaces */
					System.out.print(" -");
				}
			}
			System.out.println();
		}
	}

	/**
	 * Testing method: used to empty the letterBank.
	 */
	public void emptyLetterBank() {
		while (board.getLetterBankTotal() != 0) {
			board.getTile();
		}
	}

	// *************************************************************

	/**
	 * The method used to set up/restart a game.
	 * 
	 * @param playerNames
	 *            - the names of each player
	 * @param extra
	 *            - a flag to inform the {@link Board} to use special tiles or
	 *            not
	 */
	public void setupGame(List<String> playerNames, boolean extra) {
		/* create players */
		for (String name : playerNames) {
			players.add(new Player(name));
		}
		/* set up a new board */
		board = new Board(extra);
		/* draw 7 tiles for each player's starting hand */
		for (Player player : players) {
			int cardsDrawn = 0;
			while (cardsDrawn < HAND_LIMIT) {
				player.hand.add(board.getTile());
				cardsDrawn++;
			}
		}
		/* updated selected board/hand tile */
		updateBoardTile(Board.BOARD_SIZE / 2, Board.BOARD_SIZE / 2);
	}

	/**
	 * The method used to set the starting player of the game.
	 * 
	 * @param name
	 *            - the name of the first player to play
	 */
	public void setStartingPlayer(String name) {
		/* find matching player */
		for (Player player : players) {
			if (player.getName().equals(name)) {
				player.changeTurn();
				currPlayer = player;
			}
		}
		updateHandTile(0);
	}

	/**
	 * Method that ends the current turn and sets the next players turn in the
	 * list.
	 */
	public void changeTurn() {
		/* index of current player in list */
		int index = players.indexOf(currPlayer);
		/* go to the next player */
		Player player = players.get((++index) % players.size());
		/* flip turn flags */
		player.changeTurn();
		currPlayer.changeTurn();
		currPlayer = player;
		/* player assumed to have at least one tile in hand */
		updateHandTile(0);
	}

	/**
	 * The method to check for a winning player.
	 * 
	 * @return null if no player found or the Player/Players who won
	 */
	public List<Player> checkForWin() {
		/* check if the letterBank is empty */
		if (board.getLetterBankTotal() != 0) {
			return null;
		}
		/* now see if a given player has no tiles in hand */
		for (Player player : players) {
			if (player.hand.size() == 0) {
				/* update final scores */
				board.setFinalScores(players);
				/* now find player with highest score */
				int max = 0;
				List<Player> winners = new ArrayList<Player>();
				for (Player check : players) {
					if (check.getScore() > max) {
						/* clear old winners */
						winners.clear();
						winners.add(check);
						max = check.getScore();
					} else if (check.getScore() == max) {
						winners.add(check);
					}
				}
				return winners;
			}
		}
		return null;
	}

	/**
	 * the method to take the current player's selected hand tile and adds it to
	 * their list of moves made this turn while removing this tile from their
	 * hand. If the player's is invalid, the move is undone and the selected
	 * tile is returned to the player's hand. NOTE: does not actually place on
	 * Board.
	 */
	public void placeTileOnBoard() {
		AbstractTile tile = currPlayer.selectedHandTile;
		/* now get the location of the selected board tile */
		int row = board.selectedBoardTile.getRow();
		int col = board.selectedBoardTile.getCol();
		/* set the default selected board tile to center of board */
		updateBoardTile(Board.BOARD_SIZE / 2, Board.BOARD_SIZE / 2);
		/* define this location for the tile we will place */
		tile.setLocation(new Location(row, col));
		/* add this tile to the moves made list */
		currPlayer.getMoves().add(tile);
		/* check if move was not a valid step */
		if (!board.isValidStep(currPlayer)) {
			/* undo last move */
			currPlayer.getMoves().remove(tile);
			/* undo location set */
			tile.setLocation(null);
		} else {
			/* remove tile from player's hand for valid step */
			currPlayer.hand.remove(tile);
		}
		/* assign the new selected hand tile to start of hand */
		if (currPlayer.hand.size() > 0) {
			updateHandTile(0);
		}
	}

	/**
	 * The method used when a player wants to undo the last move they made. Does
	 * nothing if their is no last move to undo.
	 */
	public void removeTilefromBoard() {
		/* get last played tile */
		AbstractTile tile;
		int size = currPlayer.getMoves().size();
		if (size > 0) {
			tile = currPlayer.getMoves().remove(size - 1);
			/* clear the location of that tile */
			tile.setLocation(null);
			/* put back into hand */
			currPlayer.hand.add(tile);
			/* check the need to update first tile */
			if (currPlayer.hand.size() == 0) {
				updateHandTile(0);
			}
		}
	}

	/**
	 * The method to mix the tiles in the current Player's hand.
	 */
	public void mixHand() {
		currPlayer.mixTiles();
		/* update selected hand tile */
		updateHandTile(0);
	}

	/**
	 * The method which updates which hand tile the current player has selected
	 * to use.
	 * 
	 * @param index
	 *            - the index of the tile in the hand of the current player
	 */
	public void updateHandTile(int index) {
		if ((0 <= index) && (index < currPlayer.hand.size())) {
			currPlayer.selectedHandTile = currPlayer.hand.get(index);
		}
	}

	/**
	 * The method used to update the board tile selected by the current player
	 * for use in their move.
	 * 
	 * @param row
	 *            - The row of the tile selected on the board
	 * @param col
	 *            - the column of the tile selected on the board
	 */
	public void updateBoardTile(int row, int col) {
		board.selectedBoardTile = new Location(row, col);
	}

	/**
	 * The method used by the current player to take their move-set and place
	 * these moves on the board. If the move is invalid (does not match a word
	 * in the dictionary.txt) then the move is undone and all tiles used in the
	 * move are returned to the current player's hand. If the move was valid,
	 * then tiles are drawn from the letterBank until the player has the
	 * {@value #HAND_LIMIT} number of tiles in their hand. If there are no more
	 * tiles left in the letterBank, then the player doesn't get anymore cards
	 * into their hand. Lastly, this method clears the current player's moves
	 * list.
	 */
	public void playWord() {
		/* if the player has a valid move, play it */
		if (board.isValidWord(currPlayer, players.size())
				&& board.isValidStep(currPlayer)) {
			/* set the creator of these tiles to be the current player */
			for (AbstractTile tile : currPlayer.getMoves()) {
				tile.setCreator(currPlayer.getName());
			}
			/* make move from hand onto board */
			board.placeTiles(currPlayer, players);
			/* draw tiles until player has correct amount in hand */
			while (currPlayer.hand.size() != HAND_LIMIT) {
				/* only draw if there are tiles to do so */
				if (board.getLetterBankTotal() == 0) {
					break;
				} else {
					currPlayer.hand.add(board.getTile());
				}
			}
		} else {
			/* undo player each move */
			for (AbstractTile tile : currPlayer.getMoves()) {
				tile.setLocation(null);
				currPlayer.hand.add(tile);
			}
		}
		/* update selected tiles */
		updateBoardTile(Board.BOARD_SIZE / 2, Board.BOARD_SIZE / 2);
		if (currPlayer.hand.size() > 0) {
			updateHandTile(0);
		}
		/* clear player moves */
		currPlayer.getMoves().clear();
	}

	/**
	 * This method is used when a player wishes to take a given tile in their
	 * hand and add it to the list of exchanged tiles. Does not remove tile from
	 * the player's hand.
	 * 
	 * @param index
	 *            - The index of the tile in the player's hand to add to the
	 *            list
	 */
	public void addExchangeTile(int index) {
		/* only add in tiles that haven't been added already */
		if (!exchange.contains(currPlayer.hand.get(index))) {
			exchange.add(currPlayer.hand.get(index));
		}
	}

	/**
	 * This method is used when the current player wishes to remove a certain
	 * tile from the list of exchanged tiles.
	 * 
	 * @param index
	 *            - the index of the tile in the player's hand to remove.
	 */
	public void removeExchangeTile(int index) {
		exchange.remove(currPlayer.hand.get(index));
	}

	/**
	 * This method is used when the player is done adding tiles to be exchanged
	 * and wishes to exchange all selected tiles with the letterBank. If the
	 * letterBank is empty, then all selected tiles are just returned to the
	 * current player's hand. Lastly, this method clears the exchanges list upon
	 * exit.
	 */
	public void exchangeTiles() {
		for (AbstractTile tile : exchange) {
			currPlayer.hand.remove(tile);
			/* make sure letter bank has enough tiles */
			if (board.getLetterBankTotal() > 0) {
				currPlayer.hand.add(board.getTile());
				board.putTile(tile);
			} else {
				/* just add the tile back to the player hand */
				currPlayer.hand.add(tile);
			}
		}
		/* reset the exchange list */
		exchange.clear();
		/* update selected hand tile */
		updateHandTile(0);
	}
	
	/**
	 * A simple method used by the GUI to undo an invalid move
	 */
	public void undoMove() {
		/* undo player each move */
		for (AbstractTile tile : currPlayer.getMoves()) {
			tile.setLocation(null);
			currPlayer.hand.add(tile);
		}
		/* update selected tiles */
		updateBoardTile(Board.BOARD_SIZE / 2, Board.BOARD_SIZE / 2);
		if (currPlayer.hand.size() > 0) {
			updateHandTile(0);
		}
		/* clear player moves */
		currPlayer.getMoves().clear();
	}

	/**
	 * Getter method to get the current board.
	 * 
	 * @return the {@link Board#board}
	 */
	public AbstractTile[][] getBoard() {
		return board.board;
	}

	/**
	 * getter method to get the current list of player in the game.
	 * 
	 * @return the {@link #players}
	 */
	public List<Player> getPlayers() {
		return players;
	}

	/**
	 * getter method to get the length of the LetterBank list.
	 * 
	 * @return the return value of {@link Board#getLetterBankTotal()}
	 */
	public int getNumTilesLeft() {
		return board.getLetterBankTotal();
	}

	/**
	 * getter method to get the current player making their move.
	 * 
	 * @return {@link #currPlayer}
	 */
	public Player getCurrentPlayer() {
		return currPlayer;
	}

	/**
	 * getter method to get the current move set of the current player.
	 * 
	 * @return {@link Player#movesMade}
	 */
	public List<AbstractTile> getCurrentMove() {
		return currPlayer.getMoves();
	}

	/**
	 * getter method to get the current player's hand
	 * 
	 * @return {@link Player#hand}
	 */
	public List<AbstractTile> getPlayerHand() {
		return currPlayer.hand;
	}

}
