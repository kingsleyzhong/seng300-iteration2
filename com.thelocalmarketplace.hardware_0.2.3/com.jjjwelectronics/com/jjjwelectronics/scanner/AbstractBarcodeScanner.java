package com.jjjwelectronics.scanner;

import java.util.Random;

import com.jjjwelectronics.AbstractDevice;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import powerutility.NoPowerException;

/**
 * Abstract base class for barcode scanners.
 * 
 * @author JJJW Electronics LLP
 */
abstract class AbstractBarcodeScanner extends AbstractDevice<BarcodeScannerListener> implements IBarcodeScanner {
	private Random random = new Random();
	protected int probabilityOfFailedScan = 0; /* out of 100 */

	@Override
	public synchronized void scan(BarcodedItem item) {
		if(!isPoweredUp())
			throw new NoPowerException();
	
		if(item == null)
			throw new NullPointerSimulationException("item");
	
		if(isDisabled())
			return; // silently ignore
	
		if(random.nextInt(100) >= probabilityOfFailedScan)
			notifyBarcodeScanned(item.getBarcode());
	
		// else silently ignore it
	}

	protected void notifyBarcodeScanned(Barcode barcode) {
		for(BarcodeScannerListener l : listeners())
			l.aBarcodeHasBeenScanned(this, barcode);
	}
}
