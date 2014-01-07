/*
 * Player.java
 * Version: 1.0
 * Date: 10 Oct 2013
 * Author: Aaron M. Reyes
 *
 * This content is released under the (http://opensource.org/licenses/MIT) MIT License.
 */

package com.aaronmreyes.scrabble.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.aaronmreyes.scrabble.core.tiles.AbstractTile;

/**
 * The player class which contains various public getter methods used by the GUI
 * to get the info of a player and various protected methods used by the
 * {@link Game} to manipulate the state of the player.
 * 
 * @author Aaron Reyes
 * 
 */
final public class Player {
	/* used by Game: the current hand tile this player has selected */
	protected AbstractTile selectedHandTile;
	/* used by Game: the list of moves the player has made during their turn */
	protected final List<AbstractTile> moves = new ArrayList<AbstractTile>();
	/* used by Game: the list of tiles in the player's hand */
	protected final List<AbstractTile> hand = new ArrayList<AbstractTile>();
	/* is it this player's turn? */
	private boolean turn;
	/* what is the player's name? */
	private String name;
	/* what is this player's score */
	private int score;
	/* a generator used to mix the player's hand tiles */
	private final Random gen = new Random();

	/**
	 * The constructor method for a given player.
	 * 
	 * @param name
	 *            - the String containing the player's name
	 */
	protected Player(String name) {
		this.name = name;
		this.score = 0;
		this.turn = false;
	}

	/**
	 * USED BY GUI: getter method for a player's name.
	 * 
	 * @return {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * USED BY GUI: getter method for a player's score.
	 * 
	 * @return {@link #score}
	 */
	public int getScore() {
		return score;
	}

	/**
	 * USED BY GUI: getter method to figure out if its the player's turn.
	 * 
	 * @return {@link #turn}
	 */
	public boolean getTurn() {
		return turn;
	}

	/**
	 * USED BY GUI: getter method to get current player move-set.
	 * 
	 * @return {@link moves}
	 */
	public List<AbstractTile> getMoves() {
		return moves;
	}

	/**
	 * This method ends the player's turn.
	 */
	protected void changeTurn() {
		if (turn) {
			turn = false;
		} else {
			turn = true;
		}
	}

	/**
	 * This method mixes the {@link #hand} of the player.
	 */
	protected void mixTiles() {
		List<AbstractTile> newHand = new ArrayList<AbstractTile>();
		int size = hand.size();
		int seedIndex;
		for (int i = 0; i < size; i++) {
			seedIndex = gen.nextInt(hand.size());
			newHand.add(hand.get(seedIndex));
			hand.remove(seedIndex);
		}
		/* copy into hand list */
		this.hand.clear();
		for (AbstractTile tile : newHand) {
			this.hand.add(tile);
		}
	}

	/**
	 * USED BY SPECIAL TILES: This method changes the player's score by (value).
	 * 
	 * @param value
	 *            - a positive or negative integer
	 */
	public void changeScore(int value) {
		score += value;
	}
}
