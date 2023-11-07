package com.tdc.banknote;

import com.tdc.AbstractComponent;
import com.tdc.CashOverloadException;
import com.tdc.ComponentFailure;
import com.tdc.DisabledException;
import com.tdc.PassiveSource;
import com.tdc.Sink;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;

/**
 * Represents a simple banknote slot component that can either accept a banknote or
 * eject the most recently inserted banknote, leaving it dangling until the
 * customer removes it, via {@link #removeDanglingBanknote()}.
 *            
 * @author TDC, Inc.
 */
public final class BanknoteInsertionSlot extends AbstractComponent<BanknoteInsertionSlotObserver>
	implements Sink<Banknote>, PassiveSource<Banknote> {
	/**
	 * Represents the output sink of this component.
	 */
	public Sink<Banknote> sink;
	private Banknote danglingEjectedBanknote = null;

	/**
	 * Creates a banknote slot.
	 */
	public BanknoteInsertionSlot() {}

	/**
	 * Tells the banknote slot that the indicated banknote is being inserted. If the
	 * sink can accept the banknote, the banknote is passed to the sink and a
	 * "banknoteInserted" event is announced; otherwise, a "banknoteEjected" event
	 * is announced, meaning that the banknote is returned to the user. Requires
	 * power.
	 * 
	 * @param banknote
	 *            The banknote to be added. Cannot be null.
	 * @throws DisabledException
	 *             if the banknote slot is currently disabled.
	 * @throws SimulationException
	 *             If the banknote is null.
	 * @throws CashOverloadException
	 *             If a banknote is dangling from the slot.
	 */
	public synchronized void receive(Banknote banknote) throws DisabledException, CashOverloadException {
		if(!isActivated())
			throw new NoPowerException();

		if(banknote == null)
			throw new NullPointerSimulationException("banknote");

		if(isDisabled())
			throw new DisabledException();

		if(danglingEjectedBanknote != null)
			throw new CashOverloadException("A banknote is dangling from the slot. Remove it before adding another.");

		notifyBanknoteInserted();

		if(sink.hasSpace()) {
			try {
				sink.receive(banknote);
			}
			catch(CashOverloadException e) {
				// Should never happen
				throw e;
			}
		}
		else {
			danglingEjectedBanknote = banknote;
			notifyBanknoteEjected();
		}
	}

	/**
	 * Ejects the indicated banknote, leaving it dangling until the customer grabs
	 * it. Requires power.
	 * 
	 * @param banknote
	 *            The banknote to be ejected.
	 * @throws DisabledException
	 *             If the component is disabled.
	 * @throws SimulationException
	 *             If the argument is null.
	 * @throws CashOverloadException
	 *             If a banknote is already dangling from the slot.
	 */
	public synchronized void emit(Banknote banknote) throws DisabledException, CashOverloadException {
		if(!isActivated())
			throw new NoPowerException();

		if(isDisabled())
			throw new DisabledException();

		if(banknote == null)
			throw new NullPointerSimulationException("banknote");

		if(danglingEjectedBanknote != null)
			throw new CashOverloadException(
				"A banknote is already dangling from the slot. Remove that before ejecting another.");

		danglingEjectedBanknote = banknote;

		notifyBanknoteEjected();
	}

	public synchronized void reject(Banknote banknote) {
		if(!isActivated())
			throw new NoPowerException();

		if(danglingEjectedBanknote != null)
			throw new ComponentFailure("Attempt to reject a banknote when the slot is already occupied.");

		danglingEjectedBanknote = banknote;
		notifyBanknoteEjected();
	}

	/**
	 * Simulates the user removing a banknote that is dangling from the slot.
	 * Announces "banknoteRemoved" event. Disabling has no effect on this method.
	 * Does not require power.
	 * 
	 * @return The formerly dangling banknote.
	 */
	public synchronized Banknote removeDanglingBanknote() {
		if(danglingEjectedBanknote == null)
			throw new NullPointerSimulationException("danglingEjectedBanknote");

		Banknote banknote = danglingEjectedBanknote;
		danglingEjectedBanknote = null;
		notifyBanknoteRemoved();

		return banknote;
	}

	/**
	 * Determines whether this slot has a banknote dangling from it. Does not
	 * require power.
	 * 
	 * @return true if there is a dangling banknote; otherwise, false.
	 */
	public synchronized boolean hasDanglingBanknotes() {
		return danglingEjectedBanknote != null;
	}

	/**
	 * Tests whether a banknote can be accepted by or ejected from this slot.
	 * Disabling has no effect on this method. Requires power.
	 * 
	 * @return True if the slot is not occupied by a dangling banknote; otherwise,
	 *             false.
	 */
	public synchronized boolean hasSpace() {
		if(!isActivated())
			throw new NoPowerException();

		return danglingEjectedBanknote == null;
	}

	private void notifyBanknoteInserted() {
		for(BanknoteInsertionSlotObserver observer : observers)
			observer.banknoteInserted(this);
	}

	private void notifyBanknoteEjected() {
		for(BanknoteInsertionSlotObserver observer : observers)
			observer.banknoteEjected(this);
	}

	private void notifyBanknoteRemoved() {
		for(BanknoteInsertionSlotObserver observer : observers)
			observer.banknoteRemoved(this);
	}
}
