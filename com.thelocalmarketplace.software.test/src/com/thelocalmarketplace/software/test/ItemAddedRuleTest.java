package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodeScannerListener;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.receipt.PrintReceipt;
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
    
    // Code added
    private PrintReceipt receiptBronze;
    private PrintReceipt receiptSilver;
    private PrintReceipt receiptGold;
    
    private ScannerListenerStub listener;

    @Before
    public void setup() {
        session = new Session();
        session2 = new Session();
        session3 = new Session();
        
        PowerGrid.engageUninterruptiblePowerSource();
        
        AbstractSelfCheckoutStation.resetConfigurationToDefaults();
        
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
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product); // Add a product to the database

        weightBronze = new Weight(scsb);
        fundsBronze = new Funds(scsb);
        weightSilver = new Weight(scss);
        fundsSilver = new Funds(scss);
        weightGold = new Weight(scsg);
        fundsGold = new Funds(scsg);
        
        // Code added
        receiptBronze = new PrintReceipt(scsb);
        receiptSilver = new PrintReceipt(scsb);
        receiptGold = new PrintReceipt(scsb);
        
        session.setup(new HashMap<BarcodedProduct, Integer>(), fundsBronze, weightBronze, receiptBronze);
        session2.setup(new HashMap<BarcodedProduct, Integer>(), fundsSilver, weightSilver, receiptSilver);
        session3.setup(new HashMap<BarcodedProduct, Integer>(), fundsGold, weightGold, receiptGold);
        
        listener = new ScannerListenerStub();
        
        scsb.mainScanner.register(listener);
        scsb.handheldScanner.register(listener);
        scss.mainScanner.register(listener);
        scss.handheldScanner.register(listener);
        scsg.mainScanner.register(listener);
        scsg.handheldScanner.register(listener);
    }

    @Test(expected = InvalidArgumentSimulationException.class)
    public void testNullSCS() {
    	AbstractSelfCheckoutStation scsNull = null;
    	new ItemAddedRule(scsNull, session);
    }
    
    @Test
    public void testAddItemInDatabaseBronze() {
        session.start();
        
        while (!listener.barcodesScanned.contains(item.getBarcode())) {
        	scsb.mainScanner.scan(item);
        }
        HashMap<BarcodedProduct, Integer> productList = session.getBarcodedItems();
        assertTrue(productList.containsKey(product));
    }

    @Test
    public void testAddItemInDatabaseSilver() {
        session2.start();
        while (!listener.barcodesScanned.contains(item.getBarcode())) {
        	scss.mainScanner.scan(item);
        }
        HashMap<BarcodedProduct, Integer> productList = session2.getBarcodedItems();
        assertTrue(productList.containsKey(product));
    }

    @Test
    public void testAddItemInDatabaseGold() {
        session3.start();
        while (!listener.barcodesScanned.contains(item.getBarcode())) {
        	scsg.mainScanner.scan(item);
        }
        HashMap<BarcodedProduct, Integer> productList = session3.getBarcodedItems();
        assertTrue(productList.containsKey(product));
    }
    
    @Test
    public void testAddItemInDatabaseHandheldScannerBronze() {
        session.start();
        while (!listener.barcodesScanned.contains(item.getBarcode())) {
        	scsb.handheldScanner.scan(item);
        }
        HashMap<BarcodedProduct, Integer> productList = session.getBarcodedItems();
        assertTrue(productList.containsKey(product));
    }

    @Test
    public void testAddItemInDatabaseHandheldScannerSilver() {
        session2.start();
        while (!listener.barcodesScanned.contains(item.getBarcode())) {
        	scss.handheldScanner.scan(item);
        }
        HashMap<BarcodedProduct, Integer> productList = session2.getBarcodedItems();
        assertTrue(productList.containsKey(product));
    }

    @Test
    public void testAddItemInDatabaseHandheldScannerGold() {
        session3.start();
        while (!listener.barcodesScanned.contains(item.getBarcode())) {
        	scsg.handheldScanner.scan(item);
        }
        HashMap<BarcodedProduct, Integer> productList = session3.getBarcodedItems();
        assertTrue(productList.containsKey(product));
    }
    
    @Test(expected = InvalidArgumentSimulationException.class)
    public void testAddItemNotInDatabaseBronze() {
        session.start();
        
        Barcode barcodeNotInDatabase = new Barcode(new Numeral[] { Numeral.five, Numeral.five, Numeral.eight });
        BarcodedItem itemNotInDatabase = new BarcodedItem(barcodeNotInDatabase, new Mass(100.0));
        
        for(int i =0; i < 100; i++) {
        	scsb.mainScanner.scan(itemNotInDatabase);
        }
    }

    @Test(expected = InvalidArgumentSimulationException.class)
    public void testAddItemNotInDatabaseSilver() {
        session2.start();
        
        Barcode barcodeNotInDatabase = new Barcode(new Numeral[] { Numeral.five, Numeral.five, Numeral.eight });
        BarcodedItem itemNotInDatabase = new BarcodedItem(barcodeNotInDatabase, new Mass(100.0));
        
        for(int i =0; i < 100; i++) {
        	scss.mainScanner.scan(itemNotInDatabase);
        }
    }

    @Test(expected = InvalidArgumentSimulationException.class)
    public void testAddItemNotInDatabaseGold() {
        session3.start();
        
        Barcode barcodeNotInDatabase = new Barcode(new Numeral[] { Numeral.five, Numeral.five, Numeral.eight });
        BarcodedItem itemNotInDatabase = new BarcodedItem(barcodeNotInDatabase, new Mass(100.0));

        for(int i =0; i < 100; i++) {
        	scsg.mainScanner.scan(itemNotInDatabase);
        }
    }

    @Test
    public void testSessionNotOnBronze() {
        scsb.mainScanner.scan(item);
        HashMap<BarcodedProduct, Integer> productList = session.getBarcodedItems();
        assertFalse(productList.containsKey(product));
    }
    
    @Test
    public void testSessionNotOnSilver() {
        scss.mainScanner.scan(item);
        HashMap<BarcodedProduct, Integer> productList = session2.getBarcodedItems();
        assertFalse(productList.containsKey(product));
    }

    @Test
    public void testSessionNotOnGold() {
        scsg.mainScanner.scan(item);
        HashMap<BarcodedProduct, Integer> productList = session3.getBarcodedItems();
        assertFalse(productList.containsKey(product));
    }
    
    @Test
    public void testSessionFrozenBronze() {
    	session.start();
    	
    	while (!listener.barcodesScanned.contains(item.getBarcode())) {
    		scsb.mainScanner.scan(item);
    	}
        Barcode newBarcode = new Barcode(new Numeral[] { Numeral.six});
        BarcodedItem newItem = new BarcodedItem(newBarcode, new Mass(100.0));
        BarcodedProduct newProduct = new BarcodedProduct(newBarcode, "New Product", 10, 100.0);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(newBarcode, newProduct);
        
        scsb.mainScanner.scan(newItem);
        
        HashMap<BarcodedProduct, Integer> productList = session.getBarcodedItems();
        assertTrue(productList.containsKey(product));
        assertFalse(productList.containsKey(newProduct));
    }
    
    @Test
    public void testSessionFrozenSilver() {
    	session2.start();
    	
    	while (!listener.barcodesScanned.contains(item.getBarcode())) {
    		scss.mainScanner.scan(item);
    	}
        Barcode newBarcode = new Barcode(new Numeral[] { Numeral.six});
        BarcodedItem newItem = new BarcodedItem(newBarcode, new Mass(100.0));
        BarcodedProduct newProduct = new BarcodedProduct(newBarcode, "New Product", 10, 100.0);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(newBarcode, newProduct);
        
        scss.mainScanner.scan(newItem);
        
        HashMap<BarcodedProduct, Integer> productList = session2.getBarcodedItems();
        assertTrue(productList.containsKey(product));
        assertFalse(productList.containsKey(newProduct));
    }
    
    @Test
    public void testSessionFrozenGold() {
    	session3.start();
    	while (!listener.barcodesScanned.contains(item.getBarcode())) {
    		scsg.mainScanner.scan(item);
    	}
        Barcode newBarcode = new Barcode(new Numeral[] { Numeral.six});
        BarcodedItem newItem = new BarcodedItem(newBarcode, new Mass(100.0));
        BarcodedProduct newProduct = new BarcodedProduct(newBarcode, "New Product", 10, 100.0);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(newBarcode, newProduct);
        
        scsg.mainScanner.scan(newItem);
        
        HashMap<BarcodedProduct, Integer> productList = session3.getBarcodedItems();
        assertTrue(productList.containsKey(product));
        assertFalse(productList.containsKey(newProduct));
    }
    
    @Test
    public void testNoExceptionsOccur() {
    	scsb.mainScanner.turnOff();
    	scsb.mainScanner.turnOn();
    	scsb.mainScanner.disable();
    	scsb.mainScanner.enable();
    	
    }
   
    public class ScannerListenerStub implements BarcodeScannerListener{
    	public ArrayList<Barcode> barcodesScanned;
    	
    	ScannerListenerStub(){
    		barcodesScanned = new ArrayList<Barcode>();
    	}
    	
		@Override
		public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void aBarcodeHasBeenScanned(IBarcodeScanner barcodeScanner, Barcode barcode) {
			barcodesScanned.add(barcode);			
		}
    	
    }
}

