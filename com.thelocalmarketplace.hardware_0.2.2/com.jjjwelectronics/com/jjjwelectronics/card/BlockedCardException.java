package com.jjjwelectronics.card;

/**
 * Represents exceptions arising from a blocked card.
 * 
 * @author JJJW Electronics LLP
 */
public class BlockedCardException extends SecurityException {
	private static final long serialVersionUID = 8824192400137175094L;

	/**
	 * Create an exception.
	 */
	public BlockedCardException() {}
}
