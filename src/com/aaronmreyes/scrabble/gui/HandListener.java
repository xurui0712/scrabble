/*
 * HandListener.java
 * Version: 1.0
 * Date: 31 Oct 2013
 * Author: Aaron M. Reyes
 *
 * This content is released under the (http://opensource.org/licenses/MIT) MIT License.
 */

package com.aaronmreyes.scrabble.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.aaronmreyes.scrabble.core.Game;

/**
 * The ActionListener for a hand tile that is used to update the border around a
 * tile when it is selected.
 * 
 * @author Aaron Reyes
 * 
 */
class HandListener implements ActionListener {
	/* size of outline border */
	static final int MARGIN_SIZE = 2;
	private final Game game;
	private final List<JButton> hand;

	/**
	 * Creates a new hand listener to get click events at a specific game hand
	 * coordinate in the current player's hand
	 */
	public HandListener(Game game, List<JButton> hand) {
		this.game = game;
		this.hand = hand;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		/* update selected board tile */
		int index = hand.indexOf(event.getSource());
		game.updateHandTile(index);
		/* get the tile that was clicked */
		JButton tile = (JButton) event.getSource();
		/* change its border to red to notify user */
		tile.setBorder(GamePanel.selected);
		/* make sure all other buttons do not have this border */
		for (int i = 0; i < hand.size(); i++) {
			JButton notSelected = hand.get(i);
			/* check to make sure tile isn't played already too */
			if (notSelected != tile) {
				notSelected.setBorder(GamePanel.compound);
			}
		}
		/* update the view of the GUI */
		Component component = (Component) event.getSource();
		JFrame frame = (JFrame) SwingUtilities.getRoot(component);
		frame.getContentPane().revalidate();
	}

}
