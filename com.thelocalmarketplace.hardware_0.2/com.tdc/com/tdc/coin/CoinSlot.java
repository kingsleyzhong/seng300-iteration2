package com.tdc.coin;

import com.tdc.AbstractComponent;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.Sink;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;

/**
 * Represents a simple coin slot component that has one output channel. The slot is
 * stupid: it has no functionality other than being enabled/disabled, and cannot
 * determine the value and currency of the coin.
 *            
 * @author TDC, Inc.
 */
public final class CoinSlot extends AbstractComponent<CoinSlotObserver> implements Sink<Coin> {
	/**
	 * Represents this component's output sink.
	 */
	public Sink<Coin> sink;

	/**
	 * Creates a coin slot.
	 */
	public CoinSlot() {}

	/**
	 * Tells the coin slot that the indicated coin is being inserted. If the slot is
	 * enabled, announces "coinInserted" event. Requires power.
	 * 
	 * @param coin
	 *            The coin to be added. Cannot be null.
	 * @throws DisabledException
	 *             If the coin slot is currently disabled.
	 * @throws CashOverloadException
	 *             If the sink has no space.
	 * @throws SimulationException
	 *             If coin is null.
	 */
	public synchronized void receive(Coin coin) throws DisabledException, CashOverloadException {
		if(!isActivated())
			throw new NoPowerException();

		if(isDisabled())
			throw new DisabledException();

		if(coin == null)
			throw new NullPointerSimulationException("coin");

		notifyCoinInserted();

		if(sink.hasSpace()) {
			try {
				sink.receive(coin);
			}
			catch(CashOverloadException e) {
				// Should never happen
				throw e;
			}
		}
		else
			throw new CashOverloadException("Unable to route coin: Output channel is full");
	}

	@Override
	public synchronized boolean hasSpace() {
		if(!isActivated())
			throw new NoPowerException();

		return sink.hasSpace();
	}

	private void notifyCoinInserted() {
		for(CoinSlotObserver observer : observers)
			observer.coinInserted(this);
	}
}
