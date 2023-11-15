package com.thelocalmarketplace.software.test;

import static org.junit.Assert.*;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.scanner.BarcodeScannerListener;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.thelocalmarketplace.software.Attendant;
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
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.weight.Weight;

import powerutility.PowerGrid;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Unit Test class for Add Bulky Item
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
public class AddBulkyItemTest {
    private SelfCheckoutStationBronze scs;
    private SelfCheckoutStationSilver scss;
    private SelfCheckoutStationGold scsg;

    private Session session;
    private Session sessionBronze;
    private Session sessionSilver;
    private Session sessionGold;
    private BarcodedProduct product;
    private BarcodedProduct product2;
    private BarcodedItem item;
    private ScannerListenerStub listener;

    byte num;
    private Numeral numeral;
    private Numeral[] digits;
    private Barcode barcode;
    private Barcode barcode2;

    private Funds funds;
    private Weight weight;
    private Weight weightBronze;
    private Weight weightSilver;
    private Weight weightGold;

    @Before
    public void setUp() {
        AbstractSelfCheckoutStation.resetConfigurationToDefaults();

        scs = new SelfCheckoutStationBronze();
        scss = new SelfCheckoutStationSilver();
        scsg = new SelfCheckoutStationGold();

        listener = new ScannerListenerStub();
        scs.handheldScanner.register(listener);
        scss.handheldScanner.register(listener);
        scsg.handheldScanner.register(listener);

        session = new Session();
        sessionBronze = new Session();
        sessionSilver = new Session();
        sessionGold = new Session();

        num = 1;
        numeral = Numeral.valueOf(num);
        digits = new Numeral[] { numeral, numeral, numeral };
        barcode = new Barcode(digits);
        barcode2 = new Barcode(new Numeral[] { numeral });
        product = new BarcodedProduct(barcode, "Sample Product", 10, 100.0);
        product2 = new BarcodedProduct(barcode2, "Sample Product 2", 15, 20.0);
        funds = new Funds(scs);
        item = new BarcodedItem(barcode, new Mass(100.0));

        weight = new Weight(scs);
        weightBronze = new Weight(scs);
        weightSilver = new Weight(scss);
        weightGold = new Weight(scsg);
    }

    /**
     * test case for adding bulky item
     * Scenario: add an item, call addBulkyItem
     */
    @Test
    public void testAddBulkyItem() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        session.addBulkyItem();
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();

        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(0);
        assertEquals("Mass is 0", expected, actual);
    }

    /**
     * test case for adding two items and call addBulkyItem
     * Scenario: add items, call addBulkyItem
     */
    @Test
    public void testAddTwoBulkyItem() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        session.addItem(product2);
        session.addBulkyItem();
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();

        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(BigDecimal.valueOf(100));
        assertEquals("Mass is 100", expected, actual);
    }

    /**
     * test case for adding two same items and call addBulkyItem
     * Scenario: add items, call addBulkyItem
     */
    @Test
    public void testAddTwoSameBulkyItem() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        session.addItem(product);
        session.addBulkyItem();
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();

        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(BigDecimal.valueOf(100));
        assertEquals("Mass is 100", expected, actual);
    }

    /**
     * test case for adding bulky item by handheld scan bronze
     * Scenario: scan an item with handheld scanner, call addBulkyItem
     */
    @Test
    public void testAddBulkyItemBronzeHandheldScanner() {
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weightBronze);

        while (!listener.barcodesScanned.contains(item.getBarcode())) {
            scs.handheldScanner.scan(item);
        }

        session.addBulkyItem();
        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(0);
        assertEquals("Mass is 0", expected, actual);
    }

    /**
     * test case for adding bulky item by handheld scan silver
     * Scenario: scan an item with handheld scanner, call addBulkyItem
     */
    @Test
    public void testAddBulkyItemSilverHandheldScanner() {
        scss.plugIn(PowerGrid.instance());
        scss.turnOn();
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weightSilver);

        while (!listener.barcodesScanned.contains(item.getBarcode())) {
            scss.handheldScanner.scan(item);
        }

        session.addBulkyItem();
        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(0);
        assertEquals("Mass is 0", expected, actual);
    }

    /**
     * test case for adding bulky item by handheld scan gold
     * Scenario: scan an item with handheld scanner, call addBulkyItem
     */
    @Test
    public void testAddBulkyItemGoldHandheldScanner() {
        scsg.plugIn(PowerGrid.instance());
        scsg.turnOn();
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weightGold);

        while (!listener.barcodesScanned.contains(item.getBarcode())) {
            scsg.handheldScanner.scan(item);
        }

        session.addBulkyItem();
        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(0);
        assertEquals("Mass is 0", expected, actual);
    }


    /**
     * test case for checking if the session is IN_SESSION if adding bulky item
     * scenario: add item, call addBulkyItem()
     */
    @Test
    public void testAddBulkyItemInSession() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        session.addBulkyItem();
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();
        assertEquals(Session.getState(), SessionState.IN_SESSION);
    }

    /**
     * test case for causing weight discrepancy 1
     * scenario: add item, call addBulkyItem(), then place the bulky item in the bagging area anyways
     *
     * This will cause a weight discrepancy, which can have 3 options for fixing the issue
     *      1. customer removes the item
     *      2. customer signals the system for AddBulkyItem
     *      3. attendant signals the system of weight discrepancy approval
     */
    @Test
    public void testBulkyItemWeightDiscrepancy() {
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        session.addBulkyItem();
        scs.baggingArea.addAnItem(new BarcodedItem(barcode, new Mass(100.0)));
        assertEquals("Discrepancy must have occurred", Session.getState(), SessionState.BLOCKED);
    }

    /**
     * test case for fixing weight discrepancy 1
     * scenario: add item, call addBulkyItem(), then place the bulky item in the bagging area anyways,
     *           but then remove the item from the bagging area to fix weight discrepancy
     *
     * weight discrepancy fixed using option 1
     */
    @Test
    public void testBulkyItemWeightDiscrepancyResolved() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        session.addBulkyItem();
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();
        BarcodedItem item = new BarcodedItem(barcode, new Mass(100.0));
        scs.baggingArea.addAnItem(item);
        scs.baggingArea.removeAnItem(item);
        assertEquals("Discrepancy resolved", Session.getState(), SessionState.IN_SESSION);
    }

    /**
     * test case for weight discrepancy 2
     * scenario: add item, do not call addBulkyItem(), and not place the item in the bagging area
     *
     * This will cause a weight discrepancy, and the system will signal the customer if they want to call AddBulkyItem
     * 3 options for fixing the issue
     *      1. customer removes the item
     *      2. customer signals the system for AddBulkyItem
     *      3. attendant signals the system of weight discrepancy approval
     */
    @Test
    public void testAddItemButNotCallBulkyItem() {
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        assertEquals("Discrepancy must have occurred", Session.getState(), SessionState.BLOCKED);
    }

    /**
     * test case for fixing weight discrepancy 2
     * scenario: add item, do not call addBulkyItem(), and not place the item in the bagging area,
     *           then call addBulkyItem() to fix weight discrepancy
     *
     * weight discrepancy fixed using option 2
     */
    @Test
    public void testAddItemButNotCallBulkyItemFixed() {
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        session.addBulkyItem();
        assertEquals("Discrepancy is fixed", Session.getState(), SessionState.IN_SESSION);
    }

    /**
     * test case for fixing weight discrepancy by calling attendant 1
     * scenario: add item, call addBulkyItem(), then place the bulky item in the bagging area anyways,
     * fix: by calling attendant
     *
     * weight discrepancy fixed using option 3
     */
    @Test
    public void testBulkyItemWeightDiscrepancyCallAttendant() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        session.addBulkyItem();
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();
        BarcodedItem item = new BarcodedItem(barcode, new Mass(100.0));
        scs.baggingArea.addAnItem(item);
        Attendant attendant = new Attendant();
        attendant.attendantFixNoCallAddBulkyItem(scs, item);
        assertEquals("Discrepancy resolved", Session.getState(), SessionState.IN_SESSION);
    }

    /**
     * test case for fixing weight discrepancy by calling attendant 2
     * scenario: add item, do not call addBulkyItem(), and not place the item in the bagging area,
     * fix: by calling attendant
     *
     * weight discrepancy fixed using option 3
     */
    @Test
    public void testAddItemButNotCallBulkyItemCallAttendant() {
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        Attendant attendant = new Attendant();
        attendant.attendantFixNoItemInBaggingArea(session);
        assertEquals("Discrepancy is fixed", Session.getState(), SessionState.IN_SESSION);
    }

    /**
     * test case for calling AddBulkyItem() when there is no weight discrepancy
     */
    @Test
    public void testCallAddBulkyItemWithoutWeightDiscrepancy() {
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        BarcodedItem item = new BarcodedItem(barcode, new Mass(100.0));
        scs.baggingArea.addAnItem(item);
        session.addBulkyItem();
        assertFalse("Cannot call AddBulkyItem if there is no weight discrepancy", session.getCallAddBulkyItem());
    }



    public class ScannerListenerStub implements BarcodeScannerListener {
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
