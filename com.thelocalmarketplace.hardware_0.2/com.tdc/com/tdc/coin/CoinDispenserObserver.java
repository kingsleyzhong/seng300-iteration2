package com.tdc.coin;

import com.tdc.IComponentObserver;

/**
 * Observes events emanating from a coin dispenser.
 * 
 * @author TDC, Inc.
 */
public interface CoinDispenserObserver extends IComponentObserver {
	/**
	 * Announces that the indicated coin dispenser is full of coins.
	 * 
	 * @param dispenser
	 *            The dispenser where the event occurred.
	 */
	void coinsFull(ICoinDispenser dispenser);

	/**
	 * Announces that the indicated coin dispenser is empty of coins.
	 * 
	 * @param dispenser
	 *            The dispenser where the event occurred.
	 */
	void coinsEmpty(ICoinDispenser dispenser);

	/**
	 * Announces that the indicated coin has been added to the indicated coin
	 * dispenser.
	 * 
	 * @param dispenser
	 *            The dispenser where the event occurred.
	 * @param coin
	 *            The coin that was added.
	 */
	void coinAdded(ICoinDispenser dispenser, Coin coin);

	/**
	 * Announces that the indicated coin has been added to the indicated coin
	 * dispenser.
	 * 
	 * @param dispenser
	 *            The dispenser where the event occurred.
	 * @param coin
	 *            The coin that was removed.
	 */
	void coinRemoved(ICoinDispenser dispenser, Coin coin);

	/**
	 * Announces that the indicated sequence of coins has been added to the
	 * indicated coin dispenser. Used to simulate direct, physical loading of the
	 * dispenser.
	 * 
	 * @param dispenser
	 *            The dispenser where the event occurred.
	 * @param coins
	 *            The coins that were loaded.
	 */
	void coinsLoaded(ICoinDispenser dispenser, Coin... coins);

	/**
	 * Announces that the indicated sequence of coins has been removed to the
	 * indicated coin dispenser. Used to simulate direct, physical unloading of the
	 * dispenser.
	 * 
	 * @param dispenser
	 *            The dispenser where the event occurred.
	 * @param coins
	 *            The coins that were unloaded.
	 */
	void coinsUnloaded(ICoinDispenser dispenser, Coin... coins);
}
