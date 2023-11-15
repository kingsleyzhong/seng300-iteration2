package com.thelocalmarketplace.software;

import java.math.BigDecimal;
import java.util.HashMap;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Mass.MassDifference;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.software.exceptions.CartEmptyException;
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

	/*
	 * Maximum expected weight of added bag(s)
	 * Eventually this should be able to be changed?
	 * 
	 */
	private Mass MAXBAGWEIGHT = new Mass(500 + Mass.MICROGRAMS_PER_GRAM); // 500g ~ 1lb??
	
	
	
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

	/*
	 * Constructor for session that also allows the MAX BAG WEIGHT to be set 
	 * 
	 * @params 
	 * 	expectedBagWeight: double representing the expected weight of a bag in grams
	 */
	public Session(double expectedBagWeight) {
		MAXBAGWEIGHT = new Mass(expectedBagWeight);
		sessionState = SessionState.PRE_SESSION;
	}
	
	/**
	 * Setup method for the session used in installing logic on the system
	 * Initializes private variables to the ones passed. Initially has the session
	 * off, session unfrozen, and pay not
	 * enabled.
	 * 
	 * @param BarcodedItems
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

	/*
	 * The customer indicates they want to add a bag by calling addbags 
	 * 
	 */
	public void addBags() {
		// can only occure during an active session:
		if(this.getState() == SessionState.IN_SESSION) {
			// put the self checkout into block
			// idk if this is a good idea
			this.block();
		
			//get the weight of the scale before adding the bag
			Weight WeightBeforeAddBag = this.getWeight();
		
			// signal custome to add bag to the bagging area (somehow)
		
			//then the customer adds the bag(s)
			while(this.getWeight().equals(WeightBeforeAddBag)) {
				// customer adds bag(s)
			}
		
			//get the weight of the scale after the bag was added
			Weight WeightAfterAddingBag = this.getWeight();
		
			// check if the updated weight is to heavy for just a bag (Throw exception??)
			// if weight > expected weight of a bag
			if(WeightAfterAddingBag.getActualWeight().compareTo(MAXBAGWEIGHT) > 0) {
				bagsTooHeavy(); 
			}
			// else: the bag added is within the allowed weight range
			// store the bags weight
			// weight of the bag is the difference between the weight on the scale after and before adding the bags
			// this should work, this should never be negative
			Mass actualBagWeight = WeightAfterAddingBag.getActualWeight().difference(WeightBeforeAddBag.getActualWeight()).abs();
		
			// update the expected weight on the scale
			this.weight.update(actualBagWeight);// this should just work???
		
			// unblock the session
			this.start(); 
		
		}
		// else: doesnt do anything
		
	}
	
	/*
	 * 
	 * 
	 * 
	 */
	public void bagsTooHeavy() {
		// this is an attendant method
		// not sure what to put here
		
		// unblock session
		this.start();
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
