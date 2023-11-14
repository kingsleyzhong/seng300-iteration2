package com.tdc.banknote;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.tdc.AbstractComponent;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.NoCashAvailableException;
import com.tdc.Sink;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;

/**
 * The abstract base class of components that dispense banknotes.
 *            
 * @author TDC, Inc.
 */
public abstract class AbstractBanknoteDispenser extends AbstractComponent<BanknoteDispenserObserver> implements IBanknoteDispenser {
	protected int maxCapacity;
	private Queue<Banknote> queue = new LinkedList<Banknote>();

	/**
	 * Represents the output sink of this dispenser.
	 */
	public Sink<Banknote> sink;
	
	protected AbstractBanknoteDispenser(int capacity) {
		if(capacity <= 0)
			throw new InvalidArgumentSimulationException("Capacity must be positive: " + capacity);

		this.maxCapacity = capacity;
	}

	/**
	 * Accesses the current number of banknotes in the dispenser. Requires power.
	 * 
	 * @return The number of banknotes currently in the dispenser.
	 */
	@Override
	public synchronized int size() {
		if(!hasPower())
			throw new NoPowerException();

		return queue.size();
	}

	protected void receive(Banknote banknote) throws CashOverloadException, DisabledException {
		if(!hasPower())
			throw new NoPowerException();

		if(banknote == null)
			throw new NullPointerSimulationException();

		if(isDisabled())
			throw new DisabledException();

		if(queue.size() + 1 > maxCapacity)
			throw new CashOverloadException();

		queue.add(banknote);

		notifyBillAdded(banknote);
		
		if(!hasSpace())
			notifyMoneyFull();
	}

	protected boolean hasSpace() {
		if(!hasPower())
			throw new NoPowerException();

		return queue.size() < maxCapacity;
	}

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
	@Override
	public synchronized void load(Banknote... banknotes) throws CashOverloadException {
		if(!hasPower())
			throw new NoPowerException();

		if(maxCapacity < queue.size() + banknotes.length)
			throw new CashOverloadException("Capacity of dispenser is exceeded by load");

		for(Banknote banknote : banknotes)
			if(banknote == null)
				throw new NullPointerSimulationException("banknote instance");
			else
				queue.add(banknote);

		notifyBanknotesLoaded(banknotes);
	}

	/**
	 * Unloads banknotes from the dispenser directly. Announces "banknotesUnloaded"
	 * event. Requires power.
	 * 
	 * @return A list of the banknotes unloaded. May be empty. Will never be null.
	 */
	@Override
	public synchronized List<Banknote> unload() {
		if(!hasPower())
			throw new NoPowerException();

		List<Banknote> result = new ArrayList<>(queue);
		queue.clear();

		notifyBanknotesUnoaded(result.toArray(new Banknote[result.size()]));

		return result;
	}

	/**
	 * Returns the maximum capacity of this banknote dispenser. Does not require
	 * power.
	 * 
	 * @return The capacity. Will be positive.
	 */
	@Override
	public int getCapacity() {
		return maxCapacity;
	}

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
	@Override
	public synchronized void emit() throws NoCashAvailableException, DisabledException, CashOverloadException {
		if(!hasPower())
			throw new NoPowerException();

		if(isDisabled())
			throw new DisabledException();

		if(queue.size() == 0)
			throw new NoCashAvailableException();

		Banknote banknote = queue.remove();

		if(sink.hasSpace())
			try {
				sink.receive(banknote);
			}
			catch(CashOverloadException e) {
				// Should never happen
				throw e;
			}
		else
			throw new CashOverloadException("The sink is full.");

		notifyBanknoteRemoved(banknote);

		if(queue.isEmpty())
			notifyBanknotesEmpty();
	}

	protected void notifyBanknoteRemoved(Banknote banknote) {
		for(BanknoteDispenserObserver observer : observers)
			observer.banknoteRemoved(this, banknote);
	}

	protected void notifyBanknotesEmpty() {
		for(BanknoteDispenserObserver observer : observers)
			observer.banknotesEmpty(this);
	}

	protected void notifyBanknotesLoaded(Banknote[] banknotes) {
		for(BanknoteDispenserObserver observer : observers)
			observer.banknotesLoaded(this, banknotes);
	}

	protected void notifyBanknotesUnoaded(Banknote[] banknotes) {
		for(BanknoteDispenserObserver observer : observers)
			observer.banknotesUnloaded(this, banknotes);
	}

	protected void notifyBillAdded(Banknote banknote) {
		for(BanknoteDispenserObserver observer : observers)
			observer.banknoteAdded(this, banknote);
	}

	protected void notifyMoneyFull() {
		for(BanknoteDispenserObserver observer : observers)
			observer.moneyFull(this);
	}
}
