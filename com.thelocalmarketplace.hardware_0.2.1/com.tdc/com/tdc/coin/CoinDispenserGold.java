package com.tdc.coin;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.PassiveSource;
import com.tdc.Sink;

import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;

/**
 * Represents a component that stores coins of a particular denomination to
 * dispense them as change. This component can be automatically refilled from its
 * source.
 *            
 * @author TDC, Inc.
 */
public final class CoinDispenserGold extends AbstractCoinDispenser implements Sink<Coin> {
	/**
	 * Represents the input source of this dispenser.
	 */
	public PassiveSource<Coin> source;
	
	/**
	 * Creates a coin dispenser with the indicated maximum capacity.
	 * 
	 * @param capacity
	 *            The maximum number of coins that can be stored in the dispenser.
	 *            Must be positive.
	 * @throws SimulationException
	 *             if capacity is not positive.
	 */
	public CoinDispenserGold(int capacity) {
		super(capacity);
	}

	/**
	 * Causes the indicated coin to be added into the dispenser. If successful,
	 * announces "coinAdded" event. If a successful coin addition causes the
	 * dispenser to become full, announces "coinsFull" event. Requires power.
	 * 
	 * @throws DisabledException
	 *             If the coin dispenser is currently disabled.
	 * @throws SimulationException
	 *             If coin is null.
	 * @throws CashOverloadException
	 *             If the coin dispenser is already full.
	 */
	@Override
	public synchronized void receive(Coin coin) throws CashOverloadException, DisabledException {
		super.receive(coin);
	}

	/**
	 * Returns whether this coin dispenser has enough space to accept at least one
	 * more coin. Announces no events. Requires power.
	 */
	@Override
	public synchronized boolean hasSpace() {
		return super.hasSpace();
	}

	@Override
	public synchronized void reject(Coin cash) throws DisabledException, CashOverloadException {
		if(!isActivated())
			throw new NoPowerException();
		
		source.reject(cash);
	}
}
