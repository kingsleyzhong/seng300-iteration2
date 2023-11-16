package com.thelocalmarketplace.software.receipt;

public interface PrintReceiptListener {

	/**
	 * Signals an event that the printer is out of paper
	 */
	void notifiyOutOfPaper();
	
	/**
	 * Signals an event that the printer is out of ink
	 */
	void notifiyOutOfInk();
	
	/**
	 * Signals an event that the printer paper has been refilled
	 */
	void notifiyPaperRefilled();
	
	/**
	 * Signals an event that the ink has been refilled
	 */
	void notifiyInkRefilled();
	
	/**
	 * Signals that the receipt was successfully printed 
	 */
	void notifiyReceiptPrinted();

}
