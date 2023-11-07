package com.jjjwelectronics;

/**
 * Represents situations where a device has been overloaded, in terms of weight,
 * quantity of items, etc.
 * 
 * @author JJJW Electronics LLP
 */
public class OverloadedDevice extends Exception {
	private static final long serialVersionUID = 7813659161520664284L;

	/**
	 * Create an exception without an error message.
	 */
	public OverloadedDevice() {}

	/**
	 * Create an exception with an error message.
	 * 
	 * @param message
	 *            The error message to use.
	 */
	public OverloadedDevice(String message) {
		super(message);
	}
}
