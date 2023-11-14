package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.rules.ItemAddedRule;
import com.thelocalmarketplace.software.weight.Weight;

import powerutility.PowerGrid;

public class RemoveItemTests {
	
//    private SelfCheckoutStationBronze scsb;
//    private SelfCheckoutStationSilver scss;
//    private SelfCheckoutStationGold scsg;
//
//    private Session session;
//    private Session session2;
//    private Session session3;
//
//    private BarcodedProduct product;
//    private Barcode barcode;
//    private BarcodedItem item;
//
//    private Funds fundsBronze;
//    private Funds fundsSilver;
//    private Funds fundsGold;
//
//    private Weight weightBronze;
//    private Weight weightSilver;
//    private Weight weightGold;
	
    private SelfCheckoutStationBronze scs;// = new SelfCheckoutStationBronze();
    private SelfCheckoutStationSilver scss; // = new SelfCheckoutStationSilver();
    private SelfCheckoutStationGold scsg;// = new SelfCheckoutStationGold();

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
    	AbstractSelfCheckoutStation.resetConfigurationToDefaults();
    	scs = new SelfCheckoutStationBronze();
    	scss = new SelfCheckoutStationSilver();
    	scsg = new SelfCheckoutStationGold();
    	
    	
        session = new Session();
        num = 1;
        numeral = Numeral.valueOf(num);
        digits = new Numeral[] { numeral, numeral, numeral };
        barcode = new Barcode(digits);
        barcode2 = new Barcode(new Numeral[] { numeral });
        product = new BarcodedProduct(barcode, "Sample Product", 10, 100.0);
        product2 = new BarcodedProduct(barcode2, "Sample Product 2", 15, 20.0);
        funds = new Funds(scs);
        weight = new Weight(scs);
    }

    
	
	//Successfully remove item (update weight and price) Bronze, Silver, Gold
    @Test
    public void testRemoveItemInDatabaseBronze() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        HashMap<BarcodedProduct, Integer> list = session.getBarcodedItems();
        session.addItem(product);
        // Check that the product was added
        HashMap<BarcodedProduct, Integer> productList = session.getBarcodedItems();
        assertTrue(productList.containsKey(product));
        
        // Check that the product has been removed
        session.removeItem(product);
        assertFalse(productList.containsKey(product));
    }
    
    @Test
    public void testRemoveItemInDatabaseSilver() {
    	
    }
	
    
    @Test
    public void testRemoveItemInDatabaseGold() {
    	
    	
    }
    
	//remove item that hasn't been scanned Bronze, Silver, Gold
    @Test
    public void testRemoveItemNotInDatabaseBronze() {
    	
    }
    
    @Test
    public void testRemoveItemNotInDatabaseSilver() {
    	
    }
    
    @Test
    public void testRemoveItemNotInDatabaseGold() {
    	
    }
    
    
	//remove duplicate item (update weight and price) Bronze, Silver, Gold
    @Test
    public void testRemoveDupliacateItemInDatabaseBronze() {
    	
    }
    
    @Test
    public void testRemoveDupliacateItemInDatabaseSilver() {
    	
    }
    
    @Test
    public void testRemoveDupliacateItemInDatabaseGold() {
    	
    }
	
	
	//remove item twice Bronze, Silver, Gold
    @Test
    public void testRemoveSameItemTwiceBronze() {
    	
    }
    
    @Test
    public void testRemoveSameItemTwiceSilver() {
    	
    }
    
    @Test
    public void testRemoveSameItemTwiceGold() {
    	
    }
    
    
    
    
    
	
	
	
}
