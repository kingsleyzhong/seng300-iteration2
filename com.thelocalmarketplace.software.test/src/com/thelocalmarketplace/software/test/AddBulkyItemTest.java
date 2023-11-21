package com.thelocalmarketplace.software.test;

import static org.junit.Assert.*;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.scanner.BarcodeScannerListener;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.thelocalmarketplace.software.exceptions.ProductNotFoundException;
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
 * Unit Test class for Add Bulky Item Use Case
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
        weightSilver = new Weight(scss);
        weightGold = new Weight(scsg);
    }

    /**
     * test case for adding bulky item successfully
     * Scenario: add an item, customer call addBulkyItem, assistant approves
     */
    @Test
    public void testAddBulkyItem() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        // customer indicates they want to not bag item
        session.bulkyItemCalled();
        // assistant approves
        session.assistantApprove();
        // customer calls add bulky item
        session.addBulkyItem();

        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(0);
        assertEquals("Mass is 0", expected, actual);
        assertEquals(Session.getState(), SessionState.IN_SESSION);
    }

    /**
     * test case for handling bulky item
     * tests if the system properly signals the attendant that a no bagging request is in progress
     * and if the attendant signals to the system that request is approved
     */
    @Test
    public void testAddBulkyItemAttendantCall() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        // customer indicates they want to not bag item
        session.bulkyItemCalled();
        // assistant approves
        session.assistantApprove();
        session.addBulkyItem();
        assertTrue("Bulky Item request has been approved.", session.getRequestApproved());
        assertEquals("No discrepancy", Session.getState(), SessionState.IN_SESSION);
    }

    /**
     * test case for adding two items and call addBulkyItem
     * Scenario: add items, customer call addBulkyItem, attendant approves
     */
    @Test
    public void testAddTwoBulkyItemAndInSession() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        session.addItem(product2);
        // customer indicates they want to not bag item
        session.bulkyItemCalled();
        // assistant approves
        session.assistantApprove();
        session.addBulkyItem();

        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(BigDecimal.valueOf(100));
        assertEquals("Mass is 100", expected, actual);
    }

    /**
     * test case for adding two same items and call addBulkyItem
     * Scenario: add items, customer call addBulkyItem, attendant approves
     */
    @Test
    public void testAddTwoSameBulkyItem() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        session.addItem(product);
        // customer indicates they want to not bag item
        session.bulkyItemCalled();
        // assistant approves
        session.assistantApprove();
        session.addBulkyItem();

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
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);

        while (!listener.barcodesScanned.contains(item.getBarcode())) {
            scs.handheldScanner.scan(item);
        }

        // customer indicates they want to not bag item
        session.bulkyItemCalled();
        // assistant approves
        session.assistantApprove();
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

        // customer indicates they want to not bag item
        session.bulkyItemCalled();
        // assistant approves
        session.assistantApprove();
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

        // customer indicates they want to not bag item
        session.bulkyItemCalled();
        // assistant approves
        session.assistantApprove();
        session.addBulkyItem();
        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(0);
        assertEquals("Mass is 0", expected, actual);
    }

    /**
     * test case for causing weight discrepancy 1 using bronze station
     * scenario: add item, customer call addBulkyItem, attendant approves, then place the bulky item in the bagging area anyways
     *
     * This will cause a weight discrepancy, which can have 3 options for fixing the issue
     *      1. customer removes the item
     *      2. customer signals the system for AddBulkyItem
     *      3. attendant signals the system of weight discrepancy approval
     */
    @Test
    public void testBulkyItemWeightDiscrepancyBronze() {
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        // customer indicates they want to not bag item
        session.bulkyItemCalled();
        // assistant approves
        session.assistantApprove();
        session.addBulkyItem();
        scs.baggingArea.addAnItem(new BarcodedItem(barcode, new Mass(100.0)));
        assertEquals("Discrepancy must have occurred", Session.getState(), SessionState.BLOCKED);
    }

    /**
     * test case for causing weight discrepancy 1 using silver station
     * scenario: add item, customer call addBulkyItem, attendant approves, then place the bulky item in the bagging area anyways
     *
     * This will cause a weight discrepancy, which can have 3 options for fixing the issue
     *      1. customer removes the item
     *      2. customer signals the system for AddBulkyItem
     *      3. attendant signals the system of weight discrepancy approval
     */
    @Test
    public void testBulkyItemWeightDiscrepancySilver() {
        scss.plugIn(PowerGrid.instance());
        scss.turnOn();
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weightSilver);
        session.addItem(product);
        // customer indicates they want to not bag item
        session.bulkyItemCalled();
        // assistant approves
        session.assistantApprove();
        session.addBulkyItem();
        scss.baggingArea.addAnItem(new BarcodedItem(barcode, new Mass(100.0)));
        assertEquals("Discrepancy must have occurred", Session.getState(), SessionState.BLOCKED);
    }


    /**
     * test case for causing weight discrepancy 1 using gold station
     * scenario: add item, customer call addBulkyItem, attendant approves, then place the bulky item in the bagging area anyways
     *
     * This will cause a weight discrepancy, which can have 3 options for fixing the issue
     *      1. customer removes the item
     *      2. customer signals the system for AddBulkyItem
     *      3. attendant signals the system of weight discrepancy approval
     */
    @Test
    public void testBulkyItemWeightDiscrepancyGold() {
        scsg.plugIn(PowerGrid.instance());
        scsg.turnOn();
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weightGold);
        session.addItem(product);
        // customer indicates they want to not bag item
        session.bulkyItemCalled();
        // assistant approves
        session.assistantApprove();
        session.addBulkyItem();
        scsg.baggingArea.addAnItem(new BarcodedItem(barcode, new Mass(100.0)));
        assertEquals("Discrepancy must have occurred", Session.getState(), SessionState.BLOCKED);
    }

    /**
     * test case for fixing weight discrepancy 1 using bronze station
     * scenario: add item, customer call addBulkyItem, attendant approves, then place the bulky item in the bagging area anyways,
     *           but then remove the item from the bagging area to fix weight discrepancy
     *
     * weight discrepancy fixed using option 1
     */
    @Test
    public void testBulkyItemWeightDiscrepancyResolvedBronze() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        // customer indicates they want to not bag item
        session.bulkyItemCalled();
        // assistant approves
        session.assistantApprove();
        session.addBulkyItem();
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();
        BarcodedItem item = new BarcodedItem(barcode, new Mass(100.0));
        scs.baggingArea.addAnItem(item);
        scs.baggingArea.removeAnItem(item);
        assertEquals("Discrepancy resolved", Session.getState(), SessionState.IN_SESSION);
    }

    /**
     * test case for fixing weight discrepancy 1 using silver station
     * scenario: add item, customer call addBulkyItem, attendant approves, then place the bulky item in the bagging area anyways,
     *           but then remove the item from the bagging area to fix weight discrepancy
     *
     * weight discrepancy fixed using option 1
     */
    @Test
    public void testBulkyItemWeightDiscrepancyResolvedSilver() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        // customer indicates they want to not bag item
        session.bulkyItemCalled();
        // assistant approves
        session.assistantApprove();
        session.addBulkyItem();
        scss.plugIn(PowerGrid.instance());
        scss.turnOn();
        BarcodedItem item = new BarcodedItem(barcode, new Mass(100.0));
        scss.baggingArea.addAnItem(item);
        scss.baggingArea.removeAnItem(item);
        assertEquals("Discrepancy resolved", Session.getState(), SessionState.IN_SESSION);
    }

    /**
     * test case for fixing weight discrepancy 1 using gold station
     * scenario: add item, customer call addBulkyItem, attendant approves, then place the bulky item in the bagging area anyways,
     *           but then remove the item from the bagging area to fix weight discrepancy
     *
     * weight discrepancy fixed using option 1
     */
    @Test
    public void testBulkyItemWeightDiscrepancyResolvedGold() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        // customer indicates they want to not bag item
        session.bulkyItemCalled();
        // assistant approves
        session.assistantApprove();
        session.addBulkyItem();
        scsg.plugIn(PowerGrid.instance());
        scsg.turnOn();
        BarcodedItem item = new BarcodedItem(barcode, new Mass(100.0));
        scsg.baggingArea.addAnItem(item);
        scsg.baggingArea.removeAnItem(item);
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
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        // customer indicates they want to not bag item
        session.bulkyItemCalled();
        // assistant approves
        session.assistantApprove();
        session.addBulkyItem();
        assertEquals("Discrepancy is fixed", Session.getState(), SessionState.IN_SESSION);
    }

    /**
     * test case for fixing weight discrepancy 1 by calling attendant using bronze station
     * scenario: add item, call addBulkyItem(), then place the bulky item in the bagging area anyways,
     * fix: by calling attendant
     *
     * weight discrepancy fixed using option 3 (call attendant)
     */
    @Test
    public void testAttendantRemoveItemBronze() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        // customer indicates they want to not bag item
        session.bulkyItemCalled();
        // assistant approves
        session.assistantApprove();
        session.addBulkyItem();
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();
        scs.baggingArea.addAnItem(item);
        // customer calls bulky item but still places it in the bagging area; weight discrepancy
        // customer calls assistant for help
        session.callAssistantForWeightDiscrepancy();
        // assistant fixes weight discrepancy
        session.attendantFixWeightDiscrepancy(scs, item);
        assertEquals("Discrepancy resolved", Session.getState(), SessionState.IN_SESSION);
    }

    /**
     * test case for fixing weight discrepancy 1 by calling attendant using silver station
     * scenario: add item, call addBulkyItem(), then place the bulky item in the bagging area anyways,
     * fix: by calling attendant
     *
     * weight discrepancy fixed using option 3 (call attendant)
     */
    @Test
    public void testAttendantRemoveItemSilver() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weightSilver);
        session.addItem(product);
        // customer indicates they want to not bag item
        session.bulkyItemCalled();
        // assistant approves
        session.assistantApprove();
        session.addBulkyItem();
        scss.plugIn(PowerGrid.instance());
        scss.turnOn();
        scss.baggingArea.addAnItem(item);
        // customer calls bulky item but still places it in the bagging area; weight discrepancy
        // customer calls assistant for help
        session.callAssistantForWeightDiscrepancy();
        // assistant fixes weight discrepancy
        session.attendantFixWeightDiscrepancy(scss, item);
        assertEquals("Discrepancy resolved", Session.getState(), SessionState.IN_SESSION);
    }

    /**
     * test case for fixing weight discrepancy 1 by calling attendant using gold station
     * scenario: add item, call addBulkyItem(), then place the bulky item in the bagging area anyways,
     * fix: by calling attendant
     *
     * weight discrepancy fixed using option 3 (call attendant)
     */
    @Test
    public void testAttendantRemoveItemGold() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weightGold);
        session.addItem(product);
        // customer indicates they want to not bag item
        session.bulkyItemCalled();
        // assistant approves
        session.assistantApprove();
        session.addBulkyItem();
        scsg.plugIn(PowerGrid.instance());
        scsg.turnOn();
        scsg.baggingArea.addAnItem(item);
        // customer calls bulky item but still places it in the bagging area; weight discrepancy
        // customer calls assistant for help
        session.callAssistantForWeightDiscrepancy();
        // assistant fixes weight discrepancy
        session.attendantFixWeightDiscrepancy(scsg, item);
        assertEquals("Discrepancy resolved", Session.getState(), SessionState.IN_SESSION);
    }

    /**
     * test case for fixing weight discrepancy 2 by calling attendant
     * scenario: add item, do not call addBulkyItem(), and not place the item in the bagging area,
     * fix: by calling attendant
     *
     * weight discrepancy fixed using option 3 (call attendant)
     */
    @Test
    public void testAttendantCallBulkyItem() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        // customer has scanned a product but did not place it in the bagging area; weight discrepancy
        // customer calls assistant for help
        session.callAssistantForWeightDiscrepancy();
        // assistant fixes weight discrepancy
        session.attendantFixWeightDiscrepancy();
        assertEquals("Discrepancy is fixed", Session.getState(), SessionState.IN_SESSION);
    }

    /**
     * test case to check bulky item cancelled by customer
     */
    @Test
    public void testCancelBulkyItem() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        // customer indicates they want to not bag item
        session.bulkyItemCalled();
        // assistant approves
        session.assistantApprove();
        session.addBulkyItem();
        session.cancelBulkyItem();
        assertFalse("Bulky item is cancelled", session.getBulkyItemCalled());
    }

    /**
     * test case for calling add bulky item when attendant did not approve
     */
    @Test
    public void testRequestNotApproved() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        session.bulkyItemCalled();
        // call addBulkyItem without attendant approval
        session.addBulkyItem();
        assertFalse("Bulky item request is not approved.", session.getRequestApproved());
    }

    /**
     * test case for cancelling add bulky item when it was not called
     */
    @Test
    public void testBulkyItemNotCalled() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        // cancel bulky item when bulky item was not called
        session.cancelBulkyItem();
        assertFalse("Bulky item was not called.", session.getBulkyItemCalled());
    }

    /**
     * test case for checking that attendant is not called when there is no weight discrepancy using bronze station
     */
    @Test
    public void testAttendantNotCalledBronze() {
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();

        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        session.bulkyItemCalled();
        session.assistantApprove();
        session.addBulkyItem();
        session.cancelBulkyItem();

        // there is no weight discrepancy; no need for attendant
        scs.baggingArea.addAnItem(item);
        session.callAssistantForWeightDiscrepancy();
        session.attendantFixWeightDiscrepancy(scs, item);
        assertFalse("There is no weight discrepancy.", session.getWeight().isDiscrepancy());
    }

    /**
     * test case for checking that attendant is not called when there is no weight discrepancy using silver station
     */
    @Test
    public void testAttendantNotCalledSilver() {
        scss.plugIn(PowerGrid.instance());
        scss.turnOn();

        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        session.bulkyItemCalled();
        session.assistantApprove();
        session.addBulkyItem();
        session.cancelBulkyItem();

        // there is no weight discrepancy; no need for attendant
        scss.baggingArea.addAnItem(item);
        session.callAssistantForWeightDiscrepancy();
        session.attendantFixWeightDiscrepancy(scss, item);
        assertFalse("There is no weight discrepancy.", session.getWeight().isDiscrepancy());
    }

    /**
     * test case for checking that attendant is not called when there is no weight discrepancy using gold station
     */
    @Test
    public void testAttendantNotCalledGold() {
        scsg.plugIn(PowerGrid.instance());
        scsg.turnOn();

        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        session.bulkyItemCalled();
        session.assistantApprove();
        session.addBulkyItem();
        session.cancelBulkyItem();

        // there is no weight discrepancy; no need for attendant
        scsg.baggingArea.addAnItem(item);
        session.callAssistantForWeightDiscrepancy();
        session.attendantFixWeightDiscrepancy(scsg, item);
        assertFalse("There is no weight discrepancy.", session.getWeight().isDiscrepancy());
    }

    /**
     * test case for no weight discrepancy (attendant is not called)
     */
    @Test
    public void testNoWeightDiscrepancy() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        session.bulkyItemCalled();
        session.assistantApprove();
        session.addBulkyItem();
        session.attendantFixWeightDiscrepancy();
        assertFalse("There is no weight discrepancy.", session.getWeight().isDiscrepancy());
    }

    @Test
    public void testRemoveBulkyItem() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.setup2(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        // customer indicates they want to not bag item
        session.bulkyItemCalled();
        // assistant approves
        session.assistantApprove();
        // customer calls add bulky item
        session.addBulkyItem();
        session.addBulkyItemToMap(product);
        session.removeBulkyItem(product);
        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(0);
        assertEquals("Mass is 0", expected, actual);
    }

    @Test
    public void testRemoveBulkyItemTwoItems() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.setup2(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        session.addItem(product);
        session.bulkyItemCalled();
        session.assistantApprove();
        session.addBulkyItem();
        session.addBulkyItemToMap(product);
        session.addBulkyItemToMap(product);
        session.removeBulkyItem(product);
        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(BigDecimal.valueOf(100));
        assertEquals("Mass is 100", expected, actual);
    }

    @Test
    public void testRemoveBulkyItemThreeItems() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.setup2(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        session.addItem(product);
        session.addItem(product);
        session.bulkyItemCalled();
        session.assistantApprove();
        session.addBulkyItem();
        session.addBulkyItemToMap(product);
        session.addBulkyItemToMap(product);
        session.addBulkyItemToMap(product);
        session.removeBulkyItem(product);
        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(BigDecimal.valueOf(200));
        assertEquals("Mass is 200", expected, actual);
    }

    @Test (expected = ProductNotFoundException.class)
    public void testRemoveBulkyItemNotFound() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.setup2(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        // customer indicates they want to not bag item
        session.bulkyItemCalled();
        // assistant approves
        session.assistantApprove();
        // customer calls add bulky item
        session.addBulkyItem();
        session.removeBulkyItem(product);
    }

    @Test
    public void testRemoveBulkyItemNotCalled() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.setup2(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        session.addBulkyItemToMap(product);
        session.removeBulkyItem(product);
        assertFalse(session.getBulkyItemCalled());
    }

    @Test
    public void testRemoveBulkyItemRequestNotApproved() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.setup2(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        session.bulkyItemCalled();
        session.addBulkyItemToMap(product);
        session.removeBulkyItem(product);
        assertFalse(session.getRequestApproved());
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
