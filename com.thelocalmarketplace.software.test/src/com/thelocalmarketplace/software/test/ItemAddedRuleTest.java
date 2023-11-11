package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
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
 * 		
 */

public class ItemAddedRuleTest {
	private SelfCheckoutStationBronze selfCheckoutStation;
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
        selfCheckoutStation = new SelfCheckoutStationBronze();
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
    
    @Test
    public void testAddItemInDatabase() {
        // Set up the session with barcoded items, funds, and weights
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.start();
        barcode = new Barcode(new Numeral[] {Numeral.one, Numeral.two, Numeral.three});
        product = new BarcodedProduct(barcode, "Product1", 10, 100.0);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product); // Add a product to the database

        // Simulate a barcode scan
        itemAddedRule.new innerListener().aBarcodeHasBeenScanned(selfCheckoutStation.mainScanner, barcode);

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
        itemAddedRule.new innerListener().aBarcodeHasBeenScanned(selfCheckoutStation.mainScanner, barcodeNotInDatabase);

        // Check that the product was added
        HashMap<BarcodedProduct, Integer> productList = session.getBarcodedItems();
        
        //BROKEN TEST confusion.
        assertFalse(productList.containsKey(barcodeNotInDatabase));
    }
    
    //BROKEN TESTS
    
    //@Test (expected = InvalidArgumentSimulationException.class)
    //public void testAddItemNullSCS() {
    //    itemAddedRule = new ItemAddedRule(null, session);
    //}
    
    //@Test(expected = InvalidActionException.class)
    //public void testSessionFrozen() {
    //	session = new frozenSessionStub();		//session is frozen
    //	itemAddedRule.new innerListener().aBarcodeHasBeenScanned(selfCheckoutStation.scanner, barcode);
    //}
    
    //@Test(expected = InvalidActionException.class)
    //public void testSessionIsOff() {
    //	session = new isOnStub();			//session is off
    //	itemAddedRule.new innerListener().aBarcodeHasBeenScanned(selfCheckoutStation.scanner, barcode);
    //}
    
    // Can't even tell you what this genius idea was... smh
   /*@Test
   public void forCoverage() {
	   selfCheckoutStation.plugIn(PowerGrid.instance());
	   selfCheckoutStation.turnOn();
	   selfCheckoutStation.mainScanner.disable();
	   selfCheckoutStation.mainScanner.enable();
	   selfCheckoutStation.mainScanner.turnOn();
	   selfCheckoutStation.mainScanner.turnOff();
   }*/
}
