package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Item;
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
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.rules.ItemAddedRule;
import com.thelocalmarketplace.software.weight.Weight;

import StubClasses.BagStub;
import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import junit.framework.Assert;
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
	private AbstractSelfCheckoutStation scs; 
    private SelfCheckoutStationBronze scsb;// = new SelfCheckoutStationBronze();
    private SelfCheckoutStationSilver scss;// = new SelfCheckoutStationSilver();
    private SelfCheckoutStationGold scsg;// = new SelfCheckoutStationGold();

    
    // power grid
    PowerGrid powergrid;
    
    private Session session;
    private double BAG_MASS_LIMIT = 2.5;
    
    private Funds funds;
    private Weight weight;
    
    // bag
    BagStub bag;
    BagStub overweightBag;
    BagStub weightLimitBag;
    Mass bagMass;
    Mass overweightBagMass;
    Mass weightLimitBagMass;

	@Before
	public void setup() {
    	
		// set up the self checkout station
		scs.resetConfigurationToDefaults();
    	scsb.resetConfigurationToDefaults();
    	
    	
    	scsb = new SelfCheckoutStationBronze();
    	
    	// power on the self checkout station
    	powergrid = PowerGrid.instance();
    	powergrid.engageUninterruptiblePowerSource();
		
    	scsb.plugIn(powergrid);
    	// turn on the self checkout station
		scsb.turnOn();
    	
		// create a session with a known bag limit
		session = new Session(BAG_MASS_LIMIT);
		
		// Create Weight and Funds objects (we are only working with weight for these tests,
		// could probably stub out Funds?)
        funds = new Funds(scsb);
        weight = new Weight(scsb);
		
        // Tell Session about the rest of the system
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);
        
        // create "bags"
        bagMass = new Mass(1 * Mass.MICROGRAMS_PER_GRAM);// bag of mass 1g < BAG MASS LIMIT
        overweightBagMass = new Mass(BAG_MASS_LIMIT + 1 * Mass.MICROGRAMS_PER_GRAM);// mass > BAG MASS LIMIT
        weightLimitBagMass = new Mass(BAG_MASS_LIMIT * Mass.MICROGRAMS_PER_GRAM);// mass equal to the limited size of a bag in the session software
       
        bag = new BagStub(bagMass);
        overweightBag = new BagStub(overweightBagMass);
        weightLimitBag = new BagStub(weightLimitBagMass);
		
	}
	
	
	// Customer indicates they want to add bag, adds bag then system detects weight change
	// --> this should work, session returns to normal run time
	/*
	 * Tests that calling addBag() before the session has started has no impact on the state of session
	 * ie: the expected weight doesnt change and the session remails in pre-session state 
	 * Expected Behaviour: the expected weight of session is updated to include the bag's weight
	 */
	@Test
	public void test_addBags_beforeStartSession_stateUnchanged() {		
		// call addBags
		session.addBags();
		
		// add the bags to the bagging area
		scsb.baggingArea.addAnItem(bag);
				
		// the session has not started
		assertFalse(session.getState() == SessionState.IN_SESSION); 
	}
	@Test
	public void test_addBags_beforeStartSession_expectedWeightUnchanged() {		
		// save the expected Mass before adding the bag
		Mass expectedMassBefore = weight.getExpectedWeight();
				
		// call addBags
		session.addBags();
				
		// add the bags to the bagging area
		scsb.baggingArea.addAnItem(bag);
				
		// check the expected weight has been updated (?)
				
		Mass expectedMassAfter = weight.getExpectedWeight();
				
		// compare the masses to see they have updated
		assertTrue(expectedMassAfter.compareTo(expectedMassBefore) == 0);
	}

	
	
	/*
	 * Tests that calling addBag() and then adding the bag to the bagging area does not result in
	 * any issues.
	 * 
	 * Expected Behaviour: the expected weight of session is updated to include the bag's weight
	 */
	@Test
	public void test_addBags_addingBagsUnblocksSession() {
		// start session:
		session.start();
		
		// call addBags
		session.addBags();
		
		// add the bags to the bagging area
		scsb.baggingArea.addAnItem(bag);
		
		
		// the system is unblocked
		assertFalse(session.getState() == SessionState.BLOCKED); 
	}
	
	// when a customer adds a bag the expected weight of the system should be updated by the
	// weight of the bag
	/*
	 * Tests that calling addBag() and then adding the bag to the bagging area does not result in
	 * any issues.
	 * 
	 * Expected Behaviour: the expected weight of session is updated to include the bag's weight
	 */
	@Test
	public void test_addBags_updatesExpectedWeight() {
		// start session:
		session.start();
				
		
		// save the expected Mass before adding the bag
		Mass expectedMassBefore = weight.getExpectedWeight();
				
		// call addBags
		session.addBags();
				
		// add the bags to the bagging area
		scsb.baggingArea.addAnItem(bag);
				
		// check the expected weight has been updated (?)
				
		Mass expectedMassAfter = weight.getExpectedWeight();
				
		// compare the masses to see they have updated
		assertFalse(expectedMassAfter.compareTo(expectedMassBefore) == 0);
	}

	@Test
	public void test_addBags_updatesExpectedWeightByBagWeight() {
		// start session:
		session.start();
				
		
		// save the expected Mass before adding the bag
		Mass expectedMassBefore = weight.getExpectedWeight();
				
		// call addBags
		session.addBags();
				
		// add the bags to the bagging area
		scsb.baggingArea.addAnItem(bag);
				
		// check the expected weight has been updated (?)
				
		Mass expectedMassAfter = weight.getExpectedWeight();
		
		// compare the masses to see they have updated by the expected amount
		assertTrue(expectedMassAfter.compareTo(expectedMassBefore.sum(bagMass)) == 0);
	}

	
	// Customer indicates they want to add bag then adds nothing
	// --> causes a weight discrepancy
	/*
	 * Tests that not adding the bag to the bagging area causes the session to be blocked 
	 * 
	 */
/*
	@Test
	public void test_addBags_NotAddingBagsCausesWeightDiscrepancy() {
		
	}
*/
	
	// Customer indicates they want to add bag then removes something from scale
	// --> doesnt update expected weight
	@Test
	public void test_addBags_unexpectedChange_doesntUpdateExpectedWeight() {
		// start session:
		session.start();

		
		// pre-test: add an item to the bagging area
		BagStub notBag = new BagStub(new Mass(3 * Mass.MICROGRAMS_PER_GRAM));
		scsb.baggingArea.addAnItem(notBag);

		// save the expected Mass before adding the bag
		Mass expectedMassBefore = weight.getExpectedWeight();
						
		// call addBags
		session.addBags();
						
		// remove the not-bag from the bagging area
		scsb.baggingArea.removeAnItem(notBag);;
						
		// check the expected weight after the interaction				
		Mass expectedMassAfter = weight.getExpectedWeight();
						
		// compare the masses to see they have not updated
		assertTrue(expectedMassAfter.compareTo(expectedMassBefore) == 0);
	}
	
	// Customer indicates they want to add a bag then adds something heavy ()
	// --> causes a bagTooHeavy exception or smth
	//
	/*
	 * Tests adding an overly heavy item to the bagging area (above the bag weight limit) when adding bags
	 * The system will note this, and notify the attendant. The system will not be unblocked.
	 * 
	 */
	@Test
	public void test_addBags_overweightBag_doesntUnblockSession() {
		// start session:
		session.start();

		
		// call addBags
		session.addBags();
								
		// remove the not-bag from the bagging area
		scsb.baggingArea.addAnItem(overweightBag);
																
		// compare the masses to see they have not updated
		assertTrue(session.getState() == SessionState.BLOCKED);
	}

	/*
	 * Tests adding an overly heavy item to the bagging area (equal to the bag weight limit) when adding bags
	 * The system will note this, and notify the attendant. The system will not be unblocked.
	 * 
	 */
	@Test
	public void test_addBags_weightLimitBag_doesntUnblockSession() {
		// start session:
		session.start();

		
		// call addBags
		session.addBags();
								
		// remove the not-bag from the bagging area
		scsb.baggingArea.addAnItem(weightLimitBag);
																
		// compare the masses to see they have not updated
		assertTrue(session.getState() == SessionState.BLOCKED);
	}

	
	
	// Customer indicates they want to add bag then cancels
	// not implemented

	
	
	
	
}
