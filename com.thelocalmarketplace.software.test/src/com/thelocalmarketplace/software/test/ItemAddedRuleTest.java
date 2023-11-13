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
import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import powerutility.PowerGrid;

/**
 * Testing for the AddItemRule class
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

public class ItemAddedRuleTest {
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

    @Test
    public void testAddItemInDatabaseBronze() {
        session.setup(new HashMap<BarcodedProduct, Integer>(), fundsBronze, weightBronze);
        session.start();
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product); // Add a product to the database
        scsb.mainScanner.scan(item);

        // Check that the product was added
        HashMap<BarcodedProduct, Integer> productList = session.getBarcodedItems();
        assertTrue(productList.containsKey(product));
    }

    @Test
    public void testAddItemInDatabaseSilver() {
        session2.setup(new HashMap<BarcodedProduct, Integer>(), fundsSilver, weightSilver);
        session2.start();
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product); // Add a product to the database
        scss.mainScanner.scan(item);

        // Check that the product was added
        HashMap<BarcodedProduct, Integer> productList = session2.getBarcodedItems();
        assertTrue(productList.containsKey(product));
    }

    @Test
    public void testAddItemInDatabaseGold() {
        session3.setup(new HashMap<BarcodedProduct, Integer>(), fundsGold, weightGold);
        session3.start();
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product); // Add a product to the database
        scsg.mainScanner.scan(item);

        // Check that the product was added
        HashMap<BarcodedProduct, Integer> productList = session3.getBarcodedItems();
        assertTrue(productList.containsKey(product));
    }

    @Test(expected = InvalidArgumentSimulationException.class)
    public void testAddItemNotInDatabaseBronze() {
        session.setup(new HashMap<BarcodedProduct, Integer>(), fundsBronze, weightBronze);
        session.start();
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product); // Add a product to the database

        Barcode barcodeNotInDatabase = new Barcode(new Numeral[] { Numeral.five, Numeral.five, Numeral.eight });
        BarcodedItem itemNotInDatabase = new BarcodedItem(barcodeNotInDatabase, new Mass(100.0));

        scsb.mainScanner.scan(itemNotInDatabase);
    }

    @Test(expected = InvalidArgumentSimulationException.class)
    public void testAddItemNotInDatabaseSilver() {
        session2.setup(new HashMap<BarcodedProduct, Integer>(), fundsSilver, weightSilver);
        session2.start();
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product); // Add a product to the database

        Barcode barcodeNotInDatabase = new Barcode(new Numeral[] { Numeral.five, Numeral.five, Numeral.eight });
        BarcodedItem itemNotInDatabase = new BarcodedItem(barcodeNotInDatabase, new Mass(100.0));

        scss.mainScanner.scan(itemNotInDatabase);
    }

    @Test(expected = InvalidArgumentSimulationException.class)
    public void testAddItemNotInDatabaseGold() {
        session3.setup(new HashMap<BarcodedProduct, Integer>(), fundsGold, weightGold);
        session3.start();
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product); // Add a product to the database

        Barcode barcodeNotInDatabase = new Barcode(new Numeral[] { Numeral.five, Numeral.five, Numeral.eight });
        BarcodedItem itemNotInDatabase = new BarcodedItem(barcodeNotInDatabase, new Mass(100.0));

        scsg.mainScanner.scan(itemNotInDatabase);
    }

    // BROKEN TESTS

    // @Test (expected = InvalidArgumentSimulationException.class)
    // public void testAddItemNullSCS() {
    // itemAddedRule = new ItemAddedRule(null, session);
    // }

    // @Test(expected = InvalidActionException.class)
    // public void testSessionFrozen() {
    // session = new frozenSessionStub(); //session is frozen
    // itemAddedRule.new
    // innerListener().aBarcodeHasBeenScanned(selfCheckoutStation.scanner, barcode);
    // }

    // @Test(expected = InvalidActionException.class)
    // public void testSessionIsOff() {
    // session = new isOnStub(); //session is off
    // itemAddedRule.new
    // innerListener().aBarcodeHasBeenScanned(selfCheckoutStation.scanner, barcode);
    // }

    // Can't even tell you what this genius idea was... smh
    /*
     * @Test
     * public void forCoverage() {
     * selfCheckoutStation.plugIn(PowerGrid.instance());
     * selfCheckoutStation.turnOn();
     * selfCheckoutStation.mainScanner.disable();
     * selfCheckoutStation.mainScanner.enable();
     * selfCheckoutStation.mainScanner.turnOn();
     * selfCheckoutStation.mainScanner.turnOff();
     * }
     */
}
