package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;
import com.thelocalmarketplace.software.exceptions.ProductNotFoundException;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.rules.ItemAddedRule;
import com.thelocalmarketplace.software.weight.Weight;

import powerutility.PowerGrid;

/**
 * Unit Test class for RemoveItemMethod and interaction with surrounding classes Weight
 * and Funds
 * Tests for turning session on, turning session off
 * Adding a single item, adding multiple duplicate items, adding different items
 * Session freezing when discrepancy occurs and un-freezing when discrepancy
 * resolved
 * See tests for Weight, Funds to ensure no bugs.
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

public class RemoveItemTests {
	
    private SelfCheckoutStationBronze scsb;
    private SelfCheckoutStationSilver scss;
    private SelfCheckoutStationGold scsg;

    private Session session;
    private Session session2;
    private Session session3;

    private BarcodedProduct product;
    private BarcodedProduct product2;
    private Barcode barcode;
    private BarcodedItem item;

    private Funds fundsBronze;
    private Funds fundsSilver;
    private Funds fundsGold;

    private Weight weightBronze;
    private Weight weightSilver;
    private Weight weightGold;

    //sets up the test cases
    @Before
    public void setup() {
    	AbstractSelfCheckoutStation.resetConfigurationToDefaults();
    	
        session = new Session();
        session2 = new Session();
        session3 = new Session();
        scsb = new SelfCheckoutStationBronze();
        scsb.plugIn(PowerGrid.instance());
        scsb.turnOn();
        scss = new SelfCheckoutStationSilver();
        scss.plugIn(PowerGrid.instance());
        scss.turnOn();
        scsg = new SelfCheckoutStationGold();
        scsg.plugIn(PowerGrid.instance());
        scsg.turnOn();
        new ItemAddedRule(scsb, session);
        new ItemAddedRule(scss, session2);
        new ItemAddedRule(scsg, session3);

        barcode = new Barcode(new Numeral[] { Numeral.valueOf((byte) 1) });
        product = new BarcodedProduct(barcode, "Product 1", 10, 100.0);
        product2 = new BarcodedProduct(barcode, "Product 2", 10, 120.0);
        
        
        item = new BarcodedItem(barcode, new Mass(100.0));

        weightBronze = new Weight(scsb);
        fundsBronze = new Funds(scsb);
        weightSilver = new Weight(scss);
        fundsSilver = new Funds(scss);
        weightGold = new Weight(scsg);
        fundsGold = new Funds(scsg);
    } 

   
	//Successfully remove item (update weight and price) Bronze, Silver, Gold
    @Test
    public void testRemoveItemInDatabaseBronze() {
        //start the session
    	session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), fundsBronze, weightBronze);        
        //add item
        session.addItem(product);
        
        //Check that the product was added
        HashMap<BarcodedProduct, Integer> productList = session.getBarcodedItems();
        assertTrue(productList.containsKey(product));
        
        //Remove item
        session.removeItem(product);
        
        //check that the weights and values 
        assertFalse(productList.containsKey(product));
        assertEquals(BigDecimal.ZERO, session.getFunds().getAmountDue());
        
        //Initializing weights to test for adjustment
        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(0);
        assertEquals(expected, actual);
         
    }
    
    @Test
    public void testRemoveItemInDatabaseSilver() {
        //start the session
    	session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), fundsSilver, weightSilver);        
        //add item
        session.addItem(product);
        
        //Check that the product was added
        HashMap<BarcodedProduct, Integer> productList = session.getBarcodedItems();
        assertTrue(productList.containsKey(product));
        
        //Remove item
        session.removeItem(product);        
        
        
        //check that the weights and values 
        assertFalse(productList.containsKey(product));
        assertEquals(BigDecimal.ZERO, session.getFunds().getAmountDue());
        
        //Initializing weights to test for adjustment
        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(0);
        assertEquals(expected, actual);
         
    }
    
    @Test
    public void testRemoveItemInDatabaseGold() {
        //start the session
    	session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), fundsGold, weightGold);        
        //add item
        session.addItem(product);
        
        //Check that the product was added
        HashMap<BarcodedProduct, Integer> productList = session.getBarcodedItems();
        assertTrue(productList.containsKey(product));
        
        //Remove item
        session.removeItem(product);        
        
        
        //check that the weights and values 
        assertFalse(productList.containsKey(product));
        assertEquals(BigDecimal.ZERO, session.getFunds().getAmountDue());
        
        //Initializing weights to test for adjustment
        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(0);
        assertEquals(expected, actual);  
    	
    }
    
    
	//remove item that hasn't been scanned Bronze, Silver, Gold
    @Test (expected = ProductNotFoundException.class)
    public void testRemoveItemNotInDatabaseBronze() {
        //start the session
    	session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), fundsBronze, weightBronze);        
        
        
        //Remove item 
        session.removeItem(product);
    }
    
    @Test (expected = ProductNotFoundException.class)
    public void testRemoveItemNotInDatabaseSilver() {
        //start the session
    	session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), fundsSilver, weightSilver);        
        
        //Remove item
        session.removeItem(product);

    }
    
    @Test (expected = ProductNotFoundException.class)
    public void testRemoveItemNotInDatabaseGold() {
        //start the session
    	session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), fundsGold, weightGold);
        
        //Remove item
        session.removeItem(product);

    }
    
    
	//remove duplicate item (update weight and price) Bronze, Silver, Gold
    @Test
    public void testRemoveDupliacateItemInDatabaseBronze() {
        //start the session
    	session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), fundsBronze, weightBronze);        
        
        //add item twice
        session.addItem(product);
        session.addItem(product);
        
        //Check that the product was added
        HashMap<BarcodedProduct, Integer> productList = session.getBarcodedItems();
        assertTrue(productList.containsKey(product));
        
        //Remove item once
        session.removeItem(product);        
        
        //check that the weights and values 
        assertTrue(productList.containsKey(product));
        assertEquals(BigDecimal.TEN , session.getFunds().getAmountDue());
        
        //Initializing weights to test for adjustment
        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(100.0);
        assertEquals(expected, actual);
    }
    
    @Test
    public void testRemoveDupliacateItemInDatabaseSilver() {
        //start the session
    	session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), fundsSilver, weightSilver);        
        
        //add item twice
        session.addItem(product);
        session.addItem(product);
        
        //Check that the product was added
        HashMap<BarcodedProduct, Integer> productList = session.getBarcodedItems();
        assertTrue(productList.containsKey(product));
        
        //Remove item once
        session.removeItem(product);        
        
        //check that the weights and values 
        assertTrue(productList.containsKey(product));
        assertEquals(BigDecimal.TEN , session.getFunds().getAmountDue());
        
        //Initializing weights to test for adjustment
        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(100.0);
        assertEquals(expected, actual);
        //assertEquals(session.getWeight(), product.getExpectedWeight());
    	
    }
    
    @Test
    public void testRemoveDupliacateItemInDatabaseGold() {
        //start the session
    	session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), fundsGold, weightGold);        
        
        //add item twice
        session.addItem(product);
        session.addItem(product);
        
        //Check that the product was added
        HashMap<BarcodedProduct, Integer> productList = session.getBarcodedItems();
        assertTrue(productList.containsKey(product));
        
        //Remove item once
        session.removeItem(product);        
        
        //check that the weights and values 
        assertTrue(productList.containsKey(product));
        assertEquals(BigDecimal.TEN , session.getFunds().getAmountDue());
        
        //Initializing weights to test for adjustment
        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(100.0);
        assertEquals(expected, actual);
    }
	
	
	//remove item twice Bronze, Silver, Gold
    @Test (expected = ProductNotFoundException.class)
    public void testRemoveSameItemTwiceBronze() {
    	session.start();
    	session.setup(new HashMap<BarcodedProduct, Integer>(), fundsBronze, weightBronze); 
        session.addItem(product);
        HashMap<BarcodedProduct, Integer> list = session.getBarcodedItems();
        session.removeItem(product);
        session.removeItem(product);
    }
    
    @Test(expected = ProductNotFoundException.class)
    public void testRemoveSameItemTwiceSilver()  {
    	session.start();
    	session.setup(new HashMap<BarcodedProduct, Integer>(), fundsSilver, weightSilver); 
        session.addItem(product);
        HashMap<BarcodedProduct, Integer> list = session.getBarcodedItems();
        session.removeItem(product);
        session.removeItem(product);
    }
    
    @Test (expected = ProductNotFoundException.class)
    public void testRemoveSameItemTwiceGold() {
    	session.start();
    	session.setup(new HashMap<BarcodedProduct, Integer>(), fundsGold, weightGold); 
        session.addItem(product);
        HashMap<BarcodedProduct, Integer> list = session.getBarcodedItems();
        session.removeItem(product);
        session.removeItem(product);
    }
     
    
    //remove item that was not the last added
    @Test
    public void testRemoveItemThatsNotLastAddedbronze() {
        //start the session
    	session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), fundsBronze, weightBronze);        
        
        //add two different items
        session.addItem(product);
        session.addItem(product2);
        
        //Check that the product was added
        HashMap<BarcodedProduct, Integer> productList = session.getBarcodedItems();
        assertTrue(productList.containsKey(product));
        
        //Remove item once
        session.removeItem(product);        
        
        //check that the weights and values 
        assertFalse(productList.containsKey(product));
        assertEquals(BigDecimal.TEN , session.getFunds().getAmountDue());
        
        //Initializing weights to test for adjustment
        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(120.0); 
        assertEquals(expected, actual);
    }
    
    @Test
    public void testRemoveItemThatsNotLastAddedSilver() {
        //start the session
    	session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), fundsSilver, weightSilver);        
        
        //add item twice
        session.addItem(product);
        session.addItem(product2);
        
        //Check that the product was added
        HashMap<BarcodedProduct, Integer> productList = session.getBarcodedItems();
        assertTrue(productList.containsKey(product));
        
        //Remove item once
        session.removeItem(product);        
        
        //check that the weights and values 
        assertFalse(productList.containsKey(product));
        assertEquals(BigDecimal.TEN , session.getFunds().getAmountDue());
        
        //Initializing weights to test for adjustment
        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(120.0); 
        assertEquals(expected, actual);
    	 
    }
     
    @Test
    public void testRemoveItemThatsNotLastAddedGold() {
        //start the session
    	session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), fundsGold, weightGold);        
        
        //add item twice
        session.addItem(product);
        session.addItem(product2);
        
        //Check that the product was added
        HashMap<BarcodedProduct, Integer> productList = session.getBarcodedItems();
        assertTrue(productList.containsKey(product));
        
        //Remove item once
        session.removeItem(product);        
        
        //check that the weights and values 
        assertFalse(productList.containsKey(product));
        assertEquals(BigDecimal.TEN , session.getFunds().getAmountDue());
        
        //Initializing weights to test for adjustment
        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(120.0); 
        assertEquals(expected, actual);
    }
	
}