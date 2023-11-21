package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.IllegalDigitException;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.NoCashAvailableException;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteDispenserBronze;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinDispenserBronze;
import com.tdc.coin.CoinDispenserGold;
import com.tdc.coin.CoinValidator;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.software.SelfCheckoutStationLogic;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;
import com.thelocalmarketplace.software.exceptions.NotEnoughChangeException;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.funds.FundsListener;
import com.thelocalmarketplace.software.funds.PayByCard;
import com.thelocalmarketplace.software.funds.PayByCashController;

import StubClasses.FundsListenerStub;
import StubClasses.SessionFundsSimulationStub;
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

	private PayByCashController cashControllerBronze;
	private PayByCashController cashControllerSilver;
	private PayByCashController cashControllerGold;

	private PayByCard cardControllerBronze;
	private Funds funds;
	private Funds fundss;
	private Funds fundsg;
	private BigDecimal amountPaid;
	private BigDecimal price;

	@Before
	public void setUp() {
		SelfCheckoutStationBronze.resetConfigurationToDefaults();
		SelfCheckoutStationSilver.resetConfigurationToDefaults();
		SelfCheckoutStationGold.resetConfigurationToDefaults();
		
		scs = new SelfCheckoutStationBronze();
		scs.plugIn(PowerGrid.instance());
		scs.turnOn();
		
		scss = new SelfCheckoutStationSilver();
		scss.plugIn(PowerGrid.instance());
		scss.turnOn();
		
		scsg = new SelfCheckoutStationGold();
		scsg.plugIn(PowerGrid.instance());
		scsg.turnOn();
		
		Funds funds = new Funds(scs);
		this.funds = funds;
		funds.setPay(true);
		
		Funds fundss = new Funds(scss);
		this.fundss = fundss;
		fundss.setPay(true);
		
		Funds fundsg = new Funds(scsg);
		this.fundsg = fundsg;
		fundsg.setPay(true);
		
		this.cashControllerBronze = new PayByCashController(scs, funds);
		this.cashControllerSilver = new PayByCashController(scss, fundss);
		this.cashControllerGold = new PayByCashController(scsg, fundsg);

		price = BigDecimal.valueOf(1);
		amountPaid = BigDecimal.valueOf(1);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testNullSelfCheckoutStation() {
		scs = null;
		funds = new Funds(scs);
	}
	
	@Test
	public void testUpdateValidPrice() throws CashOverloadException, NoCashAvailableException, DisabledException {
		funds.update(price);
		assertEquals(price, funds.getItemsPrice());
		assertEquals(price, funds.getAmountDue());
	}

	@Test(expected = IllegalDigitException.class)
	public void testUpdateInvalidPriceZero() throws CashOverloadException, NoCashAvailableException, DisabledException {
		funds.update(BigDecimal.valueOf(0.00));
	}
	
	@Test(expected = IllegalDigitException.class)
	public void testUpdateInvalidPriceNegative() throws CashOverloadException, NoCashAvailableException, DisabledException {
		funds.update(BigDecimal.valueOf(-1.00));
	}
	
	@Test
	public void testRemoveValidItemPrice() throws CashOverloadException, NoCashAvailableException, DisabledException {
		funds.update(price);
		funds.removeItemPrice(price);
		assertEquals(BigDecimal.valueOf(0), funds.getItemsPrice());
		assertEquals(BigDecimal.valueOf(0), funds.getAmountDue());
	}
	
	@Test(expected = IllegalDigitException.class)
	public void testRemoveInvalidItemPriceZero() throws CashOverloadException, NoCashAvailableException, DisabledException {
		funds.update(price);
		funds.removeItemPrice(BigDecimal.valueOf(0.00));
	}
	
	@Test(expected = IllegalDigitException.class)
	public void testRemoveInvalidItemPriceNegative() throws CashOverloadException, NoCashAvailableException, DisabledException {
		funds.update(price);
		funds.removeItemPrice(BigDecimal.valueOf(-1.00));
	}

	@Test
	public void testTurnOnPay() {
		funds.setPay(true);
		assertTrue(funds.isPay());
	}
	
	@Test
	public void testTurnOffPay() {
		funds.setPay(false);
		assertFalse(funds.isPay());
	}

	@Test
	public void testAmountPaidFullCashBronze() throws DisabledException, CashOverloadException, NoCashAvailableException {
		Currency currency = Currency.getInstance(Locale.CANADA);
		Coin coinAmountPaid = new Coin(currency, amountPaid);

		FundsListenerStub stub = new FundsListenerStub();
		funds.register(stub);
		funds.update(price);
		
		SessionFundsSimulationStub sampleSimulation = new SessionFundsSimulationStub();
		sampleSimulation.setPayByCash();
		
		scs.coinSlot.receive(coinAmountPaid);

		assertTrue("Paid event called", stub.getEvents().contains("Paid"));
	}
	
	@Test
	public void testAmountPaidPartialCashBronze() throws DisabledException, CashOverloadException {
		price = BigDecimal.valueOf(2);

		Currency currency = Currency.getInstance(Locale.CANADA);
		Coin coinAmountPaid = new Coin(currency, amountPaid);

		FundsListenerStub stub = new FundsListenerStub();
		funds.register(stub);
		funds.update(price);
		
		SessionFundsSimulationStub sampleSimulation = new SessionFundsSimulationStub();
		sampleSimulation.setPayByCash();
		
		scs.coinSlot.receive(coinAmountPaid);

		assertFalse("Paid event not called", stub.getEvents().contains("Paid"));
	}
	
	@Test
	public void testAmountPaidFullCashSilver() throws DisabledException, CashOverloadException, NoCashAvailableException {
		Currency currency = Currency.getInstance(Locale.CANADA);
		Coin coinAmountPaid = new Coin(currency, amountPaid);

		FundsListenerStub stub = new FundsListenerStub();
		fundss.register(stub);
		fundss.update(price);
		
		SessionFundsSimulationStub sampleSimulation = new SessionFundsSimulationStub();
		sampleSimulation.setPayByCash();
		
		scss.coinSlot.receive(coinAmountPaid);

		assertTrue("Paid event called", stub.getEvents().contains("Paid"));
	}
	
	@Test
	public void testAmountPaidPartialCashSilver() throws DisabledException, CashOverloadException {
		price = BigDecimal.valueOf(2);

		Currency currency = Currency.getInstance(Locale.CANADA);
		Coin coinAmountPaid = new Coin(currency, amountPaid);

		FundsListenerStub stub = new FundsListenerStub();
		fundss.register(stub);
		fundss.update(price);
		
		SessionFundsSimulationStub sampleSimulation = new SessionFundsSimulationStub();
		sampleSimulation.setPayByCash();
		
		scss.coinSlot.receive(coinAmountPaid);

		assertFalse("Paid event not called", stub.getEvents().contains("Paid"));
	}
	
	@Test
	public void testAmountPaidFullCashGold() throws DisabledException, CashOverloadException, NoCashAvailableException {
		Currency currency = Currency.getInstance(Locale.CANADA);
		Coin coinAmountPaid = new Coin(currency, amountPaid);

		FundsListenerStub stub = new FundsListenerStub();
		fundsg.register(stub);
		fundsg.update(price);
		
		SessionFundsSimulationStub sampleSimulation = new SessionFundsSimulationStub();
		sampleSimulation.setPayByCash();
		
		scsg.coinSlot.receive(coinAmountPaid);

		assertTrue("Paid event called", stub.getEvents().contains("Paid"));
	}
	
	@Test
	public void testAmountPaidPartialCashGold() throws DisabledException, CashOverloadException {
		price = BigDecimal.valueOf(2);

		Currency currency = Currency.getInstance(Locale.CANADA);
		Coin coinAmountPaid = new Coin(currency, amountPaid);

		FundsListenerStub stub = new FundsListenerStub();
		fundsg.register(stub);
		fundsg.update(price);
		
		SessionFundsSimulationStub sampleSimulation = new SessionFundsSimulationStub();
		sampleSimulation.setPayByCash();
		
		scsg.coinSlot.receive(coinAmountPaid);

		assertFalse("Paid event not called", stub.getEvents().contains("Paid"));
	}

	@Test(expected = SimulationException.class)
	public void testRegisterInvalidListener() {
		FundsListenerStub stub = null; 
		funds.register(stub);
	}
	
	 @Test(expected = SimulationException.class)
	    public void testDeregisterInvalidListener() {
		FundsListenerStub stub = null; 
		funds.deregister(stub);
	}

	@Test
	public void testUnregisterListener() throws DisabledException, CashOverloadException {
		Currency currency = Currency.getInstance(Locale.CANADA);
		Coin coinAmountPaid = new Coin(currency, amountPaid);

		FundsListenerStub stub = new FundsListenerStub();
		funds.register(stub);
		funds.deregister(stub);
		funds.update(price);
		
		SessionFundsSimulationStub sampleSimulation = new SessionFundsSimulationStub();
		sampleSimulation.setPayByCash();
		
		scs.coinSlot.receive(coinAmountPaid);

		assertFalse("Paid event should not be called", stub.getEvents().contains("Paid"));
	}

	@Test
	public void testUnregisterAllListeners() throws DisabledException, CashOverloadException {
		Currency currency = Currency.getInstance(Locale.CANADA);
		Coin coinAmountPaid = new Coin(currency, amountPaid);

		FundsListenerStub stub = new FundsListenerStub();
		FundsListenerStub stub2 = new FundsListenerStub();

		funds.register(stub);
		funds.register(stub2);

		funds.deregisterAll();
		funds.update(price);
		
		SessionFundsSimulationStub sampleSimulation = new SessionFundsSimulationStub();
		sampleSimulation.setPayByCash();
		
		scs.coinSlot.receive(coinAmountPaid);

		assertFalse("Paid event should not be called", stub.getEvents().contains("Paid"));
		assertFalse("Paid event should not be called", stub2.getEvents().contains("Paid"));
	}

	@Test
	public void testEnableDisable() {
		scs.coinValidator.disable();
		scs.coinValidator.enable();
		scs.coinValidator.disactivate();
		scs.coinValidator.activate();
	}
	
	@Test (expected = NotEnoughChangeException.class)
	public void testNotEnoughChange() throws DisabledException, CashOverloadException {
		
		Currency currency = Currency.getInstance(Locale.CANADA);
		Banknote ones = new Banknote(currency, BigDecimal.ONE);
			
		FundsListenerStub stub = new FundsListenerStub();
		
		SessionFundsSimulationStub sampleSimulation = new SessionFundsSimulationStub();
		sampleSimulation.setPayByCash();
		
		funds.update(BigDecimal.valueOf(1));
		
		scs.banknoteInput.receive(ones);
		scs.banknoteInput.receive(ones);
				
	}
}