package com.jjjwelectronics.scanner;

import com.jjjwelectronics.IDeviceListener;

/**
 * Listens for events emanating from a barcode scanner.
 * 
 * @author JJJW Electronics LLP
 */
public interface BarcodeScannerListener extends IDeviceListener {
	/**
	 * An event announcing that the indicated barcode has been successfully scanned.
	 * 
	 * @param barcodeScanner
	 *            The device on which the event occurred.
	 * @param barcode
	 *            The barcode that was read by the scanner.
	 */
	void aBarcodeHasBeenScanned(IBarcodeScanner barcodeScanner, Barcode barcode);

}
