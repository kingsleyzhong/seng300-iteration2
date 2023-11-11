package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.coin.Coin;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStation;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.SelfCheckoutStationLogic;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.weight.Weight;

import powerutility.PowerGrid;

/** Unit test for the integration of software for selfCheckoutStation with corresponding hardware
 *  Contains tests for:
 *  	Start session
 *  	Scan item
 *  	Pay by coin
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

public class SelfCheckoutStationSystemTest {
	private SelfCheckoutStation scs;
	private Session session;
	private BarcodedProduct product;
	private Barcode barcode;
	private BarcodedItem item;
	private BarcodedItem item2;
	private Coin coin;
	
	@Before
	public void setup() {
		scs = new SelfCheckoutStation();
		scs.plugIn(PowerGrid.instance());
		scs.turnOn();
		session = new Session();
		SelfCheckoutStationLogic.installOn(scs, session);
		
		// Populate database
		barcode = new Barcode(new Numeral[] {Numeral.valueOf((byte) 1)});
		product = new BarcodedProduct(barcode, "Some product", 10, 20.0);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product);
		
		item = new BarcodedItem(barcode, new Mass(20.0));
		item2 = new BarcodedItem(barcode, new Mass(20.0));
		
		Coin.DEFAULT_CURRENCY = Currency.getInstance(Locale.CANADA);
		coin = new Coin(BigDecimal.ONE);
	}
	
	// Tests for start Session requirement use case
	
	@Test
	public void testInitialConfiguration() {
		assertFalse("Session should be off", session.isOn());
	}
	
	@Test
	public void testStartSession() {
		session.start();
		assertTrue("session has been started", session.isOn());
	}
	
	// Tests for scan an Item requirement use case
	
	@Test
	public void testScanAnItem() {
		session.start();
		scs.scanner.scan(item);
		HashMap<BarcodedProduct, Integer> items = session.getBarcodedItems();
		assertTrue("The barcoded product associated with the barcode has been added", items.containsKey(product));
	}
	
	@Test
	public void testScanAnItemFunds() {
		session.start();
		scs.scanner.scan(item);
		Funds funds = session.getFunds();
		BigDecimal expected = new BigDecimal(10);
		BigDecimal actual = funds.getItemsPrice();
		assertEquals("Funds has correct amount", expected, actual);
	}
	
	@Test
	public void testScanAnItemWeight() {
		session.start();
		scs.scanner.scan(item);
		Weight weight = session.getWeight();
		Mass expected = new Mass(20.0);
		Mass actual = weight.getExpectedWeight();
		assertEquals("Weight has correct amount", expected, actual);
	}
	
	// Tests for pay via coin
	
	@Test(expected = InvalidActionException.class)
	public void enterPayWhenCartEmpty() {
		session.start();
		session.pay();
	}
	
	@Test(expected = InvalidActionException.class)
	public void addCoinWhenNotInPay() throws DisabledException, CashOverloadException {
		session.start();
		scs.coinSlot.receive(coin);
	}
	
	@Test
	public void payForItemViaCoin() throws DisabledException, CashOverloadException {
		session.start();
		scs.scanner.scan(item);
		scs.baggingArea.addAnItem(item);
		session.pay();
		for (int i = 0; i < 10; i++) {
			scs.coinSlot.receive(coin);
		}
		Funds funds = session.getFunds();
		assertEquals("Session is fully paid for", BigDecimal.ZERO, funds.getAmountDue());
		assertTrue("Session has been notified of full payment", session.hasPaid());
	}
	
	// Tests for weight Discrepancy
	
	@Test
	public void testDiscrepancy() {
		session.start();
		scs.scanner.scan(item);
		assertTrue("Session is frozen upon discrepancy", session.isFrozen());
	}
	
	@Test(expected = InvalidActionException.class)
	public void testAddItemWhenDiscrepancy() {
		session.start();
		scs.scanner.scan(item);
		scs.scanner.scan(item2);
	}
	
	@Test(expected = InvalidActionException.class)
	public void testPayWhenDiscrepancy() throws DisabledException, CashOverloadException {
		session.start();
		scs.scanner.scan(item);
		scs.baggingArea.addAnItem(item);
		session.pay();
		scs.baggingArea.addAnItem(item2);
		scs.coinSlot.receive(coin);
	}
	
	@Test
	public void testDiscrepancyDuringPay() {
		session.start();
		scs.scanner.scan(item);
		scs.baggingArea.addAnItem(item);
		session.pay();
		scs.baggingArea.addAnItem(item2);
		Funds funds = session.getFunds();
		assertTrue(session.isFrozen());
		assertFalse(funds.isPay());
		scs.baggingArea.removeAnItem(item2);
		assertTrue(session.isFrozen());
		assertTrue(funds.isPay());
	}
}
