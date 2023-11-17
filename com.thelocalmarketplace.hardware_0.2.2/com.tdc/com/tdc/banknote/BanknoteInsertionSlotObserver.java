package com.tdc.banknote;

import com.tdc.IComponentObserver;

/**
 * Observes events emanating from a banknote slot.
 *            
 * @author TDC, Inc.
 */
public interface BanknoteInsertionSlotObserver extends IComponentObserver {
	/**
	 * An event announcing that a banknote has been inserted.
	 * 
	 * @param slot
	 *            The component on which the event occurred.
	 */
	void banknoteInserted(BanknoteInsertionSlot slot);

	/**
	 * An event announcing that a banknote has been returned to the user, dangling
	 * from the slot.
	 * 
	 * @param slot
	 *            The component on which the event occurred.
	 */
	void banknoteEjected(BanknoteInsertionSlot slot);

	/**
	 * An event announcing that a dangling banknote has been removed by the user.
	 * 
	 * @param slot
	 *            The component on which the event occurred.
	 */
	void banknoteRemoved(BanknoteInsertionSlot slot);
}
