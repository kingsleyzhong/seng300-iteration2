package com.tdc.banknote;

import java.math.BigDecimal;
import java.util.Currency;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Instances of this class represent individual banknotes.
 * 
 * @author TDC, Inc.
 */
public class Banknote {
	private BigDecimal denomination;
	private Currency currency;

	/**
	 * Constructs a banknote.
	 * 
	 * @param currency
	 *            The currency represented by this banknote.
	 * @param denomination
	 *            The value of the banknote, relative to the unit of currency.
	 * @throws SimulationException
	 *             If the value &le; 0.
	 * @throws SimulationException
	 *             If currency is null.
	 */
	public Banknote(Currency currency, BigDecimal denomination) {
		if(currency == null)
			throw new NullPointerSimulationException("Null is not a valid currency.");

		if(denomination.compareTo(BigDecimal.ZERO) <= 0)
			throw new InvalidArgumentSimulationException(
				"The value must be greater than 0: the argument passed was " + denomination);

		this.denomination = denomination;
		this.currency = currency;
	}

	/**
	 * Accessor for the denomination.
	 * 
	 * @return The value of the banknote. Should always be &gt;0. Note that this is
	 *             not the same as the "currency" (e.g., a Canadian $10 banknote
	 *             is worth 10 Canadian dollars, so a Canadian $10 banknote would
	 *             have denomination "10").
	 */
	public BigDecimal getDenomination() {
		return denomination;
	}

	/**
	 * Accessor for the currency.
	 * 
	 * @return The currency for this banknote. Note that this is not the same as the
	 *             "denomination" (e.g., a Canadian $10 banknote is worth 10
	 *             Canadian dollars, so a Canadian $10 banknote would have currency
	 *             "Canadian dollars").
	 */
	public Currency getCurrency() {
		return currency;
	}

	@Override
	public String toString() {
		return denomination.toString() + " " + currency;
	}
}
