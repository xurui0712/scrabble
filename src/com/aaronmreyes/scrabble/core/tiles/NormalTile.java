/*
 * NormalTile.java
 * Version: 1.0
 * Date: 22 Oct 2013
 * Author: Aaron M. Reyes
 *
 * This content is released under the (http://opensource.org/licenses/MIT) MIT License.
 */

package com.aaronmreyes.scrabble.core.tiles;

import java.util.List;

import com.aaronmreyes.scrabble.core.Location;
import com.aaronmreyes.scrabble.core.Player;

/**
 * A Normal Tile with no special ability.
 * 
 * @author Aaron Reyes
 * 
 */
public class NormalTile extends AbstractTile {

	/**
	 * Constructor method to set up the information of a normal tile
	 * 
	 * @param value
	 *            - the ASCII value of the tile
	 * @param points
	 *            - the point value assign to the tile
	 * @param loc
	 *            - the {@link Location} of the tile (usually null)
	 * @param color
	 *            - the color in {@link colors} of a given tile for the GUI
	 */
	public NormalTile(char value, int points, Location loc, color color) {
		super(value, points, loc, color);
	}

	@Override
	public int doAbility(List<Player> players, int score) {
		return 0;
	}

}
