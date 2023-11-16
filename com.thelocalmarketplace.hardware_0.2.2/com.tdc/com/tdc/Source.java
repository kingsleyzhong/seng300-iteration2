package com.tdc;

import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * A simple interface for components that emit cash.
 * 
 * @param <T>
 *            The type of the cash to emit.
 *            
 * @author TDC, Inc.
 */
public interface Source<T> {
	/**
	 * Instructs the component to emit one arbitrary item of cash, meaning that the
	 * component stores a set of items of cash and one of them is to be emitted.
	 * Requires power.
	 * 
	 * @throws DisabledException
	 *             If the component is disabled.
	 * @throws NoCashAvailableException
	 *             If the component is empty and cannot emit.
	 * @throws CashOverloadException
	 *             If the receiving component is already full.
	 */
	public void emit() throws DisabledException, NoCashAvailableException, CashOverloadException;

	/**
	 * Instructs the component to pass one specific item of cash backwards. Requires
	 * power.
	 * 
	 * @param cash
	 *            The item of cash to be emitted.
	 * @throws DisabledException
	 *             If the component is disabled.
	 * @throws SimulationException
	 *             If the item of cash is null.
	 * @throws CashOverloadException
	 *             If the receiving component is already full.
	 */
	public void reject(T cash) throws DisabledException, CashOverloadException;
}
