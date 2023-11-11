package com.thelocalmarketplace.software.test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.software.weight.Weight;
import com.thelocalmarketplace.software.weight.WeightListener;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import powerutility.PowerGrid;

/*	* Unit Test for Weight Class
 * 
 * 
 *  * Project iteration group members:
 * 		
 */
public class WeightTest {
	private Weight weight;
    private TestWeightListener weightListener;
	private SelfCheckoutStationBronze scs;
	
	/*
	 * Stub for TestWeightListerner
	 */
	private class TestWeightListener implements WeightListener {
        boolean discrepancyNotified = false;
        boolean discrepancyFixed = false;

        @Override
        public void notifyDiscrepancy() {
            discrepancyNotified = true;
            discrepancyFixed = false;
        }

        
        @Override
        public void notifyDiscrepancyFixed() {
            discrepancyFixed = true;
            discrepancyNotified = false;
        }
    }
	
	
    @Before
    public void setUp() {
    	scs = new SelfCheckoutStationBronze();
		scs.plugIn(PowerGrid.instance());
		scs.turnOn();
		weight = new Weight(scs);
		weightListener = new TestWeightListener();
		weight.register(weightListener);
    }
	

	@Test
    public void testUpdateWithOneItem() {
		Mass mass1 = new Mass(2.0);
		weight.update(mass1);
		Mass expected = mass1;
		Mass actual = weight.getExpectedWeight();
		assertEquals("The expected Mass should be updated to 2.0", expected, actual);
	}
        

	@Test
    public void testUpdateWithTwoItems() {
		Mass mass1 = new Mass(2.0);
		Mass mass2 = new Mass(200.0);
		weight.update(mass1);
		weight.update(mass2);
		Mass expected = mass1.sum(mass2);
		Mass actual = weight.getExpectedWeight();
		assertEquals("The expected Mass should be updated to 202.0", expected, actual);;
	}

		@Test
		public void testTheMassOnTheScaleHasChangedUpdatesActualWeight() {
	    Mass newMass = new Mass(10.0);
	    Barcode barcode = new Barcode(new Numeral[] {Numeral.valueOf((byte) 1)});
	    Item item = new BarcodedItem(barcode, new Mass(10.0));
	    scs.baggingArea.addAnItem(item);; // Simulate the scale reporting a new mass
	    assertEquals("Actual weight should be updated to the new mass.", newMass, weight.getActualWeight());
	}	
		
		@Test
		public void testCheckDiscrepancyWithDifferentWeights() {
        weight.update(new Mass(100.0)); // Set expected weight to 100
        Barcode barcode = new Barcode(new Numeral[] {Numeral.valueOf((byte) 1)});
	    Item item = new BarcodedItem(barcode, new Mass(150.0));
	    scs.baggingArea.addAnItem(item);; // Simulate the scale reporting a new mass

        assertTrue("Discrepancy should be notified.", weightListener.discrepancyNotified);
        assertTrue("Discrepancy flag should be true.", weight.isDiscrepancy());
    }

 	@Test
    public void testCheckDiscrepancyWithSameWeights() {
        weight.update(new Mass(100.0)); // Set expected weight to 100
        Barcode barcode = new Barcode(new Numeral[] {Numeral.valueOf((byte) 1)});
	    Item item = new BarcodedItem(barcode, new Mass(100.0));
	    scs.baggingArea.addAnItem(item);; // Simulate the scale reporting a new mass
        //assertFalse("Discrepancy should not be notified.", weightListener.discrepancyNotified);
        assertFalse("Discrepancy flag should be false.", weight.isDiscrepancy());
    }
	 
	@Test
    public void testCheckDiscrepancyFixed() {
        weight.update(new Mass(100.0)); // Set expected weight to 100
        Barcode barcode = new Barcode(new Numeral[] {Numeral.valueOf((byte) 1)});
	    Item item = new BarcodedItem(barcode, new Mass(50.0));
	    scs.baggingArea.addAnItem(item);; // Simulate the scale reporting a new mass
	    Item item2 = new BarcodedItem(barcode, new Mass(50.0));
	    scs.baggingArea.addAnItem(item2);
        //assertTrue("Discrepancy fixed should be notified.", weightListener.discrepancyFixed);
        assertFalse("Discrepancy flag should be false.", weight.isDiscrepancy());
    }

    @Test(expected = NullPointerSimulationException.class)
    public void testRegisterNullListener() {
        weight.register(null);
    }

    @Test(expected = NullPointerSimulationException.class)
    public void testDeRegisterNullListener() {
        weight.deRegister(null);
    }

    @Test
    public void testDeRegisterListener() {
        weight.deRegister(weightListener);
        assertFalse("Listener should be removed.", weight.listeners.contains(weightListener));
    }   
}

