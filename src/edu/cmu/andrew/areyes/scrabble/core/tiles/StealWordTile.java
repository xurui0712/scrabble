/*
 * StealWordTile.java
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
 * Steal-a-word: The player who activates this tile does not gain any points.
 * Instead all other players get the value of the word.
 * 
 * @author Aaron Reyes
 * 
 */
public class StealWordTile extends AbstractTile {

	/**
	 * Constructor method to set up the information of a steal-word tile
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
	public StealWordTile(char value, int points, Location loc, color color) {
		super(value, points, loc, color);
	}

	@Override
	public int doAbility(List<Player> players, int score) {
		/* if it is not a given player's turn, give them points */
		for (Player player : players) {
			if (!player.getTurn()) {
				player.changeScore(score);
			}
		}
		return 0;
	}

}
