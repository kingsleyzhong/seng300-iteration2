package com.jjjwelectronics.printer;

import java.util.Random;

/**
 * Represents printers used for printing receipts. A printer has a finite amount
 * of paper (measured in lines that can be printed) and ink (measured in
 * characters that can be printed).
 * <p>
 * As our premium model of printers, it supports directly reading the quantity
 * of ink or paper remaining in the device.
 * <p>
 * Since this is a simulation, each character is assumed to require the same
 * amount of ink (except blanks and newlines) and the font size is fixed.
 * </p>
 * 
 * @author JJJW Electronics LLP
 */
public class ReceiptPrinterGold extends AbstractReceiptPrinter {
	private static final Random pseudorandomNumberGenerator = new Random();

	/**
	 * Creates a receipt printer.
	 */
	public ReceiptPrinterGold() {
		super();
	}

	@Override
	public int paperRemaining() {
		return linesOfPaperRemaining;
	}

	@Override
	public int inkRemaining() {
		return charactersOfInkRemaining;
	}
}
