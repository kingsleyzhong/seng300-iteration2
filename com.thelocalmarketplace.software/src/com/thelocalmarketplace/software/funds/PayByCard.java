package com.thelocalmarketplace.software.funds;

import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.external.*;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.card.Card.CardData;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.NoCashAvailableException;
import com.jjjwelectronics.card.*;

/**
 * <p> This class facilitates communication between com.jjjwelectronics.card.CardReaderListener and com.thelocalmarketplace.software.funds.Funds</p> 
 *  
 * <p>Project iteration 2 group members: </p>
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
public class PayByCard {
	
	private Card card;
	private PayByCard cardController;
	private double amountDue;
	boolean paidBool;
	boolean successfulSwipe;
	boolean posted;
	private Funds funds;
	private AbstractSelfCheckoutStation scs;
	
	public PayByCard(AbstractSelfCheckoutStation scs, Funds funds) {
		InnerListener cardListener = new InnerListener();
		scs.cardReader.register(cardListener);
		
		amountDue = funds.getAmountDue().doubleValue();
		this.funds = funds;
		this.scs = scs;
	}
	
	private class InnerListener implements CardReaderListener {

		@Override
		public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void aCardHasBeenSwiped(){
			paidBool = false;
			successfulSwipe = true;
			// TODO Auto-generated method stub
			
		}

		@Override
		public void theDataFromACardHasBeenRead(CardData data) {	
			card = new Card(data.getType(), data.getNumber(), data.getCardholder(), null);
//			if(Session.getState() != SessionState.PAY_BY_CARD) {
//				throw new InvalidActionException("Card reader not in use");
//			}
				try {
					getTransactionFromBank(card);
				} catch (CashOverloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoCashAvailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DisabledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	
    /**
     * Facilitates all communication with CardIssuer(s) required for billing/posting 
     * @throws DisabledException 
     * @throws NoCashAvailableException 
     * @throws CashOverloadException 
     */
	public void getTransactionFromBank(Card card) throws CashOverloadException, NoCashAvailableException, DisabledException {
		// We need to retrieve the funds
		// We determine the type of card, check the database for validity, then attempt 		
		if (card.kind == SupportedCardIssuers.ONE.getIssuer()) {
			long holdNumber = CardIssuerDatabase.CARD_ISSUER_DATABASE.get(SupportedCardIssuers.ONE.getIssuer()).authorizeHold(card.number, 1);
			
			if (holdNumber == -1L) {
				// There are not enough available holds
				// Invalid card
				// Blocked card
				// Maxed holds
				return;
			} else {	
				boolean post = CardIssuerDatabase.CARD_ISSUER_DATABASE.get(SupportedCardIssuers.ONE.getIssuer()).postTransaction(card.number, holdNumber, amountDue);
				// May failed for some reason
				// Credit limit

				// This can fail and return -1 or false or whatever but tbh it seems redundant to even look
				CardIssuerDatabase.CARD_ISSUER_DATABASE.get(SupportedCardIssuers.ONE.getIssuer()).releaseHold(card.number, 1);
				}
				paidBool = true;
				funds.updatePaidCard(paidBool);
				successfulSwipe = false;
				return;
							
		} else if (card.kind == SupportedCardIssuers.TWO.getIssuer()) {
			long holdNumber = CardIssuerDatabase.CARD_ISSUER_DATABASE.get(SupportedCardIssuers.TWO.getIssuer()).authorizeHold(card.number, 1);
			
			if (holdNumber == -1L) {
				// There are not enough available holds
				// Invalid card
				// Blocked card
				// Maxed holds
				return;
			} else {	
				boolean post = CardIssuerDatabase.CARD_ISSUER_DATABASE.get(SupportedCardIssuers.TWO.getIssuer()).postTransaction(card.number, holdNumber, amountDue);
				// May failed for some reason
				// Credit limit

				// This can fail and return -1 or false or whatever but tbh it seems redundant to even look
				CardIssuerDatabase.CARD_ISSUER_DATABASE.get(SupportedCardIssuers.TWO.getIssuer()).releaseHold(card.number, 1);
				}
				paidBool = true;
				funds.updatePaidCard(paidBool);
				return;
							
		} else if (card.kind == SupportedCardIssuers.THREE.getIssuer()) {
			long holdNumber = CardIssuerDatabase.CARD_ISSUER_DATABASE.get(SupportedCardIssuers.THREE.getIssuer()).authorizeHold(card.number, 1);
			
			if (holdNumber == -1L) {
				// There are not enough available holds
				// Invalid card
				// Blocked card
				// Maxed holds
				return;
			} else {	
				boolean post = CardIssuerDatabase.CARD_ISSUER_DATABASE.get(SupportedCardIssuers.THREE.getIssuer()).postTransaction(card.number, holdNumber, amountDue);
				// May failed for some reason
				// Credit limit

				// This can fail and return -1 or false or whatever but tbh it seems redundant to even look
				CardIssuerDatabase.CARD_ISSUER_DATABASE.get(SupportedCardIssuers.THREE.getIssuer()).releaseHold(card.number, 1);
				}
				paidBool = true;
				funds.updatePaidCard(paidBool);		
				successfulSwipe = false;
				return;
			
		} else if (card.kind == SupportedCardIssuers.FOUR.getIssuer()) {
			long holdNumber = CardIssuerDatabase.CARD_ISSUER_DATABASE.get(SupportedCardIssuers.FOUR.getIssuer()).authorizeHold(card.number, 1);
			
			if (holdNumber == -1L) {
				// There are not enough available holds
				// Invalid card
				// Blocked card
				// Maxed holds
				return;
			} else {	
				boolean post = CardIssuerDatabase.CARD_ISSUER_DATABASE.get(SupportedCardIssuers.FOUR.getIssuer()).postTransaction(card.number, holdNumber, amountDue);
				// May failed for some reason
				// Credit limit

				// This can fail and return -1 or false or whatever but tbh it seems redundant to even look
				CardIssuerDatabase.CARD_ISSUER_DATABASE.get(SupportedCardIssuers.FOUR.getIssuer()).releaseHold(card.number, 1);

				paidBool = true;
				funds.updatePaidCard(paidBool);
				successfulSwipe = false;
				return;
			}
		} else {
			throw new InvalidActionException("Declined");
		}
	}	
}

