/*
 * GameSetup.java
 * Version: 1.0
 * Date: Tue Oct 29 15:26:29 EDT 2013
 * Author: Aaron M. Reyes
 *
 * This content is released under the (http://opensource.org/licenses/MIT) MIT License.
 */

package edu.cmu.andrew.areyes.scrabble.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.cmu.andrew.areyes.scrabble.Main;
import edu.cmu.andrew.areyes.scrabble.core.Game;

/**
 * This class is used at the start of the scrabble application to get the
 * specifics of the game needed to run the game.
 * 
 * @author Aaron Reyes
 * 
 */
public final class GameSetup extends JPanel {
	private static final long serialVersionUID = 1331456917604013616L;
	/* number of columns for text fields */
	private static final int NUM_COLUMNS = 15;
	/* spacing used between buttons/container objects */
	private static final int COMPONENT_SPACING = 50;
	/* list to gather player names */
	private final List<String> names = new ArrayList<String>();
	private int numPlayers;
	private Game game;
	private boolean extra;

	/**
	 * Constructor method to prompt the user for the necessary game details
	 * before running the game.
	 * 
	 * @param game
	 *            - the {@link Game} to initialize
	 * @param extra
	 *            - a boolean flag used to differentiate if we are playing with
	 *            special tiles or not
	 */
	public GameSetup(Game game, boolean extra) {
		/* clear frame and set background */
		final JLabel background = new JLabel();
		background.setIcon(new ImageIcon(GameSetup.class
				.getResource("/assets/scrabblePieces.jpeg")));
		/* set new layout to be box layout */
		background.setLayout(new BoxLayout(background, BoxLayout.PAGE_AXIS));
		this.add(background);

		/* small JPanel to hold the user input section */
		JPanel info = new JPanel();
		info.setLayout(new FlowLayout());
		info.setBackground(Color.DARK_GRAY);

		/* store the game state for later */
		this.game = game;
		this.extra = extra;

		/* ask how many players are being requested */
		final JLabel prompt = new JLabel("How Many Players?");
		final JTextField valueField = new JTextField();
		final JButton submit = new JButton("Submit");
		valueField.setColumns(NUM_COLUMNS);
		prompt.setForeground(Color.WHITE);

		/* add elements to GUI */
		info.add(prompt);
		info.add(valueField);
		info.add(submit);
		background.add(Box.createVerticalStrut(2 * COMPONENT_SPACING));
		background.add(info);
		info.setAlignmentX(Component.CENTER_ALIGNMENT);
		background.add(Box.createVerticalStrut(8 * COMPONENT_SPACING));

		/* register submit button action listener */
		submit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Component component = (Component) event.getSource();
				JFrame frame = (JFrame) SwingUtilities.getRoot(component);
				/* try to parse the integer input */
				try {
					numPlayers = Integer.parseInt(valueField.getText().trim());
					/* check for correct input value */
					if ((Game.MIN_PLAYERS > numPlayers)
							|| (numPlayers > Game.MAX_PLAYERS)) {
						/* display an error dialog box */
						String err = "Minimum of " + Game.MIN_PLAYERS
								+ " and a maximum of " + Game.MAX_PLAYERS
								+ " players allowed";
						Main.showDialog(frame, "ERROR", err);
					} else {
						/* with valid value, get player names */
						getPlayerName();
					}
				} catch (NumberFormatException e) {
					/* display an error dialog box */
					Main.showDialog(frame, "ERROR", "Please enter a number");
				}
			}
		});
	}

	/*
	 * A method used to get the all player names. called once the user has
	 * selected the number of players.
	 */
	private void getPlayerName() {
		/* get main frame */
		final JFrame frame = (JFrame) SwingUtilities
				.getWindowAncestor(GameSetup.this);
		/* clear frame and set background */
		final JLabel background = new JLabel();
		background.setIcon(new ImageIcon(GameSetup.class
				.getResource("/assets/scrabblePieces.jpeg")));
		/* set new layout to be box layout */
		background.setLayout(new BoxLayout(background, BoxLayout.PAGE_AXIS));

		/* make a new JPanel to hold user input */
		final JPanel data = new JPanel(new FlowLayout());
		data.setBackground(Color.DARK_GRAY);

		/* create GUI elements */
		final JLabel prompt = new JLabel("Enter a Player Name:");
		final JTextField valueField = new JTextField();
		final JButton submit = new JButton("Submit");
		valueField.setColumns(NUM_COLUMNS);
		prompt.setForeground(Color.WHITE);

		/* add them to panel */
		data.add(prompt);
		data.add(valueField);
		data.add(submit);

		/* make another panel to hold players names */
		final JPanel received = new JPanel();
		/* make box centered box layout for names list */
		received.setLayout(new BoxLayout(received, BoxLayout.PAGE_AXIS));
		received.setBackground(Color.DARK_GRAY);
		JLabel title = new JLabel("REGISTERED PLAYERS:");
		title.setForeground(Color.WHITE);
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		received.add(title);
		received.add(Box.createVerticalStrut(10));

		/* register new action listener */
		submit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				/* add name to player list if they entered a value */
				if (valueField.getText().length() == 0) {
					/* inform user of correct values */
					String err = "Please enter a name";
					Main.showDialog(frame, "ERROR", err);
				} else if (names.contains(valueField.getText())) {
					/* inform user of duplicate values */
					String err = "That name has already been taken";
					Main.showDialog(frame, "ERROR", err);
				} else {
					/* store name */
					names.add(valueField.getText());
					/* lower our player name count */
					numPlayers--;
					/* check of we are done */
					if (numPlayers == 0) {
						/* set up the next request */
						valueField.setText("");
						pickStartingPlayer(frame);
					} else {
						/* add the new name to the received panel */
						String msg = "Player " + (numPlayers + 1) + ": "
								+ valueField.getText();
						JLabel newPlayer = new JLabel(msg);
						newPlayer.setForeground(Color.WHITE);
						newPlayer.setAlignmentX(Component.CENTER_ALIGNMENT);
						received.add(newPlayer);
						/* update view */
						frame.validate();
						/* set up the next request */
						valueField.setText("");
					}
				}
			}
		});

		/* combine separate panels */
		background.add(Box.createVerticalStrut(2 * COMPONENT_SPACING));
		background.add(data);
		background.add(Box.createVerticalStrut(COMPONENT_SPACING));
		background.add(received);
		background.add(Box.createVerticalStrut(6 * COMPONENT_SPACING));

		/* set the new panel */
		frame.setContentPane(background);
		frame.validate();
		/* center frame on screen */
		frame.setLocationRelativeTo(null);
	}

	/*
	 * Method used to pick the starting player and set up a new frame with the
	 * game loaded.
	 */
	private void pickStartingPlayer(JFrame frame) {
		/* use a pseudo-random generator to pick starting player */
		Random gen = new Random();
		int goesFirst = gen.nextInt(names.size());
		/* notify players of starting user */
		String msg = "Let's Play! Player " + names.get(goesFirst)
				+ " goes first";
		Main.showDialog(frame, "Scrabble!", msg);
		/* set up the game with the gathered information */
		frame.setVisible(false);
		JFrame match = new JFrame(Main.WINDOW_NAME);
		/* set layout?? */
		match.add(new GamePanel(this.game, this.names, this.extra, names
				.get(goesFirst)));
		/* center frame on screen */
		match.pack();
		match.setLocationRelativeTo(null);
		match.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		match.setResizable(false);
		match.setVisible(true);
	}

}
