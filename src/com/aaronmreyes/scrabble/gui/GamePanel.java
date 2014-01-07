/*
 * GamePanel.java
 * Version: 1.0
 * Date: Tue Oct 29 15:26:29 EDT 2013
 * Author: Aaron M. Reyes
 *
 * This content is released under the (http://opensource.org/licenses/MIT) MIT License.
 */

package com.aaronmreyes.scrabble.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com.aaronmreyes.scrabble.Main;
import com.aaronmreyes.scrabble.core.Game;
import com.aaronmreyes.scrabble.core.Player;
import com.aaronmreyes.scrabble.core.tiles.AbilityTile;
import com.aaronmreyes.scrabble.core.tiles.AbstractTile;
import com.aaronmreyes.scrabble.core.tiles.NormalTile;
import com.aaronmreyes.scrabble.core.tiles.AbstractTile.color;

/**
 * GUI class used to run the main game panel with board, player info and player
 * hand. Maintains all action listeners for a given game and then redirects to
 * the {@link Main} class when a game is over.
 * 
 * @author Aaron Reyes
 * 
 */
public class GamePanel extends JPanel {
	private static final long serialVersionUID = 2274869219936427299L;
	/* various constants needed: */
	private static final int BUTTON_SPACING = 30;
	private static final int TILE_BORDER = 10;
	private static final int TILE_SPACING = 3;
	static final int TILE_SIZE = 40;
	/* A custom default color for wooden-looking tiles: R:222 G:184 B:135 */
	static final Color BROWN = new Color(222, 184, 135);
	private static final Font font = new Font(Font.MONOSPACED, Font.BOLD, 16);
	/* default borders used in GUI for selected buttons */
	private static final Border loweredEtched = BorderFactory
			.createEtchedBorder(EtchedBorder.LOWERED);
	private static final Border emptySpacing = BorderFactory.createEmptyBorder(
			TILE_BORDER, 0, TILE_BORDER, 0);
	static final Border selected = BorderFactory.createMatteBorder(
			HandListener.MARGIN_SIZE, HandListener.MARGIN_SIZE,
			HandListener.MARGIN_SIZE, HandListener.MARGIN_SIZE, Color.RED);
	static final Border compound = BorderFactory.createCompoundBorder(
			loweredEtched, emptySpacing);
	/* out own reference to the game */
	private Game game;
	/* references to the buttons in board and player panels */
	private JButton[][] squares;
	private List<JButton> hand;
	/* references to the various panels in the border layout */
	private JPanel player;
	private JPanel board;
	private JPanel options;
	private JPanel stats;
	/* simple flag to make sure the first move is done correctly */
	private boolean firstMove = true;

	/**
	 * Constructor method used to initialize the game panel screen
	 * 
	 * @param match
	 *            - the {@link Game} created during {@link GameSetup}
	 * @param players
	 *            - the list of player names from {@link GameSetup}
	 * @param extra
	 *            - the flag received from {@link GameSetup}
	 * @param startingPlayer
	 *            - the selected starting player from {@link GameSetup}
	 */
	public GamePanel(Game match, List<String> players, boolean extra,
			String startingPlayer) {

		/* store game info and set up game */
		this.game = match;
		game.setupGame(players, extra);
		game.setStartingPlayer(startingPlayer);

		/* Initialize board button array */
		int size = match.getBoard().length;
		squares = new JButton[size][size];

		/* arrange the GUI */
		arrangeGUI();
	}

	/*
	 * Method that arranges all the panels in the BoardPanel into their places
	 * with a BorderLayout
	 */
	private void arrangeGUI() {
		setLayout(new BorderLayout());
		/* assign references */
		player = currentPlayerInfo();
		board = createBoardPanel();
		stats = gameInfo();
		/* fill GUI */
		add(player, BorderLayout.SOUTH);
		add(board, BorderLayout.WEST);
		add(stats, BorderLayout.EAST);
	}

	/*
	 * Method used to set up the right panel with the other player's info.
	 */
	private JPanel gameInfo() {
		/* next add in text area with the current game stats */
		JPanel gameStats = new JPanel();
		gameStats.setLayout(new BoxLayout(gameStats, BoxLayout.Y_AXIS));
		gameStats.setBorder(BorderFactory.createEmptyBorder(TILE_BORDER,
				TILE_BORDER, TILE_BORDER, TILE_BORDER));
		JLabel stats = new JLabel();
		stats.setFont(font);
		stats.setForeground(Color.WHITE);

		/* get each player score */
		String msg = " Current Game Statistics: <br>";
		for (Player player : game.getPlayers()) {
			msg += ("<br> Player: " + player.getName() + "<p> Score: "
					+ Integer.toString(player.getScore()) + "<br>");
		}
		/* add in the number of tiles left in letterBank */
		msg += "<br><br> Tiles Left: " + game.getNumTilesLeft();
		stats.setText("<html>" + msg + "</html>");

		gameStats.add(stats);
		stats.setAlignmentX(Component.CENTER_ALIGNMENT);
		/* get player options and add that to game panel */
		JPanel opt = playerOptions();
		gameStats.add(opt);
		opt.setAlignmentX(Component.CENTER_ALIGNMENT);
		gameStats.setBackground(Color.DARK_GRAY);
		return gameStats;
	}

	/*
	 * Method to create the Bottom panel that holds the current player's hand.
	 */
	private JPanel currentPlayerInfo() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.setBorder(BorderFactory.createEmptyBorder(0, TILE_BORDER,
				TILE_BORDER, TILE_BORDER));
		/* get player hand */
		List<AbstractTile> hand = game.getPlayerHand();
		/* add in a hand label for user */
		JTextArea handLabel = new JTextArea(" HAND for "
				+ game.getCurrentPlayer().getName() + ": ");
		handLabel.setFont(font);
		handLabel.setForeground(Color.DARK_GRAY);
		handLabel.setEditable(false);
		panel.add(handLabel);
		/* display player hand as JButtons */
		List<JButton> buttons = new ArrayList<JButton>();
		AbstractTile tile;
		JButton tileButton;
		String value;
		for (int i = 0; i < hand.size(); i++) {
			tile = hand.get(i);
			Color color = getTileColor(tile);
			/* create each tile in hand with default settings */
			tileButton = new JButton();
			tileButton.setOpaque(true);
			tileButton.setBackground(BROWN);
			tileButton.setForeground(color);
			value = String.valueOf(tile.getValue()) + ", "
					+ Integer.toString(tile.getPoints());
			tileButton.setText(value);
			/* check for the default selected hand tile */
			if (i == 0) {
				tileButton.setBorder(selected);
			} else {
				tileButton.setBorder(compound);
			}
			tileButton.addActionListener(new HandListener(game, buttons));
			/* to ensure each button is the same */
			tileButton.setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
			/* keep track of what we have seen */
			buttons.add(tileButton);
			panel.add(tileButton);
		}
		/* store reference to the list of hand tiles for later */
		this.hand = buttons;
		panel.setBackground(Color.DARK_GRAY);
		return panel;
	}

	/*
	 * Method used to create the western panel that holds the board of the game.
	 */
	private JPanel createBoardPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(squares.length, squares.length,
				TILE_SPACING, TILE_SPACING));
		panel.setBorder(BorderFactory.createEmptyBorder(TILE_BORDER,
				TILE_BORDER, TILE_BORDER, TILE_BORDER));
		String value;
		/* first get the current player */
		Player current = game.getCurrentPlayer();
		/* create all tiles for display */
		for (int i = 0; i < squares.length; i++) {
			for (int j = 0; j < squares.length; j++) {
				/* empty tile */
				AbstractTile tile = game.getBoard()[i][j];
				if (tile == null) {
					squares[i][j] = new JButton();
					/* set default background color: */
					squares[i][j].setBackground(BROWN);
				} else {
					Color color = getTileColor(tile);
					/* check for tile type/creator */
					if (!(tile instanceof AbilityTile)
							&& !(tile instanceof NormalTile)) {
						squares[i][j] = new JButton();
						/* set default background color: */
						squares[i][j].setBackground(BROWN);
						/* set button text */
						value = String.valueOf(tile.getValue()) + ", "
								+ Integer.toString(tile.getPoints());
						squares[i][j].setText(value);
						/* this is a special/trap tile, check creator */
						String creator = tile.getCreator();
						if (current.getName().equals(creator)) {
							/* display trap */
							squares[i][j].setForeground(color);
						}
					} else if (tile instanceof AbilityTile) {
						squares[i][j] = new JButton();
						/* set background color of button to this color */
						squares[i][j].setBackground(color);
					}
				}
				/* make button see through for color assignment */
				squares[i][j].setOpaque(true);
				squares[i][j].setPreferredSize(new Dimension(TILE_SIZE,
						TILE_SIZE));
				/* check for the default selected hand tile */
				if ((i == squares.length / 2) && (j == squares.length / 2)) {
					squares[i][j].setBorder(selected);
				} else {
					squares[i][j].setBorder(compound);
				}
				/* remove old action listeners */
				for (ActionListener act : squares[i][j].getActionListeners()) {
					squares[i][j].removeActionListener(act);
				}
				/* add in new one */
				squares[i][j].addActionListener(new BoardListener(i, j, game,
						squares, hand, player));
				panel.add(squares[i][j]);
			}
		}
		panel.setBackground(Color.DARK_GRAY);
		return panel;
	}

	/*
	 * Method used to set up the right panel buttons inside of options panel.
	 */
	private JPanel playerOptions() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(0, TILE_BORDER,
				TILE_BORDER, TILE_BORDER));
		/* add a buttons for player options during move */
		JButton mixHand = new JButton("Mix Hand");
		mixHand.setAlignmentX(Component.CENTER_ALIGNMENT);
		JButton play = new JButton("Play Word");
		play.setAlignmentX(Component.CENTER_ALIGNMENT);
		JButton exchange = new JButton("Exchange Tiles");
		exchange.setAlignmentX(Component.CENTER_ALIGNMENT);
		JButton undo = new JButton("Undo Move");
		undo.setAlignmentX(Component.CENTER_ALIGNMENT);
		JButton pass = new JButton("Pass");
		pass.setAlignmentX(Component.CENTER_ALIGNMENT);

		/* ********** register action listeners ********** */
		mixHand.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Component component = (Component) event.getSource();
				JFrame frame = (JFrame) SwingUtilities.getRoot(component);
				mixTiles(frame);
			}
		});

		undo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Component component = (Component) event.getSource();
				JFrame frame = (JFrame) SwingUtilities.getRoot(component);
				undoMove(frame);
			}
		});

		play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Component component = (Component) event.getSource();
				JFrame frame = (JFrame) SwingUtilities.getRoot(component);
				playMove(frame);
			}
		});

		exchange.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Component component = (Component) event.getSource();
				JFrame frame = (JFrame) SwingUtilities.getRoot(component);
				exchangeTiles(frame);
			}
		});

		pass.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Component component = (Component) event.getSource();
				JFrame frame = (JFrame) SwingUtilities.getRoot(component);
				passTurn(frame);
			}
		});
		/* *********************************************** */

		/* add in the components to sub-panel */
		panel.add(Box.createVerticalStrut(BUTTON_SPACING));
		panel.add(exchange);
		panel.add(Box.createVerticalStrut(BUTTON_SPACING));
		panel.add(play);
		panel.add(Box.createVerticalStrut(BUTTON_SPACING));
		panel.add(mixHand);
		panel.add(Box.createVerticalStrut(BUTTON_SPACING));
		panel.add(undo);
		panel.add(Box.createVerticalStrut(BUTTON_SPACING));
		panel.add(pass);
		panel.add(Box.createVerticalStrut(BUTTON_SPACING));
		panel.setBackground(Color.DARK_GRAY);
		panel.setForeground(Color.WHITE);
		options = panel;
		return panel;
	}

	// **************** methods used in action listeners ****************

	/*
	 * Method called when a player wants to pass/end their turn abruptly
	 */
	private void passTurn(JFrame frame) {
		/* undo any old move made */
		while ((game.getPlayerHand().size() != Game.HAND_LIMIT)
				&& (game.getNumTilesLeft() != 0)) {
			game.removeTilefromBoard();
		}
		/* change turn */
		game.changeTurn();
		String name = game.getCurrentPlayer().getName();
		Main.showDialog(frame, "Results", "It is now " + name + "'s turn");
		/* change view for new player */
		JPanel next = new JPanel(new BorderLayout());
		/* assign references */
		player = currentPlayerInfo();
		board = createBoardPanel();
		stats = gameInfo();
		/* fill GUI */
		next.add(player, BorderLayout.SOUTH);
		next.add(board, BorderLayout.WEST);
		next.add(stats, BorderLayout.EAST);
		frame.setContentPane(next);
		/* update GUI */
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
	}

	/*
	 * Method called when a player wishes to make a move
	 */
	private void playMove(JFrame frame) {
		/* check for a move to play */
		if (game.getCurrentMove().size() > 0) {
			/* old number of tiles left */
			int oldNumLeft = game.getNumTilesLeft();
			/* get player's old score */
			int oldScore = game.getCurrentPlayer().getScore();
			/* make sure the first move uses center tile */
			if (firstMove) {
				int row, col;
				int limit = squares.length / 2;
				boolean flag = false;
				for (AbstractTile tile : game.getCurrentMove()) {
					row = tile.getLocation().getRow();
					col = tile.getLocation().getCol();
					if ((row == limit) && (col == limit)) {
						flag = true;
						break;
					}
				}
				if (!flag) {
					/* if we make it here, then invalid move */
					String err = "First move must use center tile.";
					Main.showDialog(frame, "ERROR", err);
					game.undoMove();
					/* change view for player */
					JPanel next = new JPanel(new BorderLayout());
					/* assign references */
					player = currentPlayerInfo();
					board = createBoardPanel();
					stats = gameInfo();
					/* fill GUI */
					next.add(player, BorderLayout.SOUTH);
					next.add(board, BorderLayout.WEST);
					next.add(stats, BorderLayout.EAST);
					frame.setContentPane(next);
					/* update the view of the GUI */
					frame.getContentPane().revalidate();
					frame.getContentPane().repaint();
					return; // exit
				}
			}
			game.playWord(); // PLAY THE WORD!
			/* check if tiles were drawn */
			if ((game.getNumTilesLeft() != oldNumLeft)
					|| (game.getNumTilesLeft() == 0)) {
				/* check for game over */
				List<Player> winners = game.checkForWin();
				/* clear first move flag */
				if (firstMove) {
					firstMove = false;
				}
				if (winners == null) {
					/* inform player of score for move */
					int score = game.getCurrentPlayer().getScore() - oldScore;
					String msg = "You recieved " + String.valueOf(score)
							+ " points! Your current score is now "
							+ game.getCurrentPlayer().getScore() + ".";
					Main.showDialog(frame, "Results", msg);
					/* change turns */
					game.changeTurn();
					String name = game.getCurrentPlayer().getName();
					Main.showDialog(frame, "Results", "It is now " + name
							+ "'s turn");
					/* change view for new player */
					JPanel next = new JPanel(new BorderLayout());
					/* assign references */
					player = currentPlayerInfo();
					board = createBoardPanel();
					stats = gameInfo();
					/* fill GUI */
					next.add(player, BorderLayout.SOUTH);
					next.add(board, BorderLayout.WEST);
					next.add(stats, BorderLayout.EAST);
					frame.setContentPane(next);
				} else {
					/* display winner(s) */
					String msg;
					if (winners.size() > 1) {
						msg = "Congratulations! It was a tie game between"
								+ " the following players: \n";
						/* get player names */
						for (Player player : winners) {
							msg += "\n" + player.getName();
						}
					} else {
						/* single winner */
						String name = winners.get(0).getName();
						msg = "Congratulations! The winner is " + name;
					}
					/* add final scores */
					msg += "\n FINAL SCORES:";
					for (Player player : game.getPlayers()) {
						msg += "\n " + player.getName() + ": "
								+ player.getScore();
					}
					/* display notification */
					Main.showDialog(frame, "WINNER", msg);
					/* Load the main screen again */
					frame.setVisible(false); // you can't see me!
					frame.dispose();
					Main.main(null);
				}
			} else {
				/* display error message */
				Main.showDialog(frame, "ERROR", "Invalid Word");
				/* change view for new player */
				JPanel next = new JPanel(new BorderLayout());
				/* assign references */
				player = currentPlayerInfo();
				board = createBoardPanel();
				stats = gameInfo();
				/* fill GUI */
				next.add(player, BorderLayout.SOUTH);
				next.add(board, BorderLayout.WEST);
				next.add(stats, BorderLayout.EAST);
				frame.setContentPane(next);
			}
			/* update the view of the GUI */
			frame.getContentPane().revalidate();
			frame.getContentPane().repaint();
		}
	}

	/*
	 * Method called when a player wants to undo a move made
	 */
	private void undoMove(JFrame frame) {
		/* get the last move made */
		int size = game.getCurrentMove().size();
		if (size > 0) {
			AbstractTile undo = game.getCurrentMove().get(size - 1);
			int row = undo.getLocation().getRow();
			int col = undo.getLocation().getCol();
			/* update core */
			game.removeTilefromBoard();
			/* update GUI hand */
			JButton old = new JButton();
			old.setOpaque(true);
			old.setBackground(GamePanel.BROWN);
			old.setForeground(getTileColor(undo));
			String value = String.valueOf(undo.getValue()) + ", "
					+ Integer.toString(undo.getPoints());
			old.setText(value);
			/* check default selected hand tile */
			if (hand.size() == 0) {
				old.setBorder(GamePanel.selected);
			} else {
				old.setBorder(GamePanel.compound);
			}
			old.addActionListener(new HandListener(game, hand));
			/* to ensure each button is the same */
			old.setPreferredSize(new Dimension(GamePanel.TILE_SIZE,
					GamePanel.TILE_SIZE));
			/* update hand and GUI with new tile */
			player.add(old);
			hand.add(old);
			/* now update board state */
			squares[row][col].setText("");
			/* reset ability tile background color */
			if (game.getBoard()[row][col] != null) {
				Color bg = getTileColor(game.getBoard()[row][col]);
				squares[row][col].setBackground(bg);
			}
			/* update view */
			frame.getContentPane().revalidate();
			frame.getContentPane().repaint();
		}
	}

	/*
	 * Method called when a player wishes to mix the tiles in their hand
	 */
	private void mixTiles(JFrame frame) {
		/* mix tiles */
		game.mixHand();
		/* update tiles in GUI for updated player hand */
		int index = 0;
		JButton btn;
		String value;
		for (AbstractTile tile : game.getPlayerHand()) {
			value = String.valueOf(tile.getValue()) + ", "
					+ Integer.toString(tile.getPoints());
			/* look in our reference to the hand of buttons */
			btn = hand.get(index);
			Color color = getTileColor(tile);
			btn.setText(value);
			btn.setForeground(color);
			index++;
		}
		/* update view */
		frame.getContentPane().revalidate();
	}

	/*
	 * Method called when a player wishes to exchange tiles with the letterBank
	 */
	private void exchangeTiles(JFrame frame) {
		/* undo any old move made */
		while ((game.getPlayerHand().size() != Game.HAND_LIMIT)
				&& (game.getNumTilesLeft() != 0)) {
			game.removeTilefromBoard();
		}
		/* change view for player */
		JPanel next = new JPanel(new BorderLayout());
		/* assign references */
		player = currentPlayerInfo();
		board = createBoardPanel();
		stats = gameInfo();
		/* fill GUI */
		next.add(player, BorderLayout.SOUTH);
		next.add(board, BorderLayout.WEST);
		next.add(stats, BorderLayout.EAST);
		frame.setContentPane(next);
		/* update GUI */
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
		/* first disable all board buttons */
		for (int i = 0; i < squares.length; i++) {
			for (int j = 0; j < squares.length; j++) {
				squares[i][j].setEnabled(false);
			}
		}
		/* clear all hand event listeners and replace them with... */
		for (JButton btn : hand) {
			for (ActionListener act : btn.getActionListeners()) {
				btn.setBorder(compound); // reset borders
				btn.removeActionListener(act);
			}
			btn.addActionListener(new ActionListener() {
				/* ...a custom listener that marks each tile selected */
				@Override
				public void actionPerformed(ActionEvent event) {
					int index = hand.indexOf(event.getSource());
					/* get the tile that was clicked */
					JButton tile = (JButton) event.getSource();
					if (tile.getBorder() != selected) {
						/* change its border to red to notify user */
						tile.setBorder(GamePanel.selected);
						/* add this tile to exchange List */
						game.addExchangeTile(index);
					} else {
						/* change its border to red to notify user */
						tile.setBorder(GamePanel.compound);
						/* add this tile to exchange List */
						game.removeExchangeTile(index);

					}
					/* update the view of the GUI */
					Component component = (Component) event.getSource();
					JFrame frame = (JFrame) SwingUtilities.getRoot(component);
					frame.getContentPane().revalidate();
					frame.getContentPane().repaint();
				}
			});
		}
		options.removeAll();
		JButton done = new JButton("Done");

		/* register small quick action listeners */
		done.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Component component = (Component) event.getSource();
				JFrame frame = (JFrame) SwingUtilities.getRoot(component);
				/* exchange tiles */
				game.exchangeTiles();
				/* change turns */
				game.changeTurn();
				String name = game.getCurrentPlayer().getName();
				Main.showDialog(frame, "Results", "It is now " + name
						+ "'s turn");
				/* make all board tiles active */
				for (int i = 0; i < squares.length; i++) {
					for (int j = 0; j < squares.length; j++) {
						squares[i][j].setEnabled(true);
					}
				}
				/* change view for new player */
				JPanel next = new JPanel(new BorderLayout());
				/* assign references */
				player = currentPlayerInfo();
				board = createBoardPanel();
				stats = gameInfo();
				/* fill GUI */
				next.add(player, BorderLayout.SOUTH);
				next.add(board, BorderLayout.WEST);
				next.add(stats, BorderLayout.EAST);
				frame.setContentPane(next);
				/* update GUI */
				frame.getContentPane().revalidate();
				frame.getContentPane().repaint();
			}
		});

		/* place component on GUI */
		options.add(Box.createVerticalStrut(2 * BUTTON_SPACING));
		options.add(done);
		options.add(Box.createVerticalStrut(2 * BUTTON_SPACING));
		done.setAlignmentX(Component.CENTER_ALIGNMENT);
		/* update GUI */
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
	}

	// *******************************************************************

	/*
	 * A simple method used to get a tile's defining color for display
	 */
	static protected Color getTileColor(AbstractTile tile) {
		/* get its color */
		color color = tile.getColor();
		switch (color) {
		case RED:
			return Color.RED;
		case CYAN:
			return Color.CYAN;
		case MAGENTA:
			return Color.MAGENTA;
		case GREEN:
			return Color.GREEN;
		case BLUE:
			return Color.BLUE;
		default:
			return Color.BLACK;
		}
	}

}
