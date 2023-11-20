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

import StubClasses.FundsStub;
import StubClasses.SessionStub;
import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.PowerGrid;

/**
 * Testing for the Funds class
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

public class PayByCardTest {
	private SelfCheckoutStationBronze scs;
	private SelfCheckoutStationSilver scss;
	private SelfCheckoutStationGold scsg;
    private CoinValidator validator;
    private BigDecimal value;
    private BigDecimal price;
	private static SupportedCardIssuers supportedCards;
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
	private Funds fundss;
	private Funds fundsg;
	private SessionStub session;
	private BarcodedProduct product;
	private Barcode barcode; 
	
	@Before
	public void setup() {
		AbstractSelfCheckoutStation.resetConfigurationToDefaults();
		
        barcode = new Barcode(new Numeral[] { Numeral.valueOf((byte) 1) });
        product = new BarcodedProduct(barcode, "Product 1", 10, 100.0);

		session = new SessionStub();
    	
		scs = new SelfCheckoutStationBronze();
		scs.plugIn(PowerGrid.instance());
		scs.turnOn();
		funds = new Funds(scs);
//		SelfCheckoutStationLogic.installOn(scs, session);

//		scss = new SelfCheckoutStationSilver();
//		scss.plugIn(PowerGrid.instance());
//		scss.turnOn();
//		fundss = new Funds(scss);
//
//		scsg = new SelfCheckoutStationGold();
//		scsg.plugIn(PowerGrid.instance());
//		scsg.turnOn();
//		fundsg = new Funds(scsg);
		
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
		
		disCard = new Card(SupportedCardIssuers.ONE.getIssuer(), "5299334598001547", "Brandon Chan", "666");
		viva = new Card(SupportedCardIssuers.TWO.getIssuer(), "4504389022574000", "Dorris Giles", "343");
		cdnDep = new Card(SupportedCardIssuers.THREE.getIssuer(), "1111111111111111", "Not A Real Person", "420");
		debit = new Card(SupportedCardIssuers.FOUR.getIssuer(), "5160617843321186", "Robehrt Lazar", "111");
		
		Calendar exp = Calendar.getInstance();
		exp.set(Calendar.YEAR, 2099);
		exp.set(Calendar.MONTH, 12);
		
		ci1.addCardData(disCard.number, disCard.cardholder, exp, disCard.cvv, 10000);
		ci2.addCardData(viva.number, viva.cardholder, exp, viva.cvv, 7500);
		ci3.addCardData("0", cdnDep.cardholder, exp, cdnDep.cvv, 1);
		ci4.addCardData(debit.number, debit.cardholder, exp, debit.cvv, 2);
	}
	
	@Test (expected = InvalidActionException.class)
	public void swipeIncorrectState() throws IOException, CashOverloadException, NoCashAvailableException, DisabledException{
		session.start();
		scs.cardReader.swipe(debit);
		// Swiping a card when the reader is not supposed to be in use (wrong session state
		// Expect that aCardHasBeenSwiped throws InvalidActionException
	}
	
	@Test (expected = IOException.class)
	public void testInvalidCardNumber() throws IOException, CashOverloadException, NoCashAvailableException, DisabledException{
		long price = 100;
		BigDecimal itemPrice = new BigDecimal(price);
		session.payByCard();
		funds.update(itemPrice);
		funds.beginPayment();
		scs.cardReader.swipe(cdnDep);
		// The card numbers do not match and will decline a card if the card is blocked
		// authorizeHold should return -1 
		// How do we effectively call authorize hold
	}
	
	@Test (expected = BlockedCardException.class)
	public void testBlockedCard() throws IOException, CashOverloadException, NoCashAvailableException, DisabledException {
		long price = 100;
		BigDecimal itemPrice = new BigDecimal(price);
		session.payByCard();
		funds.update(itemPrice);
		funds.beginPayment();
		ci4.block(debit.number);
		scs.cardReader.swipe(debit);
		// This will decline a card if the card is blocked
		// authorizeHold should return -1 
	}
	
	// The following test works if we do not actually swipe a card
	// PayByCard currently is not capable of doing anything with the -1 value; change this?
	@Test
	public void testHoldCountDecline() throws IOException, CashOverloadException, NoCashAvailableException, DisabledException {
		ci1.authorizeHold(disCard.number, 1);
		long price = 100;
		BigDecimal itemPrice = new BigDecimal(price);
		session.payByCard();
		funds.update(itemPrice);
		funds.beginPayment();
		assertEquals(-1, ci1.authorizeHold(disCard.number, 1));
		// This will decline a card if it has run out of available holds
		// authorizeHold should return -1 
	}
	
	@Test
	public void testAvailableBalanceDecline() throws CashOverloadException, NoCashAvailableException, DisabledException, IOException {
		long price = 1000000;
		BigDecimal itemPrice = new BigDecimal(price);
		funds.update(itemPrice);
		session.payByCard();
		funds.beginPayment();
		scs.cardReader.swipe(viva);	
		// This will decline a card if there is insufficient available balance 
		// postTransaction should return false 
	}

	@Test
	public void testSuccessfulPostingTransaction() throws CashOverloadException, NoCashAvailableException, DisabledException, IOException {

		long price = 100;
		BigDecimal itemPrice = new BigDecimal(price);
		funds.update(itemPrice);
		session.payByCard();
		funds.beginPayment();		
		scs.cardReader.swipe(viva);	
		// This will post a successful charge on the given card
		// postTransaction should return true 
	}
	
    
}