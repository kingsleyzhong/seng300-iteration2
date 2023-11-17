package com.tdc.coin;

import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Represents a component that stores coins of a particular denomination to
 * dispense them as change. This component cannot be automatically refilled from
 * its source.
 *            
 * @author TDC, Inc.
 */
public final class CoinDispenserBronze extends AbstractCoinDispenser {
	/**
	 * Creates a coin dispenser with the indicated maximum capacity.
	 * 
	 * @param capacity
	 *            The maximum number of coins that can be stored in the dispenser.
	 *            Must be positive.
	 * @throws SimulationException
	 *             if capacity is not positive.
	 */
	public CoinDispenserBronze(int capacity) {
		super(capacity);
	}
}
