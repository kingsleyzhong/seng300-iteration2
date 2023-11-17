package com.tdc;

/**
 * Represents situations where a component has been overloaded with cash.
 * 
 * @author TDC, Inc.
 */
public class CashOverloadException extends Exception {
	private static final long serialVersionUID = -8154114758538868332L;

	/**
	 * Create an exception without an error message.
	 */
	public CashOverloadException() {}

	/**
	 * Create an exception with an error message.
	 * 
	 * @param message
	 *            The error message to use.
	 */
	public CashOverloadException(String message) {
		super(message);
	}
}
