package com.tdc.banknote;

/**
 * Represents a component that stores banknotes (as known as banknotes, paper money,
 * etc.) of a particular denomination to dispense them as change. This component
 * cannot receive additional banknotes automatically, but must be manually
 * reloaded.
 *            
 * @author TDC, Inc.
 */
public final class BanknoteDispenserBronze extends AbstractBanknoteDispenser {
	private static final int MAX_CAPACITY = 1000;
	
	/**
	 * Creates a banknote dispenser that cannot be automatically refilled.
	 */
	public BanknoteDispenserBronze() {
		super(MAX_CAPACITY);
	}
}
