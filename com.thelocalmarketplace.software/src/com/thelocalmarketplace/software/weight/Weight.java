package com.thelocalmarketplace.software.weight;

import java.math.BigInteger;
import java.util.ArrayList;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Mass.MassDifference;
import com.jjjwelectronics.scale.ElectronicScaleListener;
import com.jjjwelectronics.scale.IElectronicScale;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

/**
 * Tracks the weight of the system. Contains an expected weight, which contains the weight of all the
 * products in the session. And contains an actual weight, which is the weight on the scale.
 * 
 * Project iteration 2 group members:
 * 		Aj Sallh 				: 30023811
 *		Anthony Kostal-Vazquez 	: 30048301
 *		Chloe Robitaille 		: 30022887
 *		Dvij Raval				: 30024340
 *		Emily Kiddle 			: 30122331
 *		Katelan NG 				: 30144672
 *		Kingsley Zhong 			: 30197260
 *		Nick McCamis 			: 30192610
 *		Sua Lim 				: 30177039
 *		Subeg CHAHAL 			: 30196531
 */

public class Weight {
	
	public ArrayList<WeightListener> listeners = new ArrayList<>();
	private Mass actualWeight=Mass.ZERO; //Actual Current weight on scale,set to zero in default
	private Mass expectedWeight=Mass.ZERO; //Expected weight according to added items, set to zero in default
	private boolean isDiscrepancy= false; //Flag of weight Discrepancy, set to false in default

	/**
	 * Basic constructors for weight class
	 * 
	 * @param scs
	 * 			The self-checkout station in which the weight shall be registered to
	 */ 
	public Weight(SelfCheckoutStationBronze scs) {
		scs.baggingArea.register(new innerListener());
	}
	
	public Weight(SelfCheckoutStationSilver scs) {
		scs.baggingArea.register(new innerListener());
	}
	
	public Weight(SelfCheckoutStationGold scs) {
		scs.baggingArea.register(new innerListener());
	}
	
	/*
	 * This method will update expected weight by accumulating the weight of scanned item from parameter
	 * After each time this method been called, it will run checkDiscrepancy()
	 */
	public void update(Mass mass){
		this.expectedWeight = this.expectedWeight.sum(mass);
		checkDiscrepancy();
	}
	
	public void removeItemWeightUpdate(Mass mass) {
		//Figure this part out 
	}
	/*
	 * This method checks if there is a Discrepancy between expectedWeight and actualWeight.
	 * if two values are equal and isDiscrepancy is true, then call DisrepancyFixed() on it's listener and set isDiscrepancy False
	 * if two value are not equal, call notifyDiscrepancy on it's listeners.
	 * Still need to figure out how to set a range of Mass as effective error
	 */
	public void checkDiscrepancy(){
	    if (!expectedWeight.equals(actualWeight)) {
	    		isDiscrepancy = true;
	            for(WeightListener l : listeners) {
	                l.notifyDiscrepancy();
	            }
	    } else{
	    	if (isDiscrepancy) {
		        isDiscrepancy = false;
		        for(WeightListener l : listeners) {
		            l.notifyDiscrepancyFixed(); }}
	    }
	}
	

	public class innerListener implements ElectronicScaleListener {

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
		public void theMassOnTheScaleHasChanged(IElectronicScale scale, Mass mass) {
			actualWeight = mass;
			checkDiscrepancy();
			
		}

		@Override
		public void theMassOnTheScaleHasExceededItsLimit(IElectronicScale scale) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void theMassOnTheScaleNoLongerExceedsItsLimit(IElectronicScale scale) {
			// TODO Auto-generated method stub
			
		}
	}
	/**
	 * Gets the expectedWeight of the system
	 * 
	 * @return
	 * 			The expected weight as Mass of the system
	 */
	public Mass getExpectedWeight() {
		return expectedWeight;
	}
	
	/**
	 * Gets the actualWeight of the system
	 * 
	 * @return
	 * 			The actual weight currently on the scale
	 */
	public Mass getActualWeight() {
		return actualWeight;
	}
	
	/**
	 * Gets the value of if there is a discrepancy or not
	 * 
	 * @return
	 * 			True if there is a discrepancy, false if no discrepancy
	 */
	public boolean isDiscrepancy() {
		return isDiscrepancy;
	}
	
	//register listeners
	public final synchronized void register(WeightListener listener) {
		if(listener == null)
			throw new NullPointerSimulationException("listener");

		listeners.add(listener);
	}
	
	//de-register listeners
	public final synchronized void deRegister(WeightListener listener) {
		if(listener == null)
			throw new NullPointerSimulationException("listener");

		listeners.remove(listener);
	}
	
}
