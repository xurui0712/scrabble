/*
 * Main.java
 * Version: 1.0
 * Date: 21 Oct 2013
 * Author: Aaron M. Reyes
 *
 * This content is released under the (http://opensource.org/licenses/MIT) MIT License.
 */

package edu.cmu.andrew.areyes.scrabble;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.cmu.andrew.areyes.scrabble.core.Game;
import edu.cmu.andrew.areyes.scrabble.gui.GamePanel;
import edu.cmu.andrew.areyes.scrabble.gui.GameSetup;

/**
 * This is the main entry point to the scrabble application.
 * 
 * @author Aaron Reyes
 * 
 */
public class Main {
	/* the size of the scrabblePieces.jpeg image used as the background */
	private static final int BACKGROUND_SIZE = 500;
	/* the size the buffer between buttons on the main screen */
	private static final int BUTTON_SPACING = 50;
	public static final String WINDOW_NAME = "Scrabble";

	/**
	 * The main entry point to the application.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				runScrabble();
			}
		});
	}

	/**
	 * Method called to set up the scrabble main page ({@link GamePanel})
	 */
	private static void runScrabble() {
		/* Create a new JFrame with title WINDOW_NAME */
		final JFrame frame = new JFrame(WINDOW_NAME);
		frame.setSize(BACKGROUND_SIZE, BACKGROUND_SIZE);
		/* center frame on screen */
		frame.setLocationRelativeTo(null);
		/* set background image */
		final JLabel background = new JLabel();
		background.setIcon(new ImageIcon(Main.class
				.getResource("/assets/scrabblePieces.jpeg")));
		JPanel panel = (JPanel) frame.getContentPane();
		panel.add(background);

		/* Add in first row the logo image, centered */
		JLabel logo = new JLabel();
		background.setLayout(new BoxLayout(background, BoxLayout.PAGE_AXIS));
		logo.setIcon(new ImageIcon(Main.class.getResource("/assets/logo.png")));
		logo.setAlignmentX(Component.CENTER_ALIGNMENT);
		background.add(Box.createVerticalStrut(BUTTON_SPACING));
		background.add(logo);
		background.add(Box.createVerticalStrut(BUTTON_SPACING));

		/* add in the JButton for a normal game, centered */
		JButton normal = new JButton("Play Original");
		JButton special = new JButton("Play with Special Tiles");
		normal.setAlignmentX(Component.CENTER_ALIGNMENT);
		special.setAlignmentX(Component.CENTER_ALIGNMENT);
		background.add(normal);
		background.add(Box.createVerticalStrut(BUTTON_SPACING));
		background.add(special);
		
		/* add a button for game info */
		JButton help = new JButton("How to play");
		help.setAlignmentX(Component.CENTER_ALIGNMENT);
		background.add(Box.createVerticalStrut(BUTTON_SPACING));
		background.add(help);

		final Game game = new Game();
		/* register event listeners */
		normal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				/* set up a game with no special tiles */
				frame.setContentPane(new GameSetup(game, false));
				frame.getContentPane().setBackground(Color.BLACK);
				frame.getContentPane().revalidate();
				frame.getContentPane().repaint();
			}
		});
		special.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				/* set up a game with special tiles */
				frame.setContentPane(new GameSetup(game, true));
				frame.getContentPane().setBackground(Color.BLACK);
				frame.getContentPane().revalidate();
				frame.getContentPane().repaint();
			}
		});
		help.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				/* try to open the game pdf file */
				if (Desktop.isDesktopSupported()) {
					try {
						/* get the pdf */
						InputStream manuel = Main.class.getResourceAsStream("/assets/manuel.pdf");
			            /* make temp file */
			            String tempFile = "scrabble";
			            File temp = File.createTempFile(tempFile, ".pdf");
			            /* write the pdf data to the temp file */
			            FileOutputStream fos = new FileOutputStream(temp);
			            while (manuel.available() > 0) {
			            	fos.write(manuel.read());
			            }
			            fos.flush();
			            fos.close();
			            manuel.close();
			            /* now the OS can open the manuel */
						Desktop.getDesktop().open(temp);
					} catch (IOException ex) {
						/* no application registered for PDFs */
						final String err = "Cannot open manual!"
								+ " You need to have PDF software installed.";
						Main.showDialog(frame, "ERROR", err);
					}
				}
			}
		});

		/* The entire program will exit when you close the window */
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
	}

	/**
	 * A simple method used to display a notification to the user
	 */
	public static void showDialog(Component component, String title,
			String message) {
		ImageIcon logo = new ImageIcon(
				Main.class.getResource("/assets/icon.png"));
		JOptionPane.showOptionDialog(component, message, title,
				JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, logo,
				null, message);
	}
}
