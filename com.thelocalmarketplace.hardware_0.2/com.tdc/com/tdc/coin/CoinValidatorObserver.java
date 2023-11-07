package com.tdc.coin;

import java.math.BigDecimal;

import com.tdc.IComponentObserver;

/**
 * Observes events emanating from a coin validator.
 * 
 * @author TDC, Inc.
 */
public interface CoinValidatorObserver extends IComponentObserver {
	/**
	 * An event announcing that the indicated coin has been detected and determined
	 * to be valid.
	 * 
	 * @param validator
	 *            The component on which the event occurred.
	 * @param value
	 *            The value of the coin.
	 */
	void validCoinDetected(CoinValidator validator, BigDecimal value);

	/**
	 * An event announcing that a coin has been detected and determined to be
	 * invalid.
	 * 
	 * @param validator
	 *            The component on which the event occurred.
	 */
	void invalidCoinDetected(CoinValidator validator);
}
