package com.thelocalmarketplace.software.funds;

import java.math.BigDecimal;
import java.util.Currency;

import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.banknote.BanknoteValidatorObserver;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinValidator;
import com.tdc.coin.CoinValidatorObserver;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

/***
 * This class contains the observers for pay by cash events 
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


public class PayByCashController {
	
	private BigDecimal cashPaid; //amount of cash that has been paid
	private Funds fund;
	
/***
 * Constructor that begins the total paid in cash at 0
 * @param scs
 * @param funds 
 * @param paid 
 */
	public PayByCashController(AbstractSelfCheckoutStation scs, Funds funds) {
		
		this.cashPaid = BigDecimal.ZERO;
				
		InnerCoinListener coinListener = new InnerCoinListener();
		InnerBankNoteListener banknoteListener = new InnerBankNoteListener();
		scs.coinValidator.attach(coinListener);
		scs.banknoteValidator.attach(banknoteListener);
		this.fund = funds;
		
		
				
	}
	
/***
 * InnerListener that observes for a valid coin to be received
 */
		 private class InnerCoinListener implements CoinValidatorObserver {

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
				
	            if (Session.getState() == SessionState.PAY_BY_CASH) {
	                updateCoin(value); 
	            }
	            
	            else {
	            	throw new InvalidActionException("Pay is not activated at the moment.");
	            }
				
			}

			public void invalidCoinDetected(CoinValidator validator) {
				//Dealt with in the hardware
			}
			
		 }
 /***
  * InnerListener that observes for a valid banknote to be received
  */
		private class InnerBankNoteListener implements BanknoteValidatorObserver {

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
			public void goodBanknote(BanknoteValidator validator, Currency currency, BigDecimal denomination) {
				if (currency == null) {
					throw new NullPointerSimulationException("Null is not a valid currency.");
				}
				
				if (denomination.compareTo(BigDecimal.ZERO) <= 0) {
	                throw new IllegalArgumentException("Coin value should be positive.");
	            }
				
	            if (Session.getState() == SessionState.PAY_BY_CASH) {
	            	updateBankNote(denomination); 
	            	
	            }
	            
	            else {
	            	throw new InvalidActionException("Pay is not activated at the moment.");
	            }
				
			}

			@Override
			public void badBanknote(BanknoteValidator validator) {
				//Dealt with in the hardware
			}	
	}
		
/***
 * This method will update the cashPaid based on the coin received
 */
	private void updateCoin(BigDecimal value) {
				
		this.cashPaid = this.cashPaid.add(value);				
		this.fund.updatePaidCash();
				
	}
	
/***
 * This method will update the cashPaid based on the banknote received
 */
	private void updateBankNote(BigDecimal denomination) {
		
		this.cashPaid = this.cashPaid.add(denomination);
		this.fund.updatePaidCash();
	
	}

/***
 * Getter for cashPaid
 * @return cashPaid
 */
	public BigDecimal getCashPaid() {
				
		return this.cashPaid;
		
		
	}
	
}
