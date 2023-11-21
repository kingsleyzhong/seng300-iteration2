package com.thelocalmarketplace.software.funds;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.jjjwelectronics.IllegalDigitException;
import com.tdc.AbstractComponent;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.NoCashAvailableException;
import com.tdc.banknote.BanknoteDispenserBronze;
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
import com.thelocalmarketplace.software.exceptions.NotEnoughChangeException;

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
	private PayByCard cardController;

  // from old version, delete if unused/ it breaks stuff
	// Testing ONLY
	public boolean payed;
	public boolean successfulSwipe;
  // end old version stuff to delete
  
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
		this.payed = false;
		this.cashController = new PayByCashController(scs, this);
		this.cardController = new PayByCard(scs, this);


		this.scs = scs;
	}

	/**
	 * Updates the total items price.
	 * 
	 * @param price The price to be added (in cents)
	 */
	public void update(BigDecimal price) {
		if (price.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalDigitException("Price should be positive.");
		}
		this.itemsPrice = this.itemsPrice.add(price);
		calculateAmountDue();
	}

	/**
	 * Updates the total items price after an item has been removed.
	 * 
	 * @param price The price to be added (in cents)
	 */
	public void removeItemPrice(BigDecimal price) {
		if (price.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalDigitException("Price should be positive.");
		}
		
		this.itemsPrice = this.itemsPrice.subtract(price);
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

	public void beginPayment() {
		
		if (amountDue.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalDigitException("Price should be positive.");
		}
		cardController.paidBool = false;
		calculateAmountDue();
	}

	/**
	 * Calculates the amount due by subtracting the paid amount from the total items
	 * price.
	 */
	private void calculateAmountDue() {

		if (Session.getState() == SessionState.PAY_BY_CASH) {
			this.paid = cashController.getCashPaid();
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
			payed = true;
		}
	}

	/***
	 * Checks the status of a card payment
	 */
	public void updatePaidCard(boolean paidBool) {
		if (Session.getState() == SessionState.PAY_BY_CARD) {
			if (paidBool) {
				this.paid = amountDue;
				calculateAmountDue();
			}
		} else {
			throw new InvalidActionException("Not in Card Payment state");
		}
	}

	/***
	 * Updates Payment based on the PayByCash Controller
	 */
	public void updatePaidCash() {

		if (Session.getState() == SessionState.PAY_BY_CASH) {
			this.paid = cashController.getCashPaid();
			calculateAmountDue();

		}

	}

	/***
	 * Calculates the change needed	 * 
	 */
	private void returnChange() {

		int changeDue = (this.amountDue).abs().intValue();

		changeHelper(changeDue);
	


	}

	/***
	 * Returns the change back to customer
	 * 
	 * @param changeDue
	 * 	 */
	private void changeHelper(int changeDue){
		if (changeDue < 0) {
			throw new InternalError("Change due is negative, which should not happen");
		}

		// Getting banknote denominations and sorting from descending order by value
		Set<BigDecimal> banknoteType = scs.banknoteDispensers.keySet();
		ArrayList banknoteList = new ArrayList(banknoteType);
		Collections.sort(banknoteList);
		Collections.reverse(banknoteList);

		// Getting coin denominations and sorting from descending order by value
		Set<BigDecimal> coinType = scs.coinDispensers.keySet();
		ArrayList coinList = new ArrayList(coinType);
		Collections.sort(coinList);
		Collections.reverse(coinList);

		// Going through each banknoteDispenser by denomination
		Iterator itrBanknote = banknoteList.iterator();

		while (itrBanknote.hasNext()) {

			// Getting the number of change from a specific that can "fit" into the
			// changeDue
			BigDecimal banknoteDenomination = (BigDecimal) itrBanknote.next();

			int denominationNum = changeDue / banknoteDenomination.intValue();

			// Checking to see if there is enough bills in the dispenser
			if (scs.banknoteDispensers.get(banknoteDenomination).size() >= denominationNum) {

				for (int i = 0; i < denominationNum; i++) {

					try {
						scs.banknoteDispensers.get(banknoteDenomination).emit();
					} catch (NoCashAvailableException e) {
						throw new NotEnoughChangeException("There are no banknotes available");
					} catch (DisabledException e) {
						System.out.println("Machine is not turned on");
					} catch (CashOverloadException e) {
						System.out.println("Too much cash, the machine has broken");
					}

					changeDue = changeDue - banknoteDenomination.intValue();

				}
			}
		}

		// Going through each coinDispenser by denomination
		Iterator itrCoin = coinList.iterator();

		while (itrCoin.hasNext()) {

			// Getting the number of change from a specific that can "fit" into the
			// changeDue
			BigDecimal coinDenomination = (BigDecimal) itrCoin.next();

			int denominationNum = (int)(changeDue / coinDenomination.doubleValue());

			// Checking to see if there is enough bills in the dispenser
			if (scs.coinDispensers.get(coinDenomination).size() >= denominationNum) {

				for (int i = 0; i < denominationNum; i++) {

					try {
						scs.coinDispensers.get(coinDenomination).emit();
					} catch (NoCashAvailableException e) {
						throw new NotEnoughChangeException("There are no coins available");
					} catch (DisabledException e) {
						System.out.println("Machine is not turned on");
					} catch (CashOverloadException e) {
						System.out.println("Too much cash, the machine has broken");
					}

					changeDue = changeDue - coinDenomination.intValue();
				}
			}
		}

		scs.banknoteOutput.dispense();
		
		if (changeDue > 0.005) {
			throw new NotEnoughChangeException("Not enough change in the machine");
		}

	}

	/**
	 * Methods for adding funds listeners to the funds
	 */
	public synchronized boolean deregister(FundsListener listener) {
		if (listener == null)
			throw new NullPointerSimulationException("listener");

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