package com.jjjwelectronics.printer;

import com.jjjwelectronics.IDeviceListener;

/**
 * Listens for events emanating from the receipt printer.
 * 
 * @author JJJW Electronics LLP
 */
public interface ReceiptPrinterListener extends IDeviceListener {
	/**
	 * Announces that the printer is out of paper.
	 */
	void thePrinterIsOutOfPaper();

	/**
	 * Announces that the printer is out of ink.
	 */
	void thePrinterIsOutOfInk();

	/**
	 * Announces that the printer is low on ink.
	 */
	void thePrinterHasLowInk();

	/**
	 * Announces that the printer is low on paper.
	 */
	void thePrinterHasLowPaper();

	/**
	 * Announces that paper has been added to the printer.
	 */
	void paperHasBeenAddedToThePrinter();

	/**
	 * Announces that ink has been added to the printer.
	 */
	void inkHasBeenAddedToThePrinter();
}
