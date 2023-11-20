package com.thelocalmarketplace.software.rules;

import java.util.Map;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodeScannerListener;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;

/**
 * Rule for adding items to the session.
 * Takes the scanned item from the barcode scanner and adds it to the session
 * if the session is on and not frozen.
 * 
 * In the case that a session is frozen or not on, an InvalidActionException
 * will be called.
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
public class ItemAddedRule {
	private Session session;

	/**
	 * Basic constructor for ItemAddedRule. Registers a listener to scanners being
	 * used in the
	 * self-checkout station.
	 * 
	 * @param scs
	 *                The selfCheckoutStation in which to be installed
	 * @param session
	 *                The session in which to add to
	 */
	public ItemAddedRule(AbstractSelfCheckoutStation scs, Session session) {
		if (scs == null) {
			throw new InvalidArgumentSimulationException("Self Checkout Station cannot be null.");
		}
		this.session = session;
		scs.mainScanner.register(new innerListener());
		scs.handheldScanner.register(new innerListener());
	}

	/**
	 * An innerListener class that listens to BarcodeScannerListener.
	 * If a barcode has been scanned add item to the session
	 */
	public class innerListener implements BarcodeScannerListener {
		@Override
		public void aBarcodeHasBeenScanned(IBarcodeScanner barcodeScanner, Barcode barcode) {
			if (Session.getState() == SessionState.IN_SESSION) {
				Map<Barcode, BarcodedProduct> database = ProductDatabases.BARCODED_PRODUCT_DATABASE;

				// Checks if product is in database. Throws exception if not in database.
				if (database.containsKey(barcode)) {
					BarcodedProduct product = database.get(barcode);
					session.addItem(product);
				} else {
					throw new InvalidArgumentSimulationException("Not in database");
				}
			} else {
				// silently ignore
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
