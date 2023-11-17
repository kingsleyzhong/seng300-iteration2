package com.jjjwelectronics.printer;

/**
 * Represents printers used for printing receipts. A printer has a finite amount
 * of paper (measured in lines that can be printed) and ink (measured in
 * characters that can be printed).
 * <p>
 * This model of printers does not support directly reading the quantity of ink
 * or paper remaining in the device.
 * <p>
 * Since this is a simulation, each character is assumed to require the same
 * amount of ink (except blanks and newlines) and the font size is fixed.
 * </p>
 * 
 * @author JJJW Electronics LLP
 */
public class ReceiptPrinterBronze extends AbstractReceiptPrinter {
	/**
	 * Creates a receipt printer.
	 */
	public ReceiptPrinterBronze() {}

	@Override
	protected void notifyLowInk() {}

	@Override
	protected void notifyLowPaper() {}

	@Override
	public int paperRemaining() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int inkRemaining() {
		throw new UnsupportedOperationException();
	}
}
