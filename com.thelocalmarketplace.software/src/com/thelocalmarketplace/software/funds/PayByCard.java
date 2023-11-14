package com.thelocalmarketplace.software.funds;

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
	private double amountDue;
	private HashMap <String, CardIssuer> bankList;
	
	public PayByCard(Funds funds, HashMap<String,CardIssuer> banks) {
		amountDue = funds.getAmountDue().doubleValue();
		bankList.putAll(banks);
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
			} catch (BlockedCardException  | IOException e) {
				System.out.println("Failed");
				e.printStackTrace();
			}	
		}

		@Override
		public void theDataFromACardHasBeenRead(CardData data) {	
				Card card = new Card(data.getType(), data.getNumber(), data.getCardholder(), data.getCVV());	
		}
	}
	
	public boolean getTransactionFromBank() {
		if (Session.getState() == SessionState.PAY_BY_CARD) {
			
			// Not sure yet what happens here also
			// Some card types to determine what object of CardIssuer to use
			// Can be changed or removed as required
			// We need to retrieve the funds
			// We determine the type of card, check the database for validity, then attempt 
			
			if (card.kind == "DisasterCard") {
				long holdNumber = bankList.get("DisasterCard").authorizeHold(card.number, 1);
				
				if (holdNumber == -1L) {
					// There are not enough available holds
					// Invalid card
					// Blocked card
					// Maxed holds
					return false;
				} else {	
					boolean post = bankList.get("DisasterCard").postTransaction(card.number, holdNumber, amountDue);
					if (!post) {
						// This failed for some reason
						// Credit limit
						return false;
					} else {
						// This can fail and return -1 or false or whatever but tbh it seems redundant to even look
						bankList.get("DisasterCard").releaseHold(card.number, 1);
					}
					return true;		
				}
								
			} else if (card.kind == "Canadian Depress") {
				long holdNumber = bankList.get("Canadian Depress").authorizeHold(card.number, 1);
				
				if (holdNumber == -1L) {
					// There are not enough available holds
					// Invalid card
					// Blocked card
					// Maxed holds
					return false;
				} else {	
					boolean post = bankList.get("Canadian Depress").postTransaction(card.number, holdNumber, amountDue);
					if (!post) {
						// This failed for some reason
						// Credit limit
						return false;
					} else {
						// This can fail and return -1 or false or whatever but tbh it seems redundant to even look
						bankList.get("Canadian Depress").releaseHold(card.number, 1);
					}
					return true;		
				}
				
			} else if (card.kind == "Detrac Debit") {
				long holdNumber = bankList.get("Detrac Debit").authorizeHold(card.number, 1);
				
				if (holdNumber == -1L) {
					// There are not enough available holds
					// Invalid card
					// Blocked card
					// Maxed holds
					return false;
				} else {	
					boolean post = bankList.get("Detrac Debit").postTransaction(card.number, holdNumber, amountDue);
					if (!post) {
						// This failed for some reason
						// Credit limit
						return false;
					} else {
						// This can fail and return -1 or false or whatever but tbh it seems redundant to even look
						bankList.get("Detrac Debit").releaseHold(card.number, 1);
					}
					return true;		
				}
			} else {
				throw new InvalidActionException("Card not recognized");
			}		
		} else {
			throw new InvalidActionException("Not in Card Payment state");
		}
	}
}
