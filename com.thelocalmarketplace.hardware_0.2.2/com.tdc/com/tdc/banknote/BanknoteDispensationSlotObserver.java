package com.tdc.banknote;

import java.util.List;

import com.tdc.IComponentObserver;

/**
 * Observes events emanating from a banknote dispensation slot.
 *            
 * @author TDC, Inc.
 */
public interface BanknoteDispensationSlotObserver extends IComponentObserver {
	/**
	 * An event announcing that banknotes have been dispensed to the user, dangling
	 * from the slot.
	 * 
	 * @param slot
	 *            The component on which the event occurred.
	 * @param banknotes
	 *            The banknotes that were dispensed. Cannot be null.
	 */
	void banknoteDispensed(BanknoteDispensationSlot slot, List<Banknote> banknotes);

	/**
	 * An event announcing that dangling banknotes have been removed by the user.
	 * 
	 * @param slot
	 *            The component on which the event occurred.
	 */
	void banknotesRemoved(BanknoteDispensationSlot slot);
}
