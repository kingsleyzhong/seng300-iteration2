package com.thelocalmarketplace.hardware;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

/**
 * Represents items for sale, each with a particular price-lookup code and weight.
 */
public class PLUCodedItem extends Item {
	private PriceLookUpCode pluCode;

	/**
	 * Basic constructor.
	 * 
	 * @param pluCode
	 *            The PLU code representing the identifier of the product of which
	 *            this is an item.
	 * @param mass
	 *            The actual mass of the item.
	 */
	public PLUCodedItem(PriceLookUpCode pluCode, Mass mass) {
		super(mass);
		
		if(pluCode == null)
			throw new NullPointerSimulationException("pluCode");

		this.pluCode = pluCode;
	}

	/**
	 * Gets the PLU code of this item.
	 * 
	 * @return The PLU code.
	 */
	public PriceLookUpCode getPLUCode() {
		return pluCode;
	}
}
