package com.jjjwelectronics;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

/**
 * Abstract base class of items for sale, each with a particular mass.
 * 
 * @author JJJW Electronics LLP
 */
public abstract class Item {
	private Mass mass;

	/**
	 * Constructs an item with the indicated mass.
	 * 
	 * @param mass
	 *            The mass of the item. Cannot be null.
	 */
	protected Item(Mass mass) {
		if(mass == null)
			throw new NullPointerSimulationException("The mass cannot be non-existent.");

		if(mass.compareTo(Mass.ZERO) <= 0)
			throw new InvalidArgumentSimulationException("The mass must be positive.");
		
		this.mass = mass;
	}

	/**
	 * Reads the mass of the item.
	 * 
	 * @return The mass.
	 */
	public Mass getMass() {
		return mass;
	}
}
