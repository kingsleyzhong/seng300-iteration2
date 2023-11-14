package com.thelocalmarketplace.software.test;

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
    private SelfCheckoutStationBronze scsb;
    private SelfCheckoutStationSilver scss;
    private SelfCheckoutStationGold scsg;

    private Session session;
    private Session session2;
    private Session session3;

    private BarcodedProduct product;
    private Barcode barcode;
    private BarcodedItem item;

    private Funds fundsBronze;
    private Funds fundsSilver;
    private Funds fundsGold;

    private Weight weightBronze;
    private Weight weightSilver;
    private Weight weightGold;

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
        session.setup(new HashMap<BarcodedProduct, Integer>(), fundsBronze, weightBronze);
        session.start();
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product); // Add a product to the database
        scsb.mainScanner.scan(item);

        // Check that the product was added
        HashMap<BarcodedProduct, Integer> productList = session.getBarcodedItems();
        assertTrue(productList.containsKey(product));
        
        // Check that the product has been removed
        session.removeItem(item);
        
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
