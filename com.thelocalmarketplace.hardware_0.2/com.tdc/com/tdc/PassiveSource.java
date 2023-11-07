package com.tdc;

/**
 * A simple interface for components that emit cash, not on demand but when some
 * external action has caused it.
 * 
 * @param <T>
 *            The type of the cash to emit.
 *            
 * @author TDC, Inc.
 */
public interface PassiveSource<T> {
	/**
	 * Allows a component to reject an item of cash, forcing it back to the source. Not
	 * all passive sources may support this. Requires power.
	 * 
	 * @param cash
	 *            The item of cash to reject.
	 * @throws DisabledException
	 *             If the component at the end of the channel receiving the cash is
	 *             disabled.
	 * @throws ComponentFailure
	 *             If the component at the end of the channel receiving the cash is not
	 *             capable of rejecting cash.
	 * @throws CashOverloadException
	 *             If the component at the end of the channel receiving the cash is too
	 *             full.
	 */
	public void reject(T cash) throws CashOverloadException, DisabledException, ComponentFailure;
}
