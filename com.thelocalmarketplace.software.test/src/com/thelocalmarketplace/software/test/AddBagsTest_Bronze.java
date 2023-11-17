package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;
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
 * Testing for methods related to the AddBags use case
 * 	- method Session.addBags()
 * 	- method Session.checkBags()
 * 	- method Session.bagsTooHeavy()
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

public class AddBagsTest_Bronze {
	
	// SelfCheckoutStations
	private SelfCheckoutStationBronze scs; 
    
    // power grid
    PowerGrid powergrid;
    
    private Session session;
    private double BAG_MASS_LIMIT = 250.00;// 250g, well above what a bag probably weighs
    										// above the sensativity limit for even a bronze scale (5g)
    
    private Funds funds;
    private Weight weight;
    
    // bag
    BagStub bag;
    BagStub overweightBag;
    BagStub weightLimitBag;
    BagStub notBag;
    Mass bagMass;
    Mass overweightBagMass;
    Mass weightLimitBagMass;
    Mass notBagMass;
    
    // time to wait before adding item to the bagging area
    
	@Before
	public void setup() {
    	
		// set up the self checkout station
		scs.resetConfigurationToDefaults();
    	
		// make the self checkout station a bronze version
    	scs = new SelfCheckoutStationBronze();
    	
    	//System.out.println("Mass limit: " + scs.baggingArea.getMassLimit().toString());
    	//System.out.println("Sensitivity limit: " + scs.baggingArea.getSensitivityLimit().toString());
    	
    	// power on the self checkout station
    	powergrid = PowerGrid.instance();
    	powergrid.engageUninterruptiblePowerSource();
		
    	scs.plugIn(powergrid);
    	// turn on the self checkout station
		scs.turnOn();

        // create "bags"
        
        bagMass = new Mass(10 * Mass.MICROGRAMS_PER_GRAM);// bag of mass 100g < BAG MASS LIMIT
        overweightBagMass = new Mass((BAG_MASS_LIMIT + 60) );// mass > BAG MASS LIMIT
        weightLimitBagMass = new Mass(BAG_MASS_LIMIT );// mass equal to the limited size of a bag in the session software
		notBagMass = new Mass((30 * Mass.MICROGRAMS_PER_GRAM));

        bag = new BagStub(bagMass);
        overweightBag = new BagStub(overweightBagMass);
        weightLimitBag = new BagStub(weightLimitBagMass);
		notBag = new BagStub(notBagMass);

    	//System.out.println("Normal bag: " + bagMass.toString());
    	//System.out.println("Not bag: " + notBagMass.toString());
    	//System.out.println("Mass limit bag: " + weightLimitBagMass.toString());
    	//System.out.println("Overweight bag: " + overweightBagMass.toString());

        
		// create a session with a known bag limit
		session = new Session(BAG_MASS_LIMIT);
		// Create Weight and Funds objects (we are only working with weight for these tests,
		// could probably stub out Funds?)
        funds = new Funds(scs);
        weight = new Weight(scs);
		// Tell Session about the rest of the system
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight);

        // make sure the session is not active before you run the tests
        session.cancel();
	}
	
	
	/*
	 * Tests that calling addBag() before the session has started has no impact on the state of session
	 * ie: the expected weight doesnt change and the session remains in pre-session state 
	 * 
	 * Expected Behavior: the session remains in the pre-session state
	 */
	@Test
	public void test_addBags_beforeStartSession_stateUnchanged() {		
		// call addBags
		session.addBags();
		
		// add the bags to the bagging area
		scs.baggingArea.addAnItem(bag);
				
		// the session has not started
		assertFalse(session.getState() == SessionState.PRE_SESSION); 
	}
	/*
	 * Tests that calling addBag() before the session has started has no impact on the state of session
	 * ie: the expected weight doesnt change and the session remains in pre-session state 
	 * 
	 * Expected Behavior: the expected weight on the scale doesnt change
	 */
	@Test
	public void test_addBags_beforeStartSession_expectedWeightUnchanged() {		
		// save the expected Mass before adding the bag
		Mass expectedMassBefore = weight.getExpectedWeight();
		
		// call addBags
		session.addBags();
				
		// add the bags to the bagging area
		scs.baggingArea.addAnItem(bag);
				
		// check the expected weight has been updated (?)
				
		Mass expectedMassAfter = weight.getExpectedWeight();
				
		// compare the masses to see they have updated
		assertTrue(expectedMassAfter.compareTo(expectedMassBefore) == 0);
	}
	
	/*
	 * Tests that calling addBags() during an active session updates the state of the Session to reflect this selection.
	 * 
	 * Expected behavior: Session.sessionState == ADDING_BAGS
	 */
	@Test
	public void test_addBags_updatesSessionState() {
		// start session:
		session.start();
		
		// call addBags
		session.addBags();
		
		// the system is in the adding bags state
		assertTrue(session.getState() == SessionState.ADDING_BAGS); 
	}


	/*
	 * Tests that calling addBag() during an active session and then adding the bag to the 
	 * bagging area does not result in any issues.
	 * 
	 * Expected Behavior: the session returns to normal runtime state (in session)
	 */
	@Test
	public void test_addBags_addingBagsUnblocksSession() {
		// start session:
		session.start();
		
		// call addBags
		session.addBags();
		
		// add the bags to the bagging area
		scs.baggingArea.addAnItem(bag);
		
		// the system is unblocked
		assertTrue(session.getState() == SessionState.IN_SESSION); 
	}

	/*
	 * Tests that calling addBag() and then adding the bag to the bagging area does not result in
	 * any issues.
	 * 
	 * Expected behavior: the expected weight of session is updated
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
		scs.baggingArea.addAnItem(bag);
				
		// check the expected weight has been updated (?)
				
		Mass expectedMassAfter = weight.getExpectedWeight();
				
		// compare the masses to see they have updated
		assertFalse(expectedMassAfter.compareTo(expectedMassBefore) == 0);
	}	
	
	/*
	 * Tests that calling addBag() and then adding the bag to the bagging area does not result in
	 * any issues.
	 * 
	 * Expected behavior: the expected weight of session is updated to include the weight of the bag
	 */
	@Test
	public void test_addBags_updatesExpectedWeightByBagWeight() {
		// start session:
		session.start();
				
		// save the expected Mass before adding the bag
		Mass expectedMassBefore = weight.getExpectedWeight();
				
		// call addBags
		session.addBags();
		
		// add the bags to the bagging area
		scs.baggingArea.addAnItem(bag);
				
		// check the expected weight has been updated (?)
				
		Mass expectedMassAfter = weight.getExpectedWeight();
		
		// compare the masses to see they have updated by the expected amount
		assertTrue(expectedMassAfter.compareTo(expectedMassBefore.sum(bagMass)) == 0);
	}

	
	/*
	 * Tests that calling addBag() during an active session and then changing the bagging area by
	 * removing something from the bagging area isnt registered as adding a bag successfully  
	 * 
	 * Expected behavior: the expected weight of session is not updated
	 */
	@Test
	public void test_addBags_unexpectedChange_doesntUpdateExpectedWeight() {
		// start session:
		session.start();
	
		// pre-test: add an item to the bagging area 
		weight.update(notBagMass); // sets the expected mass on the scale to already know about the bag
		scs.baggingArea.addAnItem(notBag);

		// save the expected Mass before adding the bag
		Mass expectedMassBefore = weight.getExpectedWeight();
		
		// call addBags
		session.addBags();
		
		// remove the not-bag from the bagging area
		scs.baggingArea.removeAnItem(notBag);
		
		
		// check the expected weight after the interaction				
		Mass expectedMassAfter = weight.getExpectedWeight();
						
		// compare the masses to see they have not updated
		assertTrue(expectedMassAfter.compareTo(expectedMassBefore) == 0);
	}

	/*
	 * Tests that calling addBag() during an active session and then changing the bagging area by
	 * removing something from the bagging area isnt registered as adding a bag successfully  
	 * 
	 * Expected behavior: the session is blocked (this is a weight discrepancy)
	 */
	@Test
	public void test_addBags_unexpectedChange_blocksSession() {
		// start session:
		session.start();
		
		// pre-test: add an item to the bagging area 
		weight.update(notBagMass); // sets the expected mass on the scale to already know about the bag

		scs.baggingArea.addAnItem(notBag);
					
		// call addBags
		session.addBags();
				
		// remove the not-bag from the bagging area
		scs.baggingArea.removeAnItem(notBag);
						
		// compare the masses to see they have not updated
		assertTrue(session.getState() == SessionState.BLOCKED);
	}

	/*
	 * Tests adding an item that is heavier than the set MAXBAGWEIGHT to the bagging area results in
	 * the session being blocked (Bags too Heavy use case)
	 * 
	 * Expected behavior: Session is blocked
	 */
	@Test
	public void test_addBags_overweightBag_blockSession() {
		// start session:
		session.start();

		// call addBags
		session.addBags();
		
		// add the heavy bag to the bagging area
		scs.baggingArea.addAnItem(overweightBag);
																
		// compare the masses to see they have not updated
		assertTrue(session.getState() == SessionState.BLOCKED);
	}
	/*
	 * Tests adding an item that is heavier than the set MAXBAGWEIGHT to the bagging area results in
	 * the expected weight not being updated (Bags too Heavy use case)
	 * 
	 * Expected behavior: expected weight is unchanged
	 */
	@Test
	public void test_addBags_overweightBag_doesntUpdateExpectedWeight() {
		// start session:
		session.start();
		
		// save the expected Mass before adding the bag
		Mass expectedMassBefore = weight.getExpectedWeight();
		
		// call addBags
		session.addBags();
		
		// add the heavy bag to the bagging area
		scs.baggingArea.addAnItem(overweightBag);
																
		// check the expected weight after the interaction				
		Mass expectedMassAfter = weight.getExpectedWeight();
								
		// compare the masses to see they have not updated
		assertTrue(expectedMassAfter.compareTo(expectedMassBefore) == 0);
				
	}
	/*
	 * Tests adding an item that is as heavy as the set MAXBAGWEIGHT to the bagging area results in
	 * the session being blocked (Bags too Heavy use case)
	 * 
	 * Expected behavior: Session is blocked
	 */
	@Test
	public void test_addBags_weightLimitBag_blockSession() {
		// start session:
		session.start();

		// call addBags
		session.addBags();
		
		// Add the bag to the bagging area
		scs.baggingArea.addAnItem(weightLimitBag);
																
		// compare the masses to see they have not updated
		assertTrue(session.getState() == SessionState.BLOCKED);
	}

	/*
	 * Tests adding an item that is as heavy as the set MAXBAGWEIGHT to the bagging area results in
	 * the expected weight staying the same
	 * 
	 * Expected behavior: expected weight is unchanged
	 */
	@Test
	public void test_addBags_weightLimitBag_expectedWeightIsNotUpdated() {
		// start session:
		session.start();

		// save the expected Mass before adding the bag
		Mass expectedMassBefore = weight.getExpectedWeight();
		
		// call addBags
		session.addBags();
		
		// add the heavy bag to the bagging area
		scs.baggingArea.addAnItem(weightLimitBag);
																
		// check the expected weight after the interaction				
		Mass expectedMassAfter = weight.getExpectedWeight();
								
		// compare the masses to see they have not updated
		assertTrue(expectedMassAfter.compareTo(expectedMassBefore) == 0);
	}
	
	
	// Customer indicates they want to add bag then cancels
	// not implemented

}
