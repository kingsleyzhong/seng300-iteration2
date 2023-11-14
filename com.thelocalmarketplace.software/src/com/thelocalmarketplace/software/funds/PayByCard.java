package com.thelocalmarketplace.software.funds;

import com.thelocalmarketplace.hardware.external.*;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;

import java.io.IOException;

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
	public Card card;
	
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
				System.out.println("Declined");
				e.printStackTrace();
			}	
		}

		@Override
		public void theDataFromACardHasBeenRead(CardData data) {	
				// Not sure yet what happens here
				// Card card = new Card(data.getType(), data.getNumber(), data.getCardholder(), data.getCVV());	
		}
	}
	
	public void getTransactionFromBank() {
		if (Session.getState() == SessionState.PAY_BY_CARD) {
			
			// Not sure yet what happens here also
			// Some card types to determine what object of CardIssuer to use
			// Can be changed or removed as required
			if (card.kind == "DisasterCard") {
				
			} else if (card.kind == "Viva") {
				
			} else if (card.kind == "Canadian Depress") {
				
			} else if (card.kind == "Detrac Debit") {
				
			} else {
				throw new InvalidActionException("Card not recognized");
			}
			
		} else {
			throw new InvalidActionException("Not in Card Payment state");
		}

	}
}
