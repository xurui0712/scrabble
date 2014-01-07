/*
 * BoardListener.java
 * Version: 1.0
 * Date: 31 Oct 2013
 * Author: Aaron M. Reyes
 *
 * This content is released under the (http://opensource.org/licenses/MIT) MIT License.
 */

package com.aaronmreyes.scrabble.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.aaronmreyes.scrabble.Main;
import com.aaronmreyes.scrabble.core.Game;
import com.aaronmreyes.scrabble.core.tiles.AbstractTile;

/**
 * The ActionListener for a board tile that is used to update the border around
 * a tile when it is selected and then place the user's seelcted hand tile on
 * the board.
 * 
 * @author Aaron Reyes
 * 
 */
class BoardListener implements ActionListener {
	/* size of outline border */
	private static final int MARGIN_SIZE = 2;
	private final int x;
	private final int y;
	private final Game game;
	private final List<JButton> hand;
	private final JButton[][] board;
	/* a reference to the player's hand panel in the GUI */
	private final JPanel panel;

	/**
	 * Creates a new square listener to get click events at a specific game grid
	 * coordinate.
	 */
	public BoardListener(int x, int y, Game game, JButton[][] board,
			List<JButton> hand, JPanel panel) {
		this.x = x;
		this.y = y;
		this.game = game;
		this.board = board;
		this.hand = hand;
		this.panel = panel;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		/* if the player has tiles to play, then place */
		if (game.getPlayerHand().size() != 0) {
			/* update selected board tile */
			game.updateBoardTile(x, y);
			/* get the tile that was clicked */
			JButton tile = (JButton) event.getSource();
			/* change its border to red to notify user */
			tile.setBorder(BorderFactory.createMatteBorder(MARGIN_SIZE,
					MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, Color.RED));
			/* make sure all other buttons do not have this border */
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board.length; j++) {
					JButton notSelected = board[i][j];
					if (notSelected != tile) {
						notSelected.setBorder(GamePanel.compound);
					}
				}
			}
			Component component = (Component) event.getSource();
			JFrame frame = (JFrame) SwingUtilities.getRoot(component);
			/* save previous hand size to check for valid move later */
			int check = game.getPlayerHand().size();
			/* now place the selected tile */
			game.placeTileOnBoard();
			if (game.getPlayerHand().size() == check) {
				/* display error message */
				String msg = "Invalid Move!";
				Main.showDialog(frame, "ERROR", msg);
			} else {
				/* get the last move made */
				int size = game.getCurrentMove().size();
				AbstractTile move = game.getCurrentMove().get(size - 1);
				/* get coordinates and data */
				int row = move.getLocation().getRow();
				int col = move.getLocation().getCol();
				int pnts = move.getPoints();
				char val = move.getValue();
				/* set the data at the selected square on board GUI */
				board[row][col].setText(String.valueOf(val) + ", "
						+ Integer.toString(pnts));
				/* set its special text if applicable */
				board[row][col].setForeground(GamePanel.getTileColor(move));
				board[row][col].setBackground(GamePanel.BROWN);
				/* update player hand tiles in view */
				for (JButton btn : hand) {
					if (btn.getBorder() == GamePanel.selected) {
						/* remove the Button */
						panel.remove(btn);
						hand.remove(btn);
						break;
					}
				}
			}
			/* update selected hand tile */
			if (hand.size() > 0) {
				hand.get(0).setBorder(GamePanel.selected);
			}
			for (JButton btn : hand) {
				if (hand.indexOf(btn) != 0) {
					btn.setBorder(GamePanel.compound);
				}
			}
			/* update the view of the GUI */
			frame.getContentPane().revalidate();
			frame.getContentPane().repaint();
		}
	}

}
