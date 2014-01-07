/*
 * DoubleWord.java
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
 * An {@link AbilityTile} that takes the total word score and doubles it.
 * 
 * @author Aaron Reyes
 * 
 */
public class DoubleWord extends AbilityTile {

	/**
	 * Constructor method for a DoubleWord tile.
	 * 
	 * @param loc
	 *            - the location of the Ability tile
	 * @param priority
	 *            - the priority of the Ability tile
	 * @param color
	 *            - the color in {@link colors} of a given tile for the GUI
	 */
	public DoubleWord(Location loc, int priority, color color) {
		super(loc, priority, color);
	}

	@Override
	public int doAbility(List<Player> players, int score) {
		/* just return the score times 2 */
		return 2 * score;
	}
}
