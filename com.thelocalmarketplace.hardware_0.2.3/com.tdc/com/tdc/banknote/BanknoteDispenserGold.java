package com.tdc.banknote;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.PassiveSource;
import com.tdc.Sink;

/**
 * Represents a component that stores banknotes (as known as banknotes, paper
 * money, etc.) of a particular denomination to dispense them as change. This
 * component can receive additional banknotes both automatically and manually.
 *            
 * @author TDC, Inc.
 */
public final class BanknoteDispenserGold extends AbstractBanknoteDispenser implements Sink<Banknote> {
	private static final int MAX_CAPACITY = 1000;

	/**
	 * Represents the input source of this dispenser.
	 */
	public PassiveSource<Banknote> source;

	/**
	 * Creates a banknote dispenser that can be automatically refilled.
	 */
	public BanknoteDispenserGold() {
		super(MAX_CAPACITY);
	}

	@Override
	public void receive(Banknote cash) throws CashOverloadException, DisabledException {
		super.receive(cash);
	}

	@Override
	public boolean hasSpace() {
		return super.hasSpace();
	}
}
