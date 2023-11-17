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


	private Mass MAXBAGWEIGHT = new Mass(500 * Mass.MICROGRAMS_PER_GRAM); // maximum weight of a bag for this system
																		  // unless configured, set to 500g ~ 1lb
	private Mass ActualMassBeforeAddBag = Mass.ZERO; 
	
	
	
	private class WeightDiscrepancyListener implements WeightListener {

		/**
		 * Upon a weightDiscrepancy, session should freeze
		 * 
		 * If the Customer has declared their intention to add bags to the scale, then checks
		 * the bags instead.
		 */
		@Override
		public void notifyDiscrepancy() {
			// Only needed when the customer wants to add their own bags (this is how Session knows the bags' weight)
			if(sessionState == SessionState.ADDING_BAGS) {
				checkBags();
				return;
			}
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
	 * Constructor for session that also allows the MAX BAG WEIGHT to be defined 
	 * 
	 * @params maxBagWeight 
	 * 				double representing the expected weight of a bag (in grams)
	 */
	public Session(double maxBagWeight) {
		configureMAXBAGWEIGHT(maxBagWeight);
		sessionState = SessionState.PRE_SESSION;

	}
	
	/**
	 * Constructor for session that also allows the MAX BAG WEIGHT to be defined 
	 * 
	 * @param maxBagWeight
	 * 						 long representing the expected weight of a bag (in micrograms)
	 */
	public Session(long maxBagWeight) {
		configureMAXBAGWEIGHT(maxBagWeight);
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

	/**
	 * The customer indicates they want to add a bag by calling addBags 
	 * Changes the state of the Session to "ADDING_BAGS"
	 * System is now waiting for bags to be added to the bagging area.
	 * 
	 */
	public void addBags() {
		// can only occur during an active session
		// this prevents the user from adding bags while already adding bags
		if(sessionState == SessionState.IN_SESSION){
			Session.sessionState = SessionState.ADDING_BAGS;// change the state to the add bags state

			// get the weight of the scale before adding the bag
			ActualMassBeforeAddBag = this.getWeight().getActualWeight();
					
			// signal customer to add bag to the bagging area (somehow) (GUI problem)
		}
		// else: nothing changes about the Session's state
	}
	
	/*
	 *  Runs when a customer has signaled their desire to add their own bags to the bagging area,
	 *  and then a change in the bagging area was recorded.
	 *  
	 *  Compares the weight on the scale to the the weight before adding bags to determine if bags were added
	 *  and that the bags are below the specified maximum bag weight (MAXBAGWEIGHT). 
	 *  
	 *  If the weight change was negative -> notifies unexpected change in the bagging area
	 *  									and blocks the system. Expected weight is not updated.
	 *  If the bags are too heavy -> notifies attendant and customer. Blocks the system
	 *  Else: bags were accepted. Expected weight is updated to include the bag weight. Session returns to normal runtime state.
	 *  
	 */
	private void checkBags() {
		//get the weight of the scale after the bag was added
		Mass ActualMassAfterAddingBag = this.getWeight().getActualWeight();
		
		// check that the weight change was caused by adding weight
		if(ActualMassAfterAddingBag.compareTo(ActualMassBeforeAddBag)  < 0 ){
			// unexpected change in the bagging area
			// signal problem to the customer
			// do not update the expected weight
			
			// cancel the interaction
			this.block();//blocks the system
			return;
		}
	
		// store the bags weight
		// weight of the bag is the difference between the weight on the scale after and before adding the bags
		// this should work, this should never be negative
		Mass actualBagWeight = ActualMassAfterAddingBag.difference(ActualMassBeforeAddBag).abs();
				
		// check if the updated weight is to heavy for just a bag (Throw exception??)
		// if weight > expected weight of a bag
		if(actualBagWeight.compareTo(MAXBAGWEIGHT) >= 0) {
			bagsTooHeavy(); 
			return;
		}
		else {
			// else: the bag added is within the allowed weight range
			// update the expected weight on the scale
			this.weight.update(actualBagWeight);

		}
		// returns the Session to the normal runtime state
		this.resume();
	}
	
	/*
	 * Allows customer to signal they no longer wish to add bags. 
	 * Will not remove already added bags, but will return the session to normal runtime behavior. 
	 */
	public void cancelAddBags() {
		// resumes normal functioning only when in the adding bags state
		if(Session.sessionState == SessionState.ADDING_BAGS) {
			this.resume();// changes the state
		}
		// else: does nothing
		
	}
	
	/*
	 * Occurs when the bags the Customer added to the bagging area are above the maximum allowed bag weight 
	 * (set by MAXBAGWEIGHT, able to be configured). 
	 * 
	 * Currently sorta useless without an attendant or any way to contact an attendant
	 * 
	 * Once blocked this could be overrides the same as any other blocked state
	 */
	private void bagsTooHeavy() {
		// notifies attendant 
		// block the system
		this.block();		
	}
	

	/**
	 * Returns the maximum bag weight for the system in grams (this one is secure)
	 */
	public double get_MAXBAGWEIGHT_inGrams() {
		return this.MAXBAGWEIGHT.inGrams().doubleValue();
	}
	
	/**
	 * Returns the maximum bag weight for the system in grams (this one is secure)
	 */
	public long get_MAXBAGWEIGHT_inMicrograms() {
		return this.MAXBAGWEIGHT.inMicrograms().longValue();
	}
	
	/**
	 * Sets the maximum bag weight for this session 
	 * 
	 * @params 
	 * 	maxBagWeight: double representing the maximum weight of a bag (in grams)
	 */
	public void configureMAXBAGWEIGHT(double maxBagWeight) {
		MAXBAGWEIGHT = new Mass(maxBagWeight);

	}
	/**
	 * Sets the maximum bag weight for this session 
	 * 
	 * @params 
	 * 	maxBagWeight: long representing the maximum weight of a bag (in micrograms)
	 */
	public void configureMAXBAGWEIGHT(long maxBagWeight) {
		MAXBAGWEIGHT = new Mass(maxBagWeight);
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
