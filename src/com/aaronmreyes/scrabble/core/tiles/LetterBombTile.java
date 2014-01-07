/*
 * LetterBombTile.java
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
 * Letter Bomb: The player's word used is not scored. Only letters used not from
 * the player's hand are counted.
 * 
 * @author Aaron Reyes
 * 
 */
public class LetterBombTile extends AbstractTile {

	/**
	 * Constructor method to set up the information of a letter-bomb tile
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
	public LetterBombTile(char value, int points, Location loc, color color) {
		super(value, points, loc, color);
	}

	@Override
	public int doAbility(List<Player> players, int score) {
		/* find current player */
		for (Player player : players) {
			if (player.getTurn()) {
				/* loop through tiles in move */
				for (AbstractTile tile : player.getMoves()) {
					if ((int) tile.getValue() == (int) this.getValue()) {
						score = score / 2;
					}
				}
			}
		}
		/* change their score */
		for (Player player : players) {
			if (player.getTurn()) {
				player.changeScore(score);
			}
		}
		return 0;
	}

}
