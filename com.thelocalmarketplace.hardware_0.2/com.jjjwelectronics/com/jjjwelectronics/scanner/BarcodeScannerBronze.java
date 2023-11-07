package com.jjjwelectronics.scanner;

/**
 * A complex device hidden behind a simple simulation. They can scan and that is
 * about all.
 * <p>
 * This is our base model.  It gets the job done but it requires frequently repeated scans before success.
 * 
 * @author JJJW Electronics LLP
 */
public class BarcodeScannerBronze extends AbstractBarcodeScanner {
	/**
	 * Create a barcode scanner.
	 */
	public BarcodeScannerBronze() {
		probabilityOfFailedScan = 25;
	}
}
