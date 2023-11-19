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
	
	public PayByCard(AbstractSelfCheckoutStation scs, Funds funds) {
		InnerListener cardListener = new InnerListener();
		scs.cardReader.register(cardListener);
		
		amountDue = funds.getAmountDue().doubleValue();
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
			 try {
				theDataFromACardHasBeenRead(card.swipe());
				if(Session.getState() != SessionState.PAY_BY_CARD) {
					throw new InvalidActionException("Card reader not in use");
				}
			} catch (BlockedCardException  | IOException e) {
				System.out.println("Declined");
				e.printStackTrace();
			}
		}

		@Override
		public void theDataFromACardHasBeenRead(CardData data) {	
				card = new Card(data.getType(), data.getNumber(), data.getCardholder(), data.getCVV());
				getTransactionFromBank(card);
		}
	}

	
    /**
     * Facilitates all communication with CardIssuer(s) required for billing/posting 
     */
	public void getTransactionFromBank(Card card) {
		if (Session.getState() == SessionState.PAY_BY_CARD) {
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
					if (!post) {
						// This failed for some reason
						// Credit limit
						return;
					} else {
						// This can fail and return -1 or false or whatever but tbh it seems redundant to even look
						CardIssuerDatabase.CARD_ISSUER_DATABASE.get(SupportedCardIssuers.ONE.getIssuer()).releaseHold(card.number, 1);
					}
					isPaid(paidBool);		
				}
								
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
					if (!post) {
						// This failed for some reason
						// Credit limit
						return;
					} else {
						// This can fail and return -1 or false or whatever but tbh it seems redundant to even look
						CardIssuerDatabase.CARD_ISSUER_DATABASE.get(SupportedCardIssuers.THREE.getIssuer()).releaseHold(card.number, 1);
					}
					isPaid(paidBool);		
				}
				
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
					if (!post) {
						// This failed for some reason
						// Credit limit
						return;
					} else {
						// This can fail and return -1 or false or whatever but tbh it seems redundant to even look
						CardIssuerDatabase.CARD_ISSUER_DATABASE.get(SupportedCardIssuers.FOUR.getIssuer()).releaseHold(card.number, 1);
					}
					isPaid(paidBool);
					
				}
			} else {
				throw new InvalidActionException("Card not recognized");
			}		
		} else {
			throw new InvalidActionException("Not in Card Payment state");
		}
	}
	private void isPaid(Boolean paidBool) {
		paidBool = true;
	}
}
