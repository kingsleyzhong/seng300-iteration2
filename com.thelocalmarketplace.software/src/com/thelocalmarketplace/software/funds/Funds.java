package com.thelocalmarketplace.software.funds;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Set;
import java.util.TreeMap;

import com.jjjwelectronics.IllegalDigitException;
import com.tdc.AbstractComponent;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.NoCashAvailableException;
import com.tdc.banknote.IBanknoteDispenser;
import com.tdc.coin.AbstractCoinDispenser;
import com.tdc.coin.CoinDispenserBronze;
import com.tdc.coin.CoinValidator;
import com.tdc.coin.CoinValidatorObserver;
import com.tdc.coin.ICoinDispenser;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

/**
 * This class represents the funds associated with a self-checkout session.
 * It manages the total price of items, the amount paid, and the amount due.
 * 
 * Project iteration 2 group members:
 * Aj Sallh : 30023811
 * Anthony Kostal-Vazquez : 30048301
 * Chloe Robitaille : 30022887
 * Dvij Raval : 30024340
 * Emily Kiddle : 30122331
 * Katelan NG : 30144672
 * Kingsley Zhong : 30197260
 * Nick McCamis : 30192610
 * Sua Lim : 30177039
 * Subeg CHAHAL : 30196531
 */
public class Funds {
	protected ArrayList<FundsListener> listeners = new ArrayList<>();
	private BigDecimal itemsPrice; // Summed price of all items in the session (in cents)
	private BigDecimal paid; // Amount paid by the customer (in cents)
	private BigDecimal amountDue; // Remaining amount to be paid (in cents)
	private boolean isPay; // Flag indicating if the session is in pay mode
	private PayByCashController cashController;

	// private PayByCardController cardController;

	private AbstractSelfCheckoutStation scs;

	/**
	 * Constructor that initializes the funds and registers an inner listener to the
	 * self-checkout station.
	 * 
	 * @param scs The self-checkout station
	 */
	public Funds(AbstractSelfCheckoutStation scs) {
		if (scs == null) {
			throw new IllegalArgumentException("SelfCheckoutStation should not be null.");
		}
		this.itemsPrice = BigDecimal.ZERO;
		this.paid = BigDecimal.ZERO;
		this.amountDue = BigDecimal.ZERO;
		this.isPay = false;
		this.cashController = new PayByCashController(scs);

		// this.cardController = new PayByCardController(scs);

		this.scs = scs;
	}

	/**
	 * Updates the total items price.
	 * 
	 * @param price The price to be added (in cents)
	 * @throws DisabledException
	 * @throws NoCashAvailableException
	 * @throws CashOverloadException
	 */
	public void update(BigDecimal price) throws CashOverloadException, NoCashAvailableException, DisabledException {
		if (price.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalDigitException("Price should be positive.");
		}
		this.itemsPrice = this.itemsPrice.add(price);
		calculateAmountDue();
	}

	/**
	 * Sets the pay mode.
	 * 
	 * @param isPay Flag indicating if the session is in pay mode
	 */
	public void setPay(boolean isPay) {
		this.isPay = isPay;
	}

	public BigDecimal getItemsPrice() {
		return itemsPrice;
	}

	public BigDecimal getPaid() {
		return paid;
	}

	public BigDecimal getAmountDue() {
		return amountDue;
	}

	public boolean isPay() {
		return isPay;
	}

	/**
	 * Calculates the amount due by subtracting the paid amount from the total items
	 * price.
	 * 
	 * @throws DisabledException
	 * @throws NoCashAvailableException
	 * @throws CashOverloadException
	 */
	private void calculateAmountDue() throws CashOverloadException, NoCashAvailableException, DisabledException {

		if (Session.getState() == SessionState.PAY_BY_CASH) {
			this.paid = cashController.getCashPaid();
		}

		if (Session.getState() == SessionState.PAY_BY_CARD) {

			// Boolean paidStatus = cardController.getPaidStatus();

			// if (paidStatus == True) {
			// this.paid = this.amountDue
			// }

		}

		this.amountDue = this.itemsPrice.subtract(this.paid);

		// To account for any rounding errors, checks if less that 0.0005 rather than
		// just 0
		if (amountDue.intValue() <= 0.0005) {
			for (FundsListener l : listeners)
				l.notifyPaid();

			// Return change if amount needed to be returned is greater than a cent
			if (amountDue.intValue() <= -1) {
				returnChange();
			}

		}
	}

	/***
	 * Calculates the change needed
	 * 
	 * @throws CashOverloadException
	 * @throws NoCashAvailableException
	 * @throws DisabledException
	 */
	private void returnChange() throws CashOverloadException, NoCashAvailableException, DisabledException {

		int change = (this.amountDue.subtract(this.paid)).abs().intValue();

		changeHelper(change);

	}

	/***
	 * Returns the change back to customer
	 * 
	 * @param changeDue
	 * @throws CashOverloadException
	 * @throws NoCashAvailableException
	 * @throws DisabledException
	 */
	private void changeHelper(int changeDue) throws CashOverloadException, NoCashAvailableException, DisabledException {
		if (changeDue < 0)
			throw new InternalError("Change due is negative, which should not happen");

		Map<BigDecimal, ICoinDispenser> coinMap = scs.coinDispensers;
		Map<BigDecimal, IBanknoteDispenser> banknoteMap = scs.banknoteDispensers;

		// Going through each banknoteDispenser by denomination
		for (BigDecimal denomination : banknoteMap.keySet()) {

			// Getting the number of change from a specific that can "fit" into the
			// changeDue
			int denominationNum = changeDue / denomination.intValue();

			// Checking to see if there is enough bills in the dispenser
			if (scs.banknoteDispensers.get(denomination).size() >= denominationNum) {

				for (int i = 0; i < denominationNum; i++) {

					scs.banknoteDispensers.get(denomination).emit();

					changeDue = changeDue - denomination.intValue();

				}
			}
		}

		// Going through each coinDispenser by denomination
		for (BigDecimal denomination : coinMap.keySet()) {

			// Getting the number of change from a specific that can "fit" into the
			// changeDue
			int denominationNum = changeDue / denomination.intValue();

			// Checking to see if there is enough bills in the dispenser
			if (scs.coinDispensers.get(denomination).size() >= denominationNum) {

				for (int i = 0; i < denominationNum; i++) {

					scs.coinDispensers.get(denomination).emit();

					changeDue = changeDue - denomination.intValue();
				}
			}
		}

		// If there is still remaining change left, then the machine does not have
		// enough change yet
		if (changeDue >= 0.0005) {
			throw new NoCashAvailableException();

		}

	}

	/**
	 * Methods for adding funds listeners to the funds
	 */
	public synchronized boolean deregister(FundsListener listener) {
		return listeners.remove(listener);
	}

	public synchronized void deregisterAll() {
		listeners.clear();
	}

	public final synchronized void register(FundsListener listener) {
		if (listener == null)
			throw new NullPointerSimulationException("listener");

		listeners.add(listener);
	}

}