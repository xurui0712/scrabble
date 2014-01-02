/*
 * LoseWordTile.java
 * Version: 1.0
 * Date: 22 Oct 2013
 * Author: Aaron M. Reyes
 *
 * This content is released under the (http://opensource.org/licenses/MIT) MIT License.
 */

package edu.cmu.andrew.areyes.scrabble.core.tiles;

import java.util.List;

import edu.cmu.andrew.areyes.scrabble.core.Location;
import edu.cmu.andrew.areyes.scrabble.core.Player;

/**
 * Lose-a-Word: The player who activates this tile loses her current turn. The
 * word that activated this tile is not scored.
 * 
 * @author Aaron Reyes
 * 
 */
public class LoseWordTile extends AbstractTile {

	/**
	 * Constructor method to set up the information of a lose-word tile
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
	public LoseWordTile(char value, int points, Location loc, color color) {
		super(value, points, loc, color);
	}

	@Override
	public int doAbility(List<Player> players, int score) {
		/* do nothing. don't add the move's score to player */
		return 0;
	}

}
