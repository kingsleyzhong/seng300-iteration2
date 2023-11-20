package com.tdc;

/**
 * Represents the situation when a component is emptied of cash but an attempt is
 * made to remove some more.
 *            
 * @author TDC, Inc.
 */
public class NoCashAvailableException extends Exception {
	private static final long serialVersionUID = 7694309140752124939L;

	/**
	 * Default constructor.
	 */
	public NoCashAvailableException() {}
}
