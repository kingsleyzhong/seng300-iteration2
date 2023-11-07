package com.jjjwelectronics.printer;

import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.OverloadedDevice;

import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Represents receipt printers without specifying the implementation details.
 */
public interface IReceiptPrinter extends IDevice<ReceiptPrinterListener> {
	/**
	 * Prints a single character to the receipt. Whitespace characters are ignored,
	 * with the exception of ' ' (blank) and '\n', which signals to move to the
	 * start of the next line. Requires power.
	 * 
	 * @param c
	 *            The character to print.
	 * @throws EmptyDevice
	 *             If there is no ink or no paper in the printer.
	 * @throws OverloadedDevice
	 *             If the extra character would spill off the end of the line.
	 */
	void print(char c) throws EmptyDevice, OverloadedDevice;

	/**
	 * The receipt is finished printing, so cut it so that the customer can easily
	 * remove it. Failure to cut the paper means that the receipt will not be
	 * retrievable by the customer. Requires power.
	 */
	void cutPaper();

	/**
	 * Simulates the customer removing the receipt. Failure to cut the receipt
	 * first, or to always remove the receipt means that the customer will end up
	 * with other customers' receipts too! Does not require power.
	 * 
	 * @return The receipt if it has been cut; otherwise, null.
	 */
	String removeReceipt();

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
	void addInk(int quantity) throws OverloadedDevice;

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
	void addPaper(int units) throws OverloadedDevice;

	/**
	 * Detects how many lines of paper remain in this device. Not all models support
	 * this functionality. Some models estimate this value.
	 * 
	 * @return The number of lines of paper remaining in this device or an
	 *             approximation thereto.
	 */
	int paperRemaining();

	/**
	 * Detects how many units of ink remain in this device. Not all models support
	 * this functionality. Some models estimate this value.
	 * 
	 * @return The units of ink remaining in this device or an approximation
	 *             thereto.
	 */
	int inkRemaining();
}
