package com.thelocalmarketplace.software.test;

import static org.junit.Assert.*;

import com.thelocalmarketplace.software.exceptions.InvalidActionException;
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

import java.util.HashMap;

/**
 * Unit Test class for Session and interaction with surrounding classes Weight
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
public class AddBulkyItemTest {
    private SelfCheckoutStationBronze scs;
    private SelfCheckoutStationSilver scss;
    private SelfCheckoutStationGold scsg;

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

    /**
     * test case for adding bulky item
     * Scenario: add an item, call addBulkyItem
     */
    @Test
    public void testAddBulkyItem() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);   // mass is 100
        session.addBulkyItem();     // mass should now be 0
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();

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
        session.addItem(product);   // mass is 100
        session.addBulkyItem();     // mass should now be 0
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
     * test case for calling AddBulkyItem() when there is no weight discrepancy
     */
    @Test (expected = InvalidActionException.class)
    public void testCallAddBulkyItemWithoutWeightDiscrepancy() {
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        session.addItem(product);
        BarcodedItem item = new BarcodedItem(barcode, new Mass(100.0));
        scs.baggingArea.addAnItem(item);
        session.addBulkyItem();
    }
}
