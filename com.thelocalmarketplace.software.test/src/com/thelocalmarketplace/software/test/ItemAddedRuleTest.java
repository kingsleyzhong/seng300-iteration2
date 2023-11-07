package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStation;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.rules.ItemAddedRule;
import com.thelocalmarketplace.software.weight.Weight;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import powerutility.PowerGrid;

/**
 * Testing for the AddItemRule class
 * 
 * Project iteration group members:
 * 		Ayman Momin 		: 30192494
 * 		Emily Kiddle 		: 30122331
 * 		Fardin Rahman Sami 	: 30172916
 * 		Kaylee Xiao 		: 30173778
 * 		Tamanna Kaur 		: 30170920
 * 		YiPing Zhang 		: 30127823
 */

public class ItemAddedRuleTest {
	private SelfCheckoutStation selfCheckoutStation;
    private Session session;
    private ItemAddedRule itemAddedRule;
    private BarcodedProduct product;
    byte num;
    private Numeral numeral;
    private Numeral[] digits;
    private Barcode barcode;
    private Funds funds;
    private Weight weight;
    
    @Before
    public void setup() {
        selfCheckoutStation = new SelfCheckoutStation();
        session = new Session();
        itemAddedRule = new ItemAddedRule(selfCheckoutStation, session);
        num = 1;
        numeral = Numeral.valueOf(num);
        digits = new Numeral[] {numeral, numeral, numeral};
        barcode = new Barcode(digits);
        product = new BarcodedProduct(barcode, "Product 1", 10, 100.0);
        weight = new Weight(selfCheckoutStation);
        funds = new Funds(selfCheckoutStation);
        
    }
    
    public class frozenSessionStub extends Session {
        @Override
        public boolean isFrozen() {
            return true;
        }
    }
    
    public class isOnStub extends Session {
        @Override
        public boolean isOn() {
            return false;
        }
    }
    
    @Test
    public void testAddItemInDatabase() {
        // Set up the session with barcoded items, funds, and weights
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.start();
        barcode = new Barcode(new Numeral[] {Numeral.one, Numeral.two, Numeral.three});
        product = new BarcodedProduct(barcode, "Product1", 10, 100.0);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product); // Add a product to the database

        // Simulate a barcode scan
        itemAddedRule.new innerListener().aBarcodeHasBeenScanned(selfCheckoutStation.scanner, barcode);

        // Check that the product was added
        HashMap<BarcodedProduct, Integer> productList = session.getBarcodedItems();
        assertTrue(productList.containsKey(product));
    }
    
    @Test(expected = InvalidArgumentSimulationException.class)
    public void testAddItemNotInDatabase() {
        // Set up the session with barcoded items, funds, and weights
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.start();
        barcode = new Barcode(new Numeral[] {Numeral.one, Numeral.two, Numeral.three});
        product = new BarcodedProduct(barcode, "Product1", 10, 100.0);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product); // Add a product to the database
        
        Barcode barcodeNotInDatabase = new Barcode(new Numeral[] {Numeral.five, Numeral.five, Numeral.eight})	;

        // Simulate a barcode scan
        itemAddedRule.new innerListener().aBarcodeHasBeenScanned(selfCheckoutStation.scanner, barcodeNotInDatabase);

        // Check that the product was added
        HashMap<BarcodedProduct, Integer> productList = session.getBarcodedItems();
        assertFalse(productList.containsKey(barcodeNotInDatabase));
    }
    
    @Test (expected = InvalidArgumentSimulationException.class)
    public void testAddItemNullSCS() {
        itemAddedRule = new ItemAddedRule(null, session);
    }
    
    @Test(expected = InvalidActionException.class)
    public void testSessionFrozen() {
    	session = new frozenSessionStub();		//session is frozen
    	itemAddedRule.new innerListener().aBarcodeHasBeenScanned(selfCheckoutStation.scanner, barcode);
    }
    
    @Test(expected = InvalidActionException.class)
    public void testSessionIsOff() {
    	session = new isOnStub();			//session is off
    	itemAddedRule.new innerListener().aBarcodeHasBeenScanned(selfCheckoutStation.scanner, barcode);
    }
    
   @Test
   public void forCoverage() {
	   selfCheckoutStation.plugIn(PowerGrid.instance());
	   selfCheckoutStation.turnOn();
	   selfCheckoutStation.scanner.disable();
	   selfCheckoutStation.scanner.enable();
	   selfCheckoutStation.scanner.turnOn();
	   selfCheckoutStation.scanner.turnOff();
   }
}
