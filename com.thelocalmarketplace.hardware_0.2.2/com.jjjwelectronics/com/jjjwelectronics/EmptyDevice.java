package com.jjjwelectronics;

/**
 * Represents the situation when a device is emptied but an attempt is made to
 * remove something from it.
 * 
 * @author JJJW Electronics LLP
 */
public class EmptyDevice extends Exception {
	private static final long serialVersionUID = 3566954386000387724L;

	/**
	 * Constructor taking a message.
	 * 
	 * @param message
	 *            The detail message to communicate.
	 */
	public EmptyDevice(String message) {
		super(message);
	}
}
