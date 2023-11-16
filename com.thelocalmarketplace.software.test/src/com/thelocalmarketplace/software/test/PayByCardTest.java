package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.IllegalDigitException;
import com.jjjwelectronics.card.Card;
import com.tdc.coin.CoinValidator;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.hardware.external.CardIssuer;
import com.thelocalmarketplace.software.SelfCheckoutStationLogic;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.funds.FundsListener;
import com.thelocalmarketplace.software.funds.SupportedCardIssuers;

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
    private Funds fund;
    private CoinValidator validator;
    private BigDecimal value;
    private BigDecimal price;
	private Session session;
	private Session session2;
	private Session session3;
	private HashMap <String, CardIssuer> bankList;
	private static SupportedCardIssuers supportedCards;
	private ArrayList <String> supportedCardsNames;
	private CardIssuer ci1;
	private CardIssuer ci2;
	private CardIssuer ci3;
	private CardIssuer ci4;
	private Card disCard;
	private Card viva;
	private Card cdnDep;
	private Card debit;
	
    
	@Before
	public void setup() {
		AbstractSelfCheckoutStation.resetConfigurationToDefaults();
		
		scs = new SelfCheckoutStationBronze();
		scs.plugIn(PowerGrid.instance());
		scs.turnOn();
		session = new Session();
		SelfCheckoutStationLogic.installOn(scs, session);

		scss = new SelfCheckoutStationSilver();
		scss.plugIn(PowerGrid.instance());
		scss.turnOn();
		session2 = new Session();
		SelfCheckoutStationLogic.installOn(scss, session2);

		scsg = new SelfCheckoutStationGold();
		scsg.plugIn(PowerGrid.instance());
		scsg.turnOn();
		session3 = new Session();
		SelfCheckoutStationLogic.installOn(scsg, session3);  
		
		for(SupportedCardIssuers supportedCards : SupportedCardIssuers.values()) {
			supportedCardsNames.add(supportedCards.getIssuer());
		}
		
		CardIssuer ci1 = new CardIssuer(supportedCardsNames.get(0), 0);
		CardIssuer ci2 = new CardIssuer(supportedCardsNames.get(1), 5);
		CardIssuer ci3 = new CardIssuer(supportedCardsNames.get(2), 1);
		CardIssuer ci4 = new CardIssuer(supportedCardsNames.get(3), 2);
		
		Card disCard = new Card(supportedCardsNames.get(0), "5299334598001547", "Brandon Chan", "666");
		Card viva = new Card(supportedCardsNames.get(1), "4504389022574000", "Dorris Giles", "343");
		Card cdnDep = new Card(supportedCardsNames.get(2), "1111111111111111", "Not A Real Person", "420");
		Card debit = new Card(supportedCardsNames.get(3), "5160617843321186", "Brent ", "911");
		
		ci1.addCardData(disCard.kind, disCard.cardholder, null, disCard.cvv, 10000);
		ci2.addCardData(viva.kind, viva.cardholder, null, viva.cvv, 7500);
		ci3.addCardData("0", cdnDep.cardholder, null, cdnDep.cvv, 0);
		ci4.addCardData(debit.kind, debit.cardholder, null, debit.cvv, 2);
	}
	
	@Test
	public void testInvalidCardNumber() throws IOException {
		scs.cardReader.swipe(cdnDep);
		// The card numbers do not match and will decline a card if the card is blocked
		// authorizeHold should return -1 
		// How do we effectively call authorize hold
	}
	
	@Test
	public void testBlockedCard() {
		// This will decline a card if the card is blocked
		// authorizeHold should return -1 
	}
	
	@Test
	public void testHoldCountDecline() {
		// This will decline a card if it has run out of available holds
		// authorizeHold should return -1 
	}
	
	@Test
	public void testAvailableBalanceDecline() {
		// This will decline a card if there is insufficient available balance 
		// postTransaction should return false 
	}

	@Test
	public void testSuccessfulPostingTransaction() {
		// This will post a successful charge on the given card
		// postTransaction should return true 
	}
	
    
}
