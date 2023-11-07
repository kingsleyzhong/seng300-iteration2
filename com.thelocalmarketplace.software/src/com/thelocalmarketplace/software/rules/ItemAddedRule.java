package com.thelocalmarketplace.software.rules;

import java.util.Map;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodeScannerListener;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStation;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

/**
 * Rule for adding items to the session.
 * Takes the scanned item from the barcode scanner and adds it to the session
 * if the session is on and not frozen.
 * 
 * In the case that a session is frozen or not on, an InvalidActionException will be called.
 * 
 * Project iteration group members:
 * 		Ayman Momin 		: 30192494
 * 		Emily Kiddle 		: 30122331
 * 		Fardin Rahman Sami 	: 30172916
 * 		Kaylee Xiao 		: 30173778
 * 		Tamanna Kaur 		: 30170920
 * 		YiPing Zhang 		: 30127823
 */
public class ItemAddedRule {
	private Session session;
    
	/**
	 * Basic constructor for ItemAddedRule
	 * 
	 * @param scs
	 * 			The selfCheckoutStation in which to be installed
	 * @param session
	 * 			The session in which to add to
	 */
	public ItemAddedRule(SelfCheckoutStation scs, Session session) {
		if (scs == null) {
			throw new InvalidArgumentSimulationException("Self Checkout Station cannot be null.");
		}
		this.session = session;
		scs.scanner.register(new innerListener());
	}

	/**
	 * An innerListener class that listens to BarcodeScannerListener.
	 * If a barcode has been scanned add item to the session
	 */
	public class innerListener implements BarcodeScannerListener {
        @Override
        public void aBarcodeHasBeenScanned(IBarcodeScanner barcodeScanner, Barcode barcode) {	
        	if (session.isOn() && !session.isFrozen()) {
       			Map<Barcode, BarcodedProduct> database = ProductDatabases.BARCODED_PRODUCT_DATABASE;
       			
       			// Checks if product is in database. Does nothing if not in database.
       			if(database.containsKey(barcode)) {
       				BarcodedProduct product = database.get(barcode);
       				session.addItem(product);	
       			}
       			else {
       				throw new InvalidArgumentSimulationException("Not in database");
       			}
       		}
        	else {
        		throw new InvalidActionException("Session is not on or is frozen");
        	}
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
	
	}
}
