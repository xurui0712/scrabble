/*
 * AbstractTile.java
 * Version: 1.0
 * Date: 22 Oct 2013
 * Author: Aaron M. Reyes
 *
 * This content is released under the (http://opensource.org/licenses/MIT) MIT License.
 */

package com.aaronmreyes.scrabble.core.tiles;

import java.util.List;

import com.aaronmreyes.scrabble.core.Game;
import com.aaronmreyes.scrabble.core.Location;
import com.aaronmreyes.scrabble.core.Player;

/**
 * Class that contains all information of any given tile on the scrabble board.
 * 
 * @author Aaron Reyes
 * 
 */
public abstract class AbstractTile {
	private char value;
	private int points;
	private Location loc;
	private String creator;
	private color color;
	
	/* all possible colors for a tile */
	public enum color {
		RED, CYAN, MAGENTA, GREEN, BLACK, BLUE,
	};

	/**
	 * Constructor method for a basic tile.
	 * 
	 * @param loc
	 *            - the location of the Ability tile
	 * @param priority
	 *            - the priority of the Ability tile
	 * @param color
	 *            - the color in {@link colors} of a given tile for the GUI
	 */
	public AbstractTile(char value, int points, Location loc, color color) {
		this.value = value;
		this.points = points;
		this.loc = loc;
		/* tiles have no creator until placed on board */
		this.creator = null;
		/* this trap tile color if applicable */
		this.color = color;
	}

	/**
	 * USED BY GUI: Getter method for the value of a tile
	 * 
	 * @return ASCII value for the tile
	 */
	public char getValue() {
		return value;
	}

	/**
	 * USED BY GUI: Getter method for the points of a tile
	 * 
	 * @return point value assigned to tile
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * USED BY GUI: Getter method for the {@link Location} of a tile
	 * 
	 * @return the location
	 */
	public Location getLocation() {
		return (Location) loc.clone();
	}
	
	/**
	 * USED BY GUI: Getter method for the color of a trap/special tile
	 * 
	 * @return the {@link color} of the tile
	 */
	public color getColor() {
		return color;
	}

	/**
	 * USED BY GUI: Getter method for the creator/owner of a tile
	 * 
	 * @return the location
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * This method allows for the {@link Game} to set the visibility of a given
	 * tile once a player plays a tile from their hand.
	 * 
	 * @param creator
	 *        - the name of the {@link Player} who owns this tile;
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * This method allows for {@link Game} class to set the location of a tile
	 * once it is taken from the player's hand and placed on the board.
	 * 
	 * @param loc
	 *            - the location this tile will be placed
	 */
	public void setLocation(Location loc) {
		this.loc = loc;
	}

	/**
	 * Abstract method for a given tile to perform its special ability on the
	 * the {@link Player}s in the {@link Game} with the final move score. This
	 * ability is limited to changing the overall score of the move. A given
	 * tile can take the move's score and change it to whatever it wants and
	 * then change the score of any given player's score by any amount desired.
	 * 
	 * @param players
	 *            - the list of {@link Player}s
	 * @param score
	 *            - the current score of the move or final score of the move
	 * @return the changed score of the move, if applicable
	 */
	public abstract int doAbility(List<Player> players, int score);
}
