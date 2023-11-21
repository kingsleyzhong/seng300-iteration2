package com.tdc.banknote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tdc.AbstractComponent;
import com.tdc.CashOverloadException;
import com.tdc.ComponentFailure;
import com.tdc.DisabledException;
import com.tdc.Sink;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;

/**
 * Represents a banknote slot component that can dispense one or more banknotes,
 * leaving them dangling until the customer removes it/them, via
 * {@link #removeDanglingBanknotes()}. The banknotes are accumulated one at a
 * time inside the component, and then they can be dispensed all at once.
 *            
 * @author TDC, Inc.
 */
public final class BanknoteDispensationSlot extends AbstractComponent<BanknoteDispensationSlotObserver>
	implements Sink<Banknote> {
	private ArrayList<Banknote> danglingDispensedBanknotes = new ArrayList<>();
	private List<Banknote> banknotesToDispense = new ArrayList<>();
	private static final int MAX_CAPACITY = 20;
	private final int capacity = MAX_CAPACITY;

	/**
	 * Creates a banknote slot.
	 */
	public BanknoteDispensationSlot() {
	}

	/**
	 * Receives the indicated banknote, adding it to the collection to be dispensed.
	 * Requires power.
	 * 
	 * @param banknote
	 *            The banknote to be received.
	 * @throws DisabledException
	 *             If the component is disabled.
	 * @throws SimulationException
	 *             If the argument is null.
	 * @throws CashOverloadException
	 *             If the component has accumulated too many banknotes.
	 */
	public synchronized void receive(Banknote banknote) throws DisabledException, CashOverloadException {
		if(!isActivated())
			throw new NoPowerException();

		if(isDisabled())
			throw new DisabledException();

		if(banknote == null)
			throw new NullPointerSimulationException("banknote");

		if(banknotesToDispense.size() == capacity)
			throw new CashOverloadException("The slot cannot accumulate another banknote.");
		
		banknotesToDispense.add(banknote);
	}

	/**
	 * Causes the accumulated banknotes to be dispensed to the customer, left
	 * dangling until removed.
	 */
	public synchronized void dispense() {
		if(!isActivated())
			throw new NoPowerException();

		if(!danglingDispensedBanknotes.isEmpty())
			throw new ComponentFailure("Attempt to dispense banknotes when the slot is already occupied.");

		danglingDispensedBanknotes.addAll(banknotesToDispense);
		banknotesToDispense.clear();

		notifyBanknotesDispensed(danglingDispensedBanknotes);
	}

	/**
	 * Simulates the user removing a banknote that is dangling from the slot.
	 * Announces "banknoteRemoved" event. Disabling has no effect on this method.
	 * Does not require power.
	 * 
	 * @return The formerly dangling banknote.
	 */
	public synchronized List<Banknote> removeDanglingBanknotes() {
		if(danglingDispensedBanknotes.isEmpty())
			throw new NullPointerSimulationException("danglingEjectedBanknote");

		@SuppressWarnings("unchecked")
		List<Banknote> banknotes = Collections.unmodifiableList((List<Banknote>)danglingDispensedBanknotes.clone());
		danglingDispensedBanknotes.clear();
		notifyBanknotesRemoved();

		return banknotes;
	}

	/**
	 * Determines whether this slot has banknotes dangling from it. Does not require
	 * power.
	 * 
	 * @return true if there are dangling banknotes; otherwise, false.
	 */
	public synchronized boolean hasDanglingBanknotes() {
		return !danglingDispensedBanknotes.isEmpty();
	}

	/**
	 * Tests whether a banknote can be accepted by this slot for dispensation.
	 * Requires power.
	 * 
	 * @return True if the slot has space to accumulate one more banknote;
	 *             otherwise, false.
	 */
	public synchronized boolean hasSpace() {
		if(!isActivated())
			throw new NoPowerException();

		if(isDisabled())
			return false;

		return danglingDispensedBanknotes.isEmpty();
	}

	private void notifyBanknotesDispensed(List<Banknote> banknotes) {
		for(BanknoteDispensationSlotObserver observer : observers)
			observer.banknoteDispensed(this, Collections.unmodifiableList(banknotes));
	}

	private void notifyBanknotesRemoved() {
		for(BanknoteDispensationSlotObserver observer : observers)
			observer.banknotesRemoved(this);
	}
}
