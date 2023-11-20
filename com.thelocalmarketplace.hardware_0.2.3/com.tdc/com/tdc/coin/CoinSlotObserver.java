package com.tdc.coin;

import com.tdc.IComponentObserver;

/**
 * Observes events emanating from a coin slot.
 *            
 * @author TDC, Inc.
 */
public interface CoinSlotObserver extends IComponentObserver {
	/**
	 * An event announcing that a coin has been inserted.
	 * 
	 * @param slot
	 *             The component on which the event occurred.
	 */
	void coinInserted(CoinSlot slot);
}
