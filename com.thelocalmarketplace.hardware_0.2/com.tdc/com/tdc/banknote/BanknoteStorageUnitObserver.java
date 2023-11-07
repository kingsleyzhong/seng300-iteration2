package com.tdc.banknote;

import com.tdc.IComponentObserver;

/**
 * Observes events emanating from a banknote storage unit.
 * 
 * @author TDC, Inc.
 */
public interface BanknoteStorageUnitObserver extends IComponentObserver {
	/**
	 * Announces that the indicated banknote storage unit is full of banknotes.
	 * 
	 * @param unit
	 *            The storage unit where the event occurred.
	 */
	void banknotesFull(BanknoteStorageUnit unit);

	/**
	 * Announces that a banknote has been added to the indicated storage unit.
	 * 
	 * @param unit
	 *            The storage unit where the event occurred.
	 */
	void banknoteAdded(BanknoteStorageUnit unit);

	/**
	 * Announces that the indicated storage unit has been loaded with banknotes.
	 * Used to simulate direct, physical loading of the unit.
	 * 
	 * @param unit
	 *            The storage unit where the event occurred.
	 */
	void banknotesLoaded(BanknoteStorageUnit unit);

	/**
	 * Announces that the storage unit has been emptied of banknotes. Used to
	 * simulate direct, physical unloading of the unit.
	 * 
	 * @param unit
	 *            The storage unit where the event occurred.
	 */
	void banknotesUnloaded(BanknoteStorageUnit unit);
}
