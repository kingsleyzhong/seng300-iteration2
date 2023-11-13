package com.tdc.coin;

import java.util.List;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.IComponent;
import com.tdc.NoCashAvailableException;
import com.tdc.Sink;

import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * The base type of components that dispense coins.
 *            
 * @author TDC, Inc.
 */
public interface ICoinDispenser extends IComponent<CoinDispenserObserver>, Sink<Coin> {
	/**
	 * Accesses the current number of coins in the dispenser. Requires power.
	 * 
	 * @return The number of coins currently in the dispenser.
	 */
	int size();

	/**
	 * Allows a set of coins to be loaded into the dispenser directly. Existing
	 * coins in the dispenser are not removed. On success, announces "coinsLoaded"
	 * event. Requires power.
	 * 
	 * @param coins
	 *            A sequence of coins to be added. Each cannot be null.
	 * @throws CashOverloadException
	 *             if the number of coins to be loaded exceeds the capacity of the
	 *             dispenser.
	 * @throws SimulationException
	 *             If any coin is null.
	 */
	void load(Coin... coins) throws SimulationException, CashOverloadException;

	/**
	 * Unloads coins from the dispenser directly. On success, announces
	 * "coinsUnloaded" event. Requires power.
	 * 
	 * @return A list of the coins unloaded. May be empty. Will never be null.
	 */
	List<Coin> unload();

	/**
	 * Returns the maximum capacity of this coin dispenser. Does not require power.
	 * 
	 * @return The capacity. Will be positive.
	 */
	int getCapacity();

	/**
	 * Releases a single coin from this coin dispenser. If successful, announces
	 * "coinRemoved" event. If a successful coin removal causes the dispenser to
	 * become empty, announces "coinsEmpty" event. Requires power.
	 * 
	 * @throws CashOverloadException
	 *             If the output channel is unable to accept another coin.
	 * @throws NoCashAvailableException
	 *             If no coins are present in the dispenser to release.
	 * @throws DisabledException
	 *             If the dispenser is currently disabled.
	 */
	void emit() throws CashOverloadException, NoCashAvailableException, DisabledException;

	/**
	 * The dispenser cannot accept rejected coins from its output sink, only from
	 * its input source. Requires power.
	 * 
	 * @param coin The coin to reject.
	 * @throws DisabledException If the component is disabled.
	 * @throws CashOverloadException If the component is already full.
	 */
	void reject(Coin coin) throws DisabledException, CashOverloadException;
}