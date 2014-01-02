/*
 * DoubleLetter.java
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
 * An {@link AbilityTile} that takes a specific tile placed at the same location
 * as this tile and doubles that letters point value for the score.
 * 
 * @author Aaron Reyes
 * 
 */
public class DoubleLetter extends AbilityTile {

	/**
	 * Constructor method for a DoubleLetter tile.
	 * 
	 * @param loc
	 *            - the location of the Ability tile
	 * @param priority
	 *            - the priority of the Ability tile
	 * @param color
	 *            - the color in {@link colors} of a given tile for the GUI
	 */
	public DoubleLetter(Location loc, int priority, color color) {
		super(loc, priority, color);
	}

	@Override
	public int doAbility(List<Player> players, int score) {
		/* add another point value of this tile to the total score */
		int row = this.getLocation().getRow();
		int col = this.getLocation().getCol();
		/* find the player whose turn it is */
		for (Player player : players) {
			if (player.getTurn()) {
				/* find tile with same location in player's moves */
				int rowCheck, colCheck;
				for (AbstractTile tile : player.getMoves()) {
					rowCheck = tile.getLocation().getRow();
					colCheck = tile.getLocation().getCol();
					if ((row == rowCheck) && (col == colCheck)) {
						/* add point value of this tile to the score */
						return score + tile.getPoints();
					}
				}
			}
		}
		/* error occurred, just return the same score */
		return score;
	}
}
