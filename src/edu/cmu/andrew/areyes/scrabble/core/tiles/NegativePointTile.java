/*
 * NegativePointTile.java
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
 * Negative-points: The word that activated this tile is scored negatively for
 * the player who activated the tile; i.e., the player loses (rather than gains)
 * the points for the played word.
 * 
 * @author Aaron Reyes
 * 
 */
public class NegativePointTile extends AbstractTile {

	/**
	 * Constructor method to set up the information of a negative point tile
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
	public NegativePointTile(char value, int points, Location loc, color color) {
		super(value, points, loc, color);
	}

	@Override
	public int doAbility(List<Player> players, int score) {
		/* find the player's score and subtract it */
		for (Player player : players) {
			if (player.getTurn()) {
				player.changeScore(-score);
			}
		}
		return 0;
	}
}
