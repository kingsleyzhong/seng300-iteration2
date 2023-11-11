package com.thelocalmarketplace.software.test;

import static org.junit.Assert.*;
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
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.weight.Weight;

import powerutility.PowerGrid;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;

/**
 * Unit Test class for Session and interaction with surrounding classes Weight and Funds
 * 	Tests for turning session on, turning session off
 * 	Adding a single item, adding multiple duplicate items, adding different items
 * 	Session freezing when discrepancy occurs and un-freezing when discrepancy resolved
 * See tests for Weight, Funds to ensure no bugs.
 * 
 * Project iteration group members:
 * 		Ayman Momin 		: 30192494
 * 		Emily Kiddle 		: 30122331
 * 		Fardin Rahman Sami 	: 30172916
 * 		Kaylee Xiao 		: 30173778
 * 		Tamanna Kaur 		: 30170920
 * 		YiPing Zhang 		: 30127823
 */
public class SessionTest {
	private SelfCheckoutStationBronze scs = new SelfCheckoutStationBronze();
	private SelfCheckoutStationSilver scss = new SelfCheckoutStationSilver();
	private SelfCheckoutStationGold scsg = new SelfCheckoutStationGold();
	
    private Session session;
    private BarcodedProduct product;
    private BarcodedProduct product2;
    byte num;
    private Numeral numeral;
    private Numeral[] digits;
    private Barcode barcode;
    private Barcode barcode2;
    
    private Funds funds;
    private Weight weight;
    private Weight weightSilver;
    private Weight weightGold;

    @Before
    public void setUp() {
        session = new Session();
        num = 1;
        numeral = Numeral.valueOf(num);
        digits = new Numeral[] {numeral, numeral, numeral};
        barcode = new Barcode(digits);
        barcode2 = new Barcode(new Numeral[] {numeral}) ;
        product = new BarcodedProduct(barcode, "Sample Product", 10, 100.0);
        product2 = new BarcodedProduct(barcode2, "Sample Product 2", 15, 20.0);
        funds = new Funds(scs);
        weight = new Weight(scs);
    }

    @Test
    public void testSessionInitialization() {
        assertFalse(session.isOn());
        assertFalse(session.isFrozen());
    }

    @Test
    public void testStartSession() {
        session.start();
        assertTrue(session.isOn());
    }

    @Test
    public void testCancelSession() {
        session.start();
        session.cancel();
        assertFalse(session.isOn());
    }

    @Test
    public void testAddItem() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        HashMap<BarcodedProduct, Integer> list = session.getBarcodedItems();
        assertTrue("Contains product in list", list.containsKey(product));
        Integer expected = 1;
        assertEquals("Has 1", expected, list.get(product));
    }

    @Test
    public void testAddItemQuantity() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        // Add multiple quantities of the same product
        session.addItem(product);
        session.addItem(product);
        HashMap<BarcodedProduct, Integer> list = session.getBarcodedItems();
        assertTrue("Contains product in list", list.containsKey(product));
        Integer expected = 2;
        assertEquals("Has 2 products", expected, list.get(product));
    }
    
    @Test
    public void addTwoDifItems() {
    	session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        session.addItem(product2);
        HashMap<BarcodedProduct, Integer> list = session.getBarcodedItems();
        Integer expected = 1;
        assertTrue("Contains product in list", list.containsKey(product));
        assertEquals("Contains 1 product", expected, list.get(product));
        assertTrue("Contains product2 in list", list.containsKey(product2));
        assertEquals("Contains 1 product2", expected, list.get(product2));
    }
    
    @Test
    public void addItemFundUpdate() {
    	session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        Funds fund = session.getFunds();
        BigDecimal actual = fund.getItemsPrice();
        BigDecimal expected = new BigDecimal(10);
        assertEquals("Value of 10 added", expected, actual);
    }
    
    @Test
    public void addTwoItemsFundUpdate() {
    	session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        session.addItem(product2);
        Funds fund = session.getFunds();
        BigDecimal actual = fund.getItemsPrice();
        BigDecimal expected = new BigDecimal(25);
        assertEquals("Value of 25 added", expected, actual);
    }
    
    @Test
    public void addItemWeightUpdate() {
    	session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(100.0);
        assertEquals("Mass is 100.0", expected, actual);
    }
    
    @Test
    public void addTwoItemsWeightUpdate() {
    	session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        session.addItem(product2);
        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(120.0);
        assertEquals("Mass is 120.0", expected, actual);
    }
    
    @Test
    public void testWeightDiscrepancy() {
    	session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        assertTrue("Discrepancy must have occured", session.isFrozen());
    }
    
    @Test
    public void testWeightDiscrepancyResolved() {
    	session.start();
    	session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();
        scs.baggingArea.addAnItem(new BarcodedItem(barcode, new Mass(100.0)));
        assertFalse("Discrepancy resolved", session.isFrozen());
    }
    
    @Test(expected = InvalidActionException.class)
    public void payEmpty() {
    	session.start();
    	session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
    	session.pay();
    }
    
    @Test
    public void testPaid() throws DisabledException, CashOverloadException {
    	session.start();
    	session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
    	session.addItem(product);
    	scs.plugIn(PowerGrid.instance());
    	scs.turnOn();
    	scs.baggingArea.addAnItem(new BarcodedItem(barcode, new Mass(100.0)));
    	session.pay();
    	assertTrue(session.isPay());
    	Coin.DEFAULT_CURRENCY = Currency.getInstance(Locale.CANADA);
		Coin coin = new Coin(BigDecimal.ONE);
    	for(int i = 0; i<10; i++) {
    		scs.coinSlot.receive(coin);
    	}
    	assertTrue(session.hasPaid());
    }
}
