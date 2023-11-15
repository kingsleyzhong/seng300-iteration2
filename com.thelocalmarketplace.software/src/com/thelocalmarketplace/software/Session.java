package com.thelocalmarketplace.software;

import java.math.BigDecimal;
import java.util.HashMap;

import com.jjjwelectronics.Mass;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.software.exceptions.CartEmptyException;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.funds.FundsListener;
import com.thelocalmarketplace.software.weight.Weight;
import com.thelocalmarketplace.software.weight.WeightListener;

/**
 * Class facade representing the session of a self-checkout station
 *
 * Can be started and canceled. Becomes frozen when a weight discrepancy
 * occurs and unfrozen when weight discrepancy is fixed.
 *
 * Contains the funds of the system, the weight of the system, and a list
 * representing all the products that have been added to the system as well
 * as the quantity of those items.
 *
 * Has add bulky item functionality
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
 *
 */
public class Session {
	private static SessionState sessionState;
	private HashMap<BarcodedProduct, Integer> barcodedItems;
	private Funds funds;
	private Weight weight;

	private class WeightDiscrepancyListener implements WeightListener {

		/**
		 * Upon a weightDiscrepancy, session should freeze
		 */
		@Override
		public void notifyDiscrepancy() {
			block();
		}

		/**
		 * Upon resolution of a weightDiscrepancy, session should resume
		 */
		@Override
		public void notifyDiscrepancyFixed() {
			resume();
		}

	}

	private class PayListener implements FundsListener {

		/**
		 * Signals to the system that the customer has payed the full amount. Ends the
		 * session.
		 */
		@Override
		public void notifyPaid() {
			sessionState = SessionState.PRE_SESSION;
		}

	}

	/**
	 * Constructor for the session method. Requires to be installed on self-checkout
	 * system
	 * with logic to function
	 */
	public Session() {
		sessionState = SessionState.PRE_SESSION;
	}

	/**
	 * Setup method for the session used in installing logic on the system
	 * Initializes private variables to the ones passed. Initially has the session
	 * off, session unfrozen, and pay not
	 * enabled.
	 *
	 * @param barcodedItems
	 *                      A hashMap of barcoded products and their associated
	 *                      quantity in shopping cart
	 * @param funds
	 *                      The funds used in the session
	 * @param weight
	 *                      The weight of the items and actual weight on the scale
	 *                      during the session
	 */
	public void setup(HashMap<BarcodedProduct, Integer> barcodedItems, Funds funds, Weight weight) {
		this.barcodedItems = barcodedItems;
		this.funds = funds;
		this.weight = weight;
		this.weight.register(new WeightDiscrepancyListener());
		this.funds.register(new PayListener());
	}

	/**
	 * Sets the session to have started, allowing customer to interact with station
	 */
	public void start() {
		sessionState = SessionState.IN_SESSION;
		// barcodedItems.clear();
		// funds.clear();
		// weight.clear();
	}

	/**
	 * Cancels the current session and resets the current session
	 */
	public void cancel() {
		sessionState = SessionState.PRE_SESSION;
	}

	/**
	 * Blocks the current session, preventing further action from the customer
	 */
	private void block() {
		if (sessionState.inPay()) {
			funds.setPay(false);
		} else {
			sessionState = SessionState.BLOCKED;
		}
	}

	/**
	 * Resumes the session, allowing the customer to continue interaction
	 */
	private void resume() {
		if (sessionState.inPay()) {
			funds.setPay(true);
		} else {
			sessionState = SessionState.IN_SESSION;
		}
	}

	/**
	 * Enters the pay mode for the customer. Prevents customer from adding further
	 * items by freezing session.
	 */
	public void pay() {
		if (!barcodedItems.isEmpty()) {
			sessionState = SessionState.PAY_BY_CASH;
			funds.setPay(true);
		} else {
			throw new CartEmptyException("Cannot pay for an empty order");
		}
	}

	/**
	 * Static getter for session state
	 *
	 * @return
	 *         Session State
	 */
	public static final SessionState getState() {
		return sessionState;
	}

	/**
	 * Adds a barcoded product to the hashMap of the barcoded products. Updates the
	 * expected weight and price
	 * of the system based on the weight and price of the product.
	 *
	 * @param product
	 *                The product to be added to the HashMap.
	 */
	public void addItem(BarcodedProduct product) {
		if (barcodedItems.containsKey(product)) {
			barcodedItems.replace(product, barcodedItems.get(product) + 1);
		} else {
			barcodedItems.put(product, 1);
		}
		double weight = product.getExpectedWeight();
		long price = product.getPrice();
		Mass mass = new Mass(weight);
		BigDecimal itemPrice = new BigDecimal(price);
		this.weight.update(mass);
		funds.update(itemPrice);
	}

	private boolean callBulkyItem = true;

	/**
	 * Subtracts the weight of the bulky item from the total expected weight
	 * of the system
	 * Can only call if there is a weight discrepancy
	 *
	 * If called when there is no weight discrepancy, then nothing happens
	 */
	public void addBulkyItem() {
		if (Session.getState() != SessionState.BLOCKED) {
			this.callBulkyItem = false;
			return;
		}

		Mass bulkyItemWeight = this.weight.getLastWeightAdded();
		this.weight.subtract(bulkyItemWeight);
	}

	/**
	 * method to check if addBulkyItem() can be called or not
	 * @return boolean callBulkyItem
	 * true if addBulkyItem() can be called
	 * false if addBulkyItem() cannot be called
	 */
	public boolean getCallAddBulkyItem() {
		return this.callBulkyItem;
	}

	/**
	 * determines if customer can call bulky item or not
	 * customer cannot call bulky item if there is a weight discrepancy
	 * @return boolean value representing callBulkyItem
	 */
	public HashMap<BarcodedProduct, Integer> getBarcodedItems() {
		return barcodedItems;
	}

	public Funds getFunds() {
		return funds;
	}

	public Weight getWeight() {
		return weight;
	}

}
