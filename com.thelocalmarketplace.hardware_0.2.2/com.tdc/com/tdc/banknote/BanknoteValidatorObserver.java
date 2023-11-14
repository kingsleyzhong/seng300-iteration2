package com.tdc.banknote;

import java.math.BigDecimal;
import java.util.Currency;

import com.tdc.IComponentObserver;

/**
 * Observes events emanating from a banknote validator.
 *            
 * @author TDC, Inc.
 */
public interface BanknoteValidatorObserver extends IComponentObserver {
	/**
	 * An event announcing that the indicated banknote has been detected and
	 * determined to be valid.
	 * 
	 * @param validator
	 *            The component on which the event occurred.
	 * @param currency
	 *            The kind of currency of the inserted banknote.
	 * @param denomination
	 *            The value of the inserted banknote.
	 */
	void goodBanknote(BanknoteValidator validator, Currency currency, BigDecimal denomination);

	/**
	 * An event announcing that the indicated banknote has been detected and
	 * determined to be invalid.
	 * 
	 * @param validator
	 *            The component on which the event occurred.
	 */
	void badBanknote(BanknoteValidator validator);
}
