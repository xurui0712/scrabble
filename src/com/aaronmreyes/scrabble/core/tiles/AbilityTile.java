/*
 * AbilityTile.java
 * Version: 1.0
 * Date: 22 Oct 2013
 * Author: Aaron M. Reyes
 *
 * This content is released under the (http://opensource.org/licenses/MIT) MIT License.
 */

package com.aaronmreyes.scrabble.core.tiles;

import com.aaronmreyes.scrabble.core.Location;

/**
 * Class that contains the setup for any ability tile. Ability tiles are placed
 * on the board and visible to all players during the game. Whenever an ability
 * tile is used, then its ability is applied to the current move's score. You
 * must assign each tile a priority. See {@link Board#HIGH_PRIORITY} and
 * {@link Board#LOW_PRIORITY} for the default values. These tiles have no point
 * value.
 * 
 * @author Aaron Reyes
 * 
 */
public abstract class AbilityTile extends AbstractTile {
	private int priority;

	/**
	 * Constructor method for a AbilityTile.
	 * 
	 * @param loc
	 *            - the location of the Ability tile
	 * @param priority
	 *            - the priority of the Ability tile
	 * @param color
	 *            - the color in {@link colors} of a given tile for the GUI
	 */
	public AbilityTile(Location loc, int priority, color color) {
		/* everyone can see this tile and its value is NULL char */
		super('\0', 0, loc, color);
		this.priority = priority;
		/* the entire game can see an ability tile */
		this.setCreator("all");
	}

	/**
	 * USED BY BOARD: A getter method for the priority of an ability tile.
	 * 
	 * @return {@link #priority}
	 */
	public int getPriority() {
		return priority;
	}
}
