package com.thelocalmarketplace.software;

import java.math.BigDecimal;
import java.util.HashMap;

import com.jjjwelectronics.Mass;
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
 * 		Aj Sallh 				: 30023811
 *		Anthony Kostal-Vazquez 	: 30048301
 *		Chloe Robitaille 		: 30022887
 *		Dvij Raval				: 30024340
 *		Emily Kiddle 			: 30122331
 *		Katelan NG 				: 30144672
 *		Kingsley Zhong 			: 30197260
 *		Nick McCamis 			: 30192610
 *		Sua Lim 				: 30177039
 *		Subeg CHAHAL 			: 30196531
 *
 */
public class Session {
	private boolean sessionOn;
	private boolean sessionFrozen;
	private boolean payWithCoin;
	private boolean hasPaid;
	private HashMap<BarcodedProduct, Integer> barcodedItems;
	private Funds funds;
	private Weight weight;
	
	private class WeightDiscrepancyListener implements WeightListener{

		/**
		 * Upon a weightDiscrepancy, session should freeze
		 */
		@Override
		public void notifyDiscrepancy() {
			freeze();		
		}

		/**
		 * Upon resolution of a weightDiscrepancy, session should resume
		 */
		@Override
		public void notifyDiscrepancyFixed() {
			resume();
		}
		
	}
	
	private class PayListener implements FundsListener{

		/**
		 * Signals to the system that the customer has payed the full amount. Ends the session.
		 */
		@Override
		public void notifyPaid() {
			hasPaid = true;
			sessionOn = false;
		}
		
	}
	
	/**
	 * Constructor for the session method. Requires to be installed on self-checkout system 
	 * with logic to function
	 */
	public Session() {
		sessionOn = false;
		sessionFrozen = false;
		payWithCoin = false;
	}
	
	/**
	 * Setup method for the session used in installing logic on the system
	 * Initializes private variables to the ones passed. Initially has the session off, session unfrozen, and pay not
	 * enabled.
	 * 
	 * @param BarcodedItems
	 * 			A hashMap of barcoded products and their associated quantity in shopping cart
	 * @param funds
	 * 			The funds used in the session
	 * @param weight
	 * 			The weight of the items and actual weight on the scale during the session
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
		sessionOn = true;
		//barcodedItems.clear();
		//funds.clear();
		//weight.clear();
	}
	
	/**
	 * Cancels the current session and resets the current session
	 */
	public void cancel() {
		sessionOn = false;
	}
 
	/**
	 * Freezes the current session, preventing further action from the customer
	 */
	private void freeze() {
		if(payWithCoin) {
			funds.setPay(false);
		}
		else {
			sessionFrozen = true;
		}
	}
	
	/**
	 * Resumes the session, allowing the customer to continue interaction
	 */
	private void resume() {
		if(payWithCoin) {
			funds.setPay(true);
		}
		else {
			sessionFrozen = false;
		}
	}
	
	/**
	 * Enters the pay mode for the customer. Prevents customer from adding further items by freezing session.
	 */
	public void pay() {
		if(!barcodedItems.isEmpty()) {
			payWithCoin = true;
			funds.setPay(true);
			sessionFrozen = true;
		}
		else {
			throw new CartEmptyException("Cannot pay for an empty order");
		}
	}
	
	/**
	 * Checks if the session is on
	 * 
	 * @return
	 * 			True if session is on. False is off
	 */
	public boolean isOn() {
		return sessionOn;
	}
	
	/**
	 * Checks if session is frozen
	 * 
	 * @return
	 * 			True if session is frozen. False is not frozen
	 */
	public boolean isFrozen() {
		return sessionFrozen;
	}
	
	/**
	 * Checks if the session is in pay mode
	 * 
	 * @return
	 * 			True if in pay mode, false otherwise.
	 */
	public boolean isPay() {
		return payWithCoin;
	}
	
	/**
	 * Checks if the customer has fully paid for an order
	 * 
	 * @return
	 * 			True if order fully paid for, false if still needs to pay
	 */
	public boolean hasPaid() {
		return hasPaid;
	}
	
	/**
	 * Adds a barcoded product to the hashMap of the barcoded products. Updates the expected weight and price
	 * of the system based on the weight and price of the product.
	 * 
	 * @param product
	 * 			The product to be added to the HashMap.
	 */
	public void addItem(BarcodedProduct product) {
		if (barcodedItems.containsKey(product)) {
			barcodedItems.replace(product, barcodedItems.get(product)+1);
		}
		else {
			barcodedItems.put(product, 1);
		}
		double weight = product.getExpectedWeight();
		long price = product.getPrice();
		Mass mass = new Mass(weight);
		BigDecimal itemPrice = new BigDecimal(price);
		this.weight.update(mass);
		funds.update(itemPrice);
	}
	
	public HashMap<BarcodedProduct, Integer> getBarcodedItems(){
		return barcodedItems;
	}
	
	public Funds getFunds() {
		return funds;
	}
	
	public Weight getWeight() {
		return weight;
	}
	
}
