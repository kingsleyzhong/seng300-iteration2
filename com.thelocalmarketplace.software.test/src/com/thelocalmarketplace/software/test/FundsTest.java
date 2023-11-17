package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.IllegalDigitException;
import com.tdc.coin.CoinDispenserBronze;
import com.tdc.coin.CoinDispenserGold;
import com.tdc.coin.CoinValidator;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.software.SelfCheckoutStationLogic;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.funds.FundsListener;

import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.PowerGrid;

/**
 * Testing for the Funds class
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
 */

public class FundsTest {
	private SelfCheckoutStationBronze scs;
	private SelfCheckoutStationSilver scss;
	private SelfCheckoutStationGold scsg;
	private Session session;
	private Session sessions;
	private Session sessiong;
	private Funds fund;
	private Funds funds;
	private Funds fundg;
	private CoinValidator validator;
	private CoinValidator validatorSilver;
	private CoinValidator validatorGold;
	private CoinDispenserBronze dispenser;
	private CoinDispenserGold dispenserGold;
	private BigDecimal amountPaid;
	private BigDecimal price;

	@Before
	public void setUp() {
		AbstractSelfCheckoutStation.resetConfigurationToDefaults();
		
		scs = new SelfCheckoutStationBronze();
		scs.plugIn(PowerGrid.instance());
		scs.turnOn();
		fund = new Funds(scs);
		fund.setPay(true);
		validator = scs.coinValidator;
		session = new Session();
		SelfCheckoutStationLogic.installOn(scs, session);

		scss = new SelfCheckoutStationSilver();
		scss.plugIn(PowerGrid.instance());
		scss.turnOn();
		funds = new Funds(scss);
		validatorSilver = scss.coinValidator;
		sessions = new Session();
		SelfCheckoutStationLogic.installOn(scss, sessions);

		scsg = new SelfCheckoutStationGold();
		scsg.plugIn(PowerGrid.instance());
		scs.turnOn();
		fundg = new Funds(scsg);
		validatorGold = scsg.coinValidator;
		sessiong = new Session();
		SelfCheckoutStationLogic.installOn(scsg, sessiong);

		price = BigDecimal.valueOf(5.00);
		amountPaid = BigDecimal.valueOf(0.00);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testNullSelfCheckoutStation()
	{
		fund = new Funds(null);
	}
	
	@Test
	public void testUpdateValidPrice() {
		fund.update(price);
		assertEquals(price, fund.getItemsPrice());
		assertEquals(price, fund.getAmountDue());
	}

	@Test(expected = IllegalDigitException.class)
	public void testUpdateInvalidPriceZero() {
		fund.update(BigDecimal.valueOf(0.00));
	}
	
	@Test(expected = IllegalDigitException.class)
	public void testUpdateInvalidPriceNegative() {
		fund.update(BigDecimal.valueOf(-1.00));
	}

	@Test
	public void testTurnOnPay() {
		fund.setPay(true);
		assertTrue(fund.isPay());
	}
	
	@Test
	public void testTurnOffPay() {
		fund.setPay(false);
		assertFalse(fund.isPay());
	}

	@Test
	public void testAmountPaidFull() {
//		FundListenerStub stub = new FundListenerStub();
//		fund.register(stub);
//		fund.update(price);
//		amountPaid = BigDecimal.valueOf(5.00); 
//		fund.new InnerListener().validCoinDetected(validator, amountPaid);
//		assertTrue("Paid event called", stub.getEvents().contains("Paid"));
	}
	
	@Test
	public void testAmountPaidPartial() {
		
	}
	
	@Test
	public void testReturnInsufficientChange() {
		
	}
	
	@Test
	public void testReturnChange() {
		
	}

	@Test(expected = SimulationException.class)
	public void testRegisterInvalidListener() {
		fund.register(null);
	}

	@Test
	public void testUnregisterListener() {
//		FundListenerStub stub = new FundListenerStub();
//		fund.register(stub);
//		fund.deregister(stub);
//		fund.update(price);
//		value = new BigDecimal(5);
//		fund.new InnerListener().validCoinDetected(validator, value);
//		assertFalse("Paid event called", stub.getEvents().contains("Paid"));
	}

	@Test
	public void testUnregisterAllListeners() {
//		FundListenerStub stub = new FundListenerStub();
//		FundListenerStub stub2 = new FundListenerStub();
//		fund.register(stub);
//		fund.register(stub2);
//		fund.deregisterAll();
//		fund.update(price);
//		value = new BigDecimal(5);
//		fund.new InnerListener().validCoinDetected(validator, value);
//		assertFalse("Paid event called", stub.getEvents().contains("Paid"));
//		assertFalse("Paid event called", stub2.getEvents().contains("Paid"));
	}

	@Test
	public void testEnable() {
		scs.coinValidator.disable();
		scs.coinValidator.enable();
		scs.coinValidator.disactivate();
		scs.coinValidator.activate();
	}

	class FundListenerStub implements FundsListener {
		ArrayList<String> events;

		public FundListenerStub() {
			events = new ArrayList<String>();
		}

		@Override
		public void notifyPaid() {
			events.add("Paid");

		}

		public ArrayList<String> getEvents() {
			return events;
		}

	}
}
