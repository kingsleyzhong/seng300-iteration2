package com.thelocalmarketplace.software.funds;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.jjjwelectronics.IllegalDigitException;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.coin.CoinValidator;
import com.tdc.coin.CoinValidatorObserver;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

/**
 * This class represents the funds associated with a self-checkout session.
 * It manages the total price of items, the amount paid, and the amount due.
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
 */
public class Funds {
	protected ArrayList<FundsListener> listeners = new ArrayList<>();
    private BigDecimal itemsPrice; // Summed price of all items in the session (in cents)
    private BigDecimal paid;       // Amount paid by the customer (in cents)
    private BigDecimal amountDue;  // Remaining amount to be paid (in cents)
    private boolean isPay;   // Flag indicating if the session is in pay mode


    /**
     * Constructor that initializes the funds and registers an inner listener to the self-checkout station.
     * 
     * @param scs The self-checkout station
     */
    public Funds (SelfCheckoutStationBronze scs) {
        if (scs == null) {
            throw new IllegalArgumentException("SelfCheckoutStation should not be null.");
        }
        this.itemsPrice = BigDecimal.ZERO;
        this.paid = BigDecimal.ZERO;
        this.amountDue = BigDecimal.ZERO;
        this.isPay = false;
        InnerListener listener = new InnerListener();
        scs.coinValidator.attach(listener);
    }
    
    public Funds (SelfCheckoutStationSilver scs) {
        if (scs == null) {
            throw new IllegalArgumentException("SelfCheckoutStation should not be null.");
        }
        this.itemsPrice = BigDecimal.ZERO;
        this.paid = BigDecimal.ZERO;
        this.amountDue = BigDecimal.ZERO;
        this.isPay = false;
        InnerListener listener = new InnerListener();
        scs.coinValidator.attach(listener);
    }
    
    public Funds (SelfCheckoutStationGold scs) {
        if (scs == null) {
            throw new IllegalArgumentException("SelfCheckoutStation should not be null.");
        }
        this.itemsPrice = BigDecimal.ZERO;
        this.paid = BigDecimal.ZERO;
        this.amountDue = BigDecimal.ZERO;
        this.isPay = false;
        InnerListener listener = new InnerListener();
        scs.coinValidator.attach(listener);
    }
    
    /**
     * Inner class to listen for valid coin additions and update the paid amount.
     */
    public class InnerListener implements CoinValidatorObserver {

		@Override
		public void enabled(IComponent<? extends IComponentObserver> component) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void disabled(IComponent<? extends IComponentObserver> component) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void turnedOn(IComponent<? extends IComponentObserver> component) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void turnedOff(IComponent<? extends IComponentObserver> component) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void validCoinDetected(CoinValidator validator, BigDecimal value) {
			if (value.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Coin value should be positive.");
            }
            if (isPay) {
                paid = paid.add(value);
                calculateAmountDue();
            }
            else {
            	throw new InvalidActionException("Pay is not activated at the moment.");
            }
			
		}

		@Override
		public void invalidCoinDetected(CoinValidator validator) {
			// TODO Auto-generated method stub
			
		}
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
     * Calculates the amount due by subtracting the paid amount from the total items price.
     */
    private void calculateAmountDue() {
        this.amountDue = this.itemsPrice.subtract(this.paid);
        
        // To account for any rounding errors, checks if less that 0.0005 rather than just 0
        if (amountDue.intValue() <= 0.0005) {
			for(FundsListener l : listeners)
				l.notifyPaid();
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
		if(listener == null)
			throw new NullPointerSimulationException("listener");

		listeners.add(listener);
	}
}
