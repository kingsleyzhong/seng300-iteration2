package com.jjjwelectronics.printer;

import java.util.Random;

import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.OverloadedDevice;

/**
 * Represents printers used for printing receipts. A printer has a finite amount
 * of paper (measured in lines that can be printed) and ink (measured in
 * characters that can be printed).
 * <p>
 * This model of printers supports directly reading the quantity of ink or paper
 * remaining in the device, though inaccurately so.
 * <p>
 * Since this is a simulation, each character is assumed to require the same
 * amount of ink (except blanks and newlines) and the font size is fixed.
 * </p>
 * 
 * @author JJJW Electronics LLP
 */
public class ReceiptPrinterSilver extends AbstractReceiptPrinter {
	private static final Random pseudorandomNumberGenerator = new Random();

	/**
	 * Creates a receipt printer.
	 */
	public ReceiptPrinterSilver() {
		paperCount = 0;
		inkCount = 0;
	}

	@Override
	public synchronized void print(char c) throws EmptyDevice, OverloadedDevice {
		super.print(c);

		// We'll guess that a line has about 10 characters on it and 7 printing
		// characters, so there is a 10%
		// probability that a line is used and a 70% probability that a unit of ink has
		// been used.
		paperCount -= pseudorandomNumberGenerator.nextInt(100) > 90 ? 1 : 0;
		inkCount -= pseudorandomNumberGenerator.nextInt(100) > 70 ? 1 : 0;
	}

	@Override
	public synchronized void addInk(int quantity) throws OverloadedDevice {
		super.addInk(quantity);
		inkCount += quantity;
	}

	@Override
	public synchronized void addPaper(int units) throws OverloadedDevice {
		super.addPaper(units);
		paperCount += units;
	}

	private int paperCount;
	private int inkCount;

	@Override
	public int paperRemaining() {
		return paperCount;
	}

	@Override
	public int inkRemaining() {
		return inkCount;
	}
}
