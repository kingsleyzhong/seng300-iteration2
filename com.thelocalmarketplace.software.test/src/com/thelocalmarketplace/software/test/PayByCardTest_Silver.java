package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.IllegalDigitException;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.card.MagneticStripeFailureException;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.card.BlockedCardException;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.NoCashAvailableException;
import com.tdc.coin.CoinValidator;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.hardware.external.CardIssuer;
import com.thelocalmarketplace.software.SelfCheckoutStationLogic;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;
import com.thelocalmarketplace.software.funds.CardIssuerDatabase;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.funds.FundsListener;
import com.thelocalmarketplace.software.funds.PayByCard;
import com.thelocalmarketplace.software.funds.SupportedCardIssuers;

import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

/**
 *  <p> Testing for the Funds class </p>
 * 
 *  <p> Project iteration 2 group members: </p>
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

public class PayByCardTest_Silver {
	private SelfCheckoutStationSilver scs;
	private ArrayList <CardIssuer> supportedCardsClasses = new ArrayList <CardIssuer>();
	private CardIssuer ci1;
	private CardIssuer ci2;
	private CardIssuer ci3;
	private CardIssuer ci4;
	private Card disCard;
	private Card viva;
	private Card cdnDep;
	private Card debit;
	private Funds funds;
	private MockSession mockSession;
	
	/***
	 * Mock Session to make the session pay mode in Pay by Card
	 */
		public class MockSession extends Session {
			
			@Override
			public void payByCard() {
				sessionState = SessionState.PAY_BY_CARD;
			}
			
			public void block() {
				sessionState = SessionState.BLOCKED;
			}
		}
	
	@Before
	public void setup() {
		AbstractSelfCheckoutStation.resetConfigurationToDefaults();

		mockSession = new MockSession();
    	
		scs = new SelfCheckoutStationSilver();
		scs.plugIn(PowerGrid.instance());
		PowerGrid.engageUninterruptiblePowerSource();
		scs.turnOn();
		funds = new Funds(scs);
		
		ci1 = new CardIssuer(SupportedCardIssuers.ONE.getIssuer(), 1);
		ci2 = new CardIssuer(SupportedCardIssuers.TWO.getIssuer(), 5);
		ci3 = new CardIssuer(SupportedCardIssuers.THREE.getIssuer(), 99);
		ci4 = new CardIssuer(SupportedCardIssuers.FOUR.getIssuer(), 2);
		
		supportedCardsClasses.add(ci1);
		supportedCardsClasses.add(ci2);
		supportedCardsClasses.add(ci3);
		supportedCardsClasses.add(ci4);

		int index = 0;
		for(SupportedCardIssuers supportedCards : SupportedCardIssuers.values()) {
			CardIssuerDatabase.CARD_ISSUER_DATABASE.put(supportedCards.getIssuer(), supportedCardsClasses.get(index));
			index ++;
		}
		
		disCard = new Card(SupportedCardIssuers.ONE.getIssuer(), "5299334598001547", "Cindy Wiggins", "489");
		viva = new Card(SupportedCardIssuers.TWO.getIssuer(), "9999999999999999", "MONEY BAGS", "777");
		cdnDep = new Card(SupportedCardIssuers.THREE.getIssuer(), "7892457826750349", "James McGill", "123");
		debit = new Card(SupportedCardIssuers.FOUR.getIssuer(), "5160617843321186", "Mark Klaassen", "111");
		
		Calendar exp = Calendar.getInstance();
		exp.set(Calendar.YEAR, 2099);
		exp.set(Calendar.MONTH, 12);
		
		ci1.addCardData(disCard.number, disCard.cardholder, exp, disCard.cvv, 10000);
		ci2.addCardData("0", viva.cardholder, exp, viva.cvv, 7500);
		ci3.addCardData(cdnDep.number, cdnDep.cardholder, exp, cdnDep.cvv, 1000);
		ci4.addCardData(debit.number, debit.cardholder, exp, debit.cvv, 2000);
	}
	
// ---------- SILVER TESTS ----------	
		
	@Test (expected = NoPowerException.class)
	public void powerOffSwipe() throws IOException{
		scs.turnOff();
		scs.cardReader.swipe(debit);
		scs.turnOn();
		// Swiping a card when the reader is not supposed to be in use (wrong session state
		// Expect that aCardHasBeenSwiped throws InvalidActionException
	}
	
	@Test (expected = InvalidActionException.class)
	public void swipeIncorrectState() throws IOException{
		mockSession.block();
		while(!funds.successfulSwipe) {
			try {
				scs.cardReader.swipe(debit);
			} catch (MagneticStripeFailureException e) {
			}
		}
		// Swiping a card when the reader is not supposed to be in use (wrong session state
		// Expect that aCardHasBeenSwiped throws InvalidActionException
	}
	
	@Test (expected = InvalidActionException.class)
	public void testInvalidCardNumber() throws IOException{
		long price = 100;
		BigDecimal itemPrice = new BigDecimal(price);
		mockSession.payByCard();
		funds.update(itemPrice);
		funds.beginPayment();
		while(!funds.successfulSwipe) {
			try {
				scs.cardReader.swipe(viva);
			} catch (MagneticStripeFailureException e) {
			}
		}
		// The card numbers do not match and will decline a card if the card is blocked
		// authorizeHold should return -1 
		// How do we effectively call authorize hold
	}
	
	@Test (expected = InvalidActionException.class)
	public void testBlockedCard() throws IOException{
		long price = 100;
		BigDecimal itemPrice = new BigDecimal(price);
		mockSession.payByCard();
		funds.update(itemPrice);
		funds.beginPayment();
		ci4.block(debit.number);
		while(!funds.successfulSwipe) {
			try {
				scs.cardReader.swipe(debit);

			} catch (MagneticStripeFailureException e) {
			}
		}
		// This will decline a card if the card is blocked
		// authorizeHold should return -1 
	}
	
	// PayByCard currently is not capable of doing anything with the -1 value; change this?
	// Otherwise testing both that cards are counting correct hold counts or not (redundant?)
	@Test (expected = InvalidActionException.class)
	public void testHoldCountDecline() throws IOException{
		ci1.authorizeHold(disCard.number, 1);
		long price = 100;
		BigDecimal itemPrice = new BigDecimal(price);
		mockSession.payByCard();
		funds.update(itemPrice);
		funds.beginPayment();
		ci1.authorizeHold(disCard.number, 1);
		assertEquals(-1, ci1.authorizeHold(disCard.number, 1));
		ci1.releaseHold(disCard.number, 1);
		while(!funds.successfulSwipe) {
			try {
				scs.cardReader.swipe(disCard);
			} catch (MagneticStripeFailureException e) {
			}
		}
	}
	
	@Test (expected = InvalidActionException.class)
	public void testAvailableBalanceDecline() throws IOException{
		long price = 1000000;
		BigDecimal itemPrice = new BigDecimal(price);
		funds.update(itemPrice);
		mockSession.payByCard();
		funds.beginPayment();
		while(!funds.successfulSwipe) {
			try {
				scs.cardReader.swipe(disCard);
			} catch (MagneticStripeFailureException e) {
			}
		}
		// This will decline a card if there is insufficient available balance 
		// postTransaction should return false 
	}

	@Test
	public void testSuccessfulPostingTransaction() throws IOException{
		mockSession.payByCard();
		long price = 10;
		BigDecimal itemPrice = new BigDecimal(price);
		funds.update(itemPrice);
		funds.beginPayment();
		
		while(!funds.payed) {
			try {
				scs.cardReader.swipe(debit);
				assertTrue(funds.payed);
			} catch (MagneticStripeFailureException e) {
			}
		}
		// This will post a successful charge on the given card
		// postTransaction should return true 
	}	
}
