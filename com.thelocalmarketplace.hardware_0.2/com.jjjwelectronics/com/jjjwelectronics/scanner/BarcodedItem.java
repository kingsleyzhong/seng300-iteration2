package com.jjjwelectronics.scanner;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

/**
 * Represents items for sale, each with a particular barcode and weight.
 * 
 * @author JJJW Electronics LLP
 */
public class BarcodedItem extends Item {
	private Barcode barcode;

	/**
	 * Basic constructor.
	 * 
	 * @param barcode
	 *            The barcode representing the identifier of the product of which
	 *            this is an item.
	 * @param mass
	 *            The real mass of the item.
	 * @throws NullPointerSimulationException
	 *             If the mass or barcode is null.
	 * @throws InvalidArgumentSimulationException
	 *             If the mass is &le; 0.
	 */
	public BarcodedItem(Barcode barcode, Mass mass) {
		super(mass);

		if(barcode == null)
			throw new NullPointerSimulationException("barcode");
		
		if(mass.compareTo(Mass.ZERO) <=0)
			throw new InvalidArgumentSimulationException("The mass must be greater than zero.");

		this.barcode = barcode;
	}

	/**
	 * Gets the barcode of this item.
	 * 
	 * @return The barcode.
	 */
	public Barcode getBarcode() {
		return barcode;
	}
}
