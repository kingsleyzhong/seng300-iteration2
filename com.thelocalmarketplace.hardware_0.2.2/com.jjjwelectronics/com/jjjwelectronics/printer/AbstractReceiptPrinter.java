package com.jjjwelectronics.printer;

import com.jjjwelectronics.AbstractDevice;
import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.OverloadedDevice;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;

/**
 * Abstract base class of printers used for printing receipts. A printer has a
 * finite amount of paper (measured in lines that can be printed) and ink
 * (measured in characters that can be printed).
 * <p>
 * Since this is a simulation, each character is assumed to require the same
 * amount of ink (except blanks and newlines) and the font size is fixed.
 * </p>
 * 
 * @author JJJW Electronics LLP
 */
abstract class AbstractReceiptPrinter extends AbstractDevice<ReceiptPrinterListener> implements IReceiptPrinter {
	/**
	 * Represents the maximum amount of ink that the printer can hold, measured in
	 * printable-character units.
	 */
	public static final int MAXIMUM_INK = 1 << 20;
	/**
	 * Represents the maximum amount of paper that the printer can hold, measured in
	 * lines.
	 */
	public static final int MAXIMUM_PAPER = 1 << 10;
	protected int charactersOfInkRemaining = 0;
	protected int linesOfPaperRemaining = 0;
	private StringBuilder sb = new StringBuilder();
	private int charactersOnCurrentLine = 0;
	/**
	 * Represents the maximum number of characters that can fit on one line of the
	 * receipt. This is a simulation, so the font is assumed monospaced and of fixed
	 * size.
	 */
	public static final int CHARACTERS_PER_LINE = 60;
	private String lastReceipt = null;

	/**
	 * Prints a single character to the receipt. Whitespace characters are ignored,
	 * with the exception of ' ' (blank) and '\n', which signals to move to the
	 * start of the next line. If the printer is out of ink, announces "outOfInk"
	 * event; otherwise, if it has less than 10% of its maximum ink, announces
	 * "lowInk" event. If the printer is out of paper, announces "outOfPaper" event;
	 * otherwise, if it has less than 10% of its maximum paper, announces "lowPaper"
	 * event. Requires power.
	 * 
	 * @param c
	 *            The character to print.
	 * @throws EmptyDevice
	 *             If there is no ink or no paper in the printer.
	 * @throws OverloadedDevice
	 *             If the extra character would spill off the end of the line.
	 */
	@Override
	public synchronized void print(char c) throws EmptyDevice, OverloadedDevice {
		if(!isPoweredUp())
			throw new NoPowerException();

		if(c == '\n') {
			--linesOfPaperRemaining;
			charactersOnCurrentLine = 0;
		}
		else if(c != ' ' && Character.isWhitespace(c))
			return;
		else if(charactersOnCurrentLine == CHARACTERS_PER_LINE)
			throw new OverloadedDevice("The line is too long. Add a newline");
		else if(linesOfPaperRemaining == 0)
			throw new EmptyDevice("There is no paper in the printer.");
		else
			charactersOnCurrentLine++;

		if(!Character.isWhitespace(c)) {
			if(charactersOfInkRemaining == 0)
				throw new EmptyDevice("There is no ink in the printer");

			charactersOfInkRemaining--;
		}

		sb.append(c);

		if(charactersOfInkRemaining == 0)
			notifyOutOfInk();
		else if(charactersOfInkRemaining <= MAXIMUM_INK * 0.1)
			notifyLowInk();

		if(linesOfPaperRemaining == 0)
			notifyOutOfPaper();
		else if(linesOfPaperRemaining <= MAXIMUM_PAPER * 0.1)
			notifyLowPaper();
	}

	/**
	 * The receipt is finished printing, so cut it so that the customer can easily
	 * remove it. Failure to cut the paper means that the receipt will not be
	 * retrievable by the customer. Requires power.
	 */
	@Override
	public synchronized void cutPaper() {
		if(!isPoweredUp())
			throw new NoPowerException();

		lastReceipt = sb.toString();
	}

	/**
	 * Simulates the customer removing the receipt. Failure to cut the receipt
	 * first, or to always remove the receipt means that the customer will end up
	 * with other customers' receipts too! Does not require power.
	 * 
	 * @return The receipt if it has been cut; otherwise, null.
	 */
	@Override
	public synchronized String removeReceipt() {
		String receipt = lastReceipt;

		if(lastReceipt != null) {
			lastReceipt = null;
			sb = new StringBuilder();
		}
		else
			throw new InvalidArgumentSimulationException("A non-existent receipt cannot be removed.");

		return receipt;
	}

	/**
	 * Adds ink to the printer. Simulates a human doing the adding. On success,
	 * announces "inkAdded" event. Requires power.
	 * 
	 * @param quantity
	 *            The quantity of characters-worth of ink to add.
	 * @throws SimulationException
	 *             If the quantity is negative.
	 * @throws OverloadedDevice
	 *             If the total of the existing ink plus the added quantity is
	 *             greater than the printer's capacity.
	 */
	@Override
	public synchronized void addInk(int quantity) throws OverloadedDevice {
		if(!isPoweredUp())
			throw new NoPowerException();

		if(quantity < 0)
			throw new InvalidArgumentSimulationException("Are you trying to remove ink?");

		if(charactersOfInkRemaining + quantity > MAXIMUM_INK)
			throw new OverloadedDevice("You spilled a bunch of ink!");

		if(quantity > 0) {
			charactersOfInkRemaining += quantity;
			notifyInkAdded();
		}
	}

	/**
	 * Adds paper to the printer. Simulates a human doing the adding. On success,
	 * announces "paperAdded" event. Requires power.
	 * 
	 * @param units
	 *            The quantity of lines-worth of paper to add.
	 * @throws SimulationException
	 *             If the quantity is negative.
	 * @throws OverloadedDevice
	 *             If the total of the existing paper plus the added quantity is
	 *             greater than the printer's capacity.
	 */
	@Override
	public synchronized void addPaper(int units) throws OverloadedDevice {
		if(!isPoweredUp())
			throw new NoPowerException();

		if(units < 0)
			throw new InvalidArgumentSimulationException("Are you trying to remove paper?");

		if(linesOfPaperRemaining + units > MAXIMUM_PAPER)
			throw new OverloadedDevice("You may have broken the printer, jamming so much in there!");

		if(units > 0) {
			linesOfPaperRemaining += units;
			notifyPaperAdded();
		}
	}

	protected void notifyOutOfInk() {
		for(ReceiptPrinterListener l : listeners())
			l.thePrinterIsOutOfInk();
	}

	protected void notifyInkAdded() {
		for(ReceiptPrinterListener l : listeners())
			l.inkHasBeenAddedToThePrinter();
	}

	protected void notifyOutOfPaper() {
		for(ReceiptPrinterListener l : listeners())
			l.thePrinterIsOutOfPaper();
	}

	protected void notifyPaperAdded() {
		for(ReceiptPrinterListener l : listeners())
			l.paperHasBeenAddedToThePrinter();
	}

	protected void notifyLowInk() {
		for(ReceiptPrinterListener l : listeners())
			l.thePrinterHasLowInk();
	}

	protected void notifyLowPaper() {
		for(ReceiptPrinterListener l : listeners())
			l.thePrinterHasLowPaper();
	}

}