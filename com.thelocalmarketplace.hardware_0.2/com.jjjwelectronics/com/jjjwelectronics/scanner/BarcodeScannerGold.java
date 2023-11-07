package com.jjjwelectronics.scanner;

/**
 * A complex device hidden behind a simple simulation. They can scan and that is
 * about all.
 * <p>
 * Our premium model: it is extremely unlikely to fail to scan a barcode.
 * 
 * @author JJJW Electronics LLP
 */
public class BarcodeScannerGold extends AbstractBarcodeScanner {
	/**
	 * Create a barcode scanner.
	 */
	public BarcodeScannerGold() {
		probabilityOfFailedScan = 0;
	}
}
