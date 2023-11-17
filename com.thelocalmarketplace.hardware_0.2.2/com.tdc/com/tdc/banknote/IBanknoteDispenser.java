package com.tdc.banknote;

import java.util.List;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.IComponent;
import com.tdc.NoCashAvailableException;

import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * The base type of components that dispense banknotes.
 *            
 * @author TDC, Inc.
 */
public interface IBanknoteDispenser extends IComponent<BanknoteDispenserObserver>{
	/**
	 * Accesses the current number of banknotes in the dispenser. Requires power.
	 * 
	 * @return The number of banknotes currently in the dispenser.
	 */
	int size();

	/**
	 * Allows a set of banknotes to be loaded into the dispenser directly. Existing
	 * banknotes in the dispenser are not removed. Announces "banknotesLoaded"
	 * event. Requires power.
	 * 
	 * @param banknotes
	 *            A sequence of banknotes to be added. Each may not be null.
	 * @throws CashOverloadException
	 *             if the number of banknotes to be loaded exceeds the capacity of
	 *             the dispenser.
	 * @throws SimulationException
	 *             If any banknote is null.
	 */
	void load(Banknote... banknotes) throws CashOverloadException;

	/**
	 * Unloads banknotes from the dispenser directly. Announces "banknotesUnloaded"
	 * event. Requires power.
	 * 
	 * @return A list of the banknotes unloaded. May be empty. Will never be null.
	 */
	List<Banknote> unload();

	/**
	 * Returns the maximum capacity of this banknote dispenser. Does not require
	 * power.
	 * 
	 * @return The capacity. Will be positive.
	 */
	int getCapacity();

	/**
	 * Emits a single banknote from this banknote dispenser. If successful,
	 * announces "banknoteRemoved" event. If a successful banknote removal causes
	 * the dispenser to become empty, announces "banknotesEmpty" event. Requires
	 * power.
	 * 
	 * @throws CashOverloadException
	 *             if the output channel is unable to accept another banknote.
	 * @throws NoCashAvailableException
	 *             if no banknotes are present in the dispenser to release.
	 * @throws DisabledException
	 *             if the dispenser is currently disabled.
	 */
	void emit() throws NoCashAvailableException, DisabledException, CashOverloadException;
}