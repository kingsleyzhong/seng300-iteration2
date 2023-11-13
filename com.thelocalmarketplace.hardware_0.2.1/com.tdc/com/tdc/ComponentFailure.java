package com.tdc;

/**
 * Issued when a component cannot perform its usual functions because it has
 * malfunctioned.
 *            
 * @author TDC, Inc.
 */
public class ComponentFailure extends RuntimeException {
	private static final long serialVersionUID = 4472863036964494556L;

	/**
	 * Default constructor.
	 */
	public ComponentFailure() {
		super();
	}

	/**
	 * Constructor permitting a message to be specified.
	 * 
	 * @param message
	 *            The message to pass on.
	 */
	public ComponentFailure(String message) {
		super(message);
	}
}
