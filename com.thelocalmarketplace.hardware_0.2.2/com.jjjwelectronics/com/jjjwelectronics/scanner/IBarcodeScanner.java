package com.jjjwelectronics.scanner;

import com.jjjwelectronics.IDevice;

/**
 * Abstract base type for barcode scanners.
 * 
 * @author JJJW Electronics LLP
 */
public interface IBarcodeScanner extends IDevice<BarcodeScannerListener> {
	/**
	 * Simulates the customer's action of scanning an item. The result of the scan
	 * is only announced to any registered observers. Requires power.
	 * 
	 * @param item
	 *            The item to scan. Of course, it will only work if the item has a
	 *            barcode, and maybe not even then.
	 */
	void scan(BarcodedItem item);
}