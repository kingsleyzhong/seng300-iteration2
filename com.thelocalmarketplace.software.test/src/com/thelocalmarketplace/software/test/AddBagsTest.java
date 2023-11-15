package com.thelocalmarketplace.software.test;

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

/*
 * Testing for the Funds class
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

public class AddBagsTest {
	
	// SelfCheckoutStations
    private SelfCheckoutStationBronze scsb = new SelfCheckoutStationBronze();
    private SelfCheckoutStationSilver scss = new SelfCheckoutStationSilver();
    private SelfCheckoutStationGold scsg = new SelfCheckoutStationGold();

    private Session session;
    private double BAG_MASS_LIMIT = 2.5;
    
    private Funds funds;
    private Weight weight;

	@Before
	public void setup() {
		
		// create a session with a known bag limit
		session = new Session(BAG_MASS_LIMIT);
		
		// 
		
		
		
	}
		
	// Customer indicates they want to add bag, adds bag then system detects weight change
	// --> this should work, session returns to normal run time
	@Test
	public void test_addBags_perfectSystem() {
		
	}
	
	// when a customer adds a bag the expected weight of the system should be updated by the
	// weight of the bag
	@Test
	public void test_addBags_updatesExpectedWeight() {
		
	}
	
	// Customer indicates they want to add bag then adds nothing
	// --> causes a weight discrepancy
	@Test
	public void test_addBags_NotAddingBagsCausesWeightDiscrepancy() {
		
	}
	
	// Customer indicates they want to add bag then removes something from scale
	// --> causes a weight discrepancy
	@Test
	public void test_addBags_unexpectedChangeCausesWeightDiscrepancy() {
		
	}
	
	// Customer indicates they want to add a bag then adds something heavy ()
	// --> causes a bagTooHeavy exception or smth
	@Test
	public void test_addBags_overweightBag_causes_problems() {
		
	}
	
	// Customer indicates they want to add bag then cancels
	// not implemented

	
	
	
	
}
