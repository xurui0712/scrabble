/*
 * Location.java
 * Version: 1.0
 * Date: 10 Oct 2013
 * Author: Aaron M. Reyes
 *
 * This content is released under the (http://opensource.org/licenses/MIT) MIT License.
 */

package com.aaronmreyes.scrabble.core;

import com.aaronmreyes.scrabble.core.tiles.AbstractTile;

/**
 * Class that acts as a struct to contain a given (row, col) coordinate on the
 * scrabble board.
 * 
 * @author Aaron Reyes
 * 
 */
public final class Location implements Cloneable {
	private int row;
	private int col;

	/**
	 * Constructor method to set up the information of a Location
	 * 
	 * @param row
	 *            - Row on the board
	 * @param col
	 *            - Column on the board
	 * @param tile
	 *            - the {@link AbstractTile} at (row, column) on the board
	 */
	protected Location(int row, int col) {
		this.row = row;
		this.col = col;
	}

	/**
	 * Used by GUI: Getter method for the row of a location
	 * 
	 * @return row where (row, column) is a location on the board
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Used by GUI: Getter method for the column of a location
	 * 
	 * @return column where (row, column) is a location on the board
	 */
	public int getCol() {
		return col;
	}

	/**
	 * Method to clone a given location.
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			/* This should never happen */
			throw new InternalError(e.toString());
		}
	}
}
