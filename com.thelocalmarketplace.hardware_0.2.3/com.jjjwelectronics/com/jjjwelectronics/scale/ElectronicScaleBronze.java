package com.jjjwelectronics.scale;

import java.math.BigInteger;

import com.jjjwelectronics.Mass;

/**
 * Represents electronic scales on which can be placed one or more items. Scales
 * have a sensitivity: changes to the mass smaller than this will go unnoticed.
 * Scales have a mass limit as well; when the items on the scale are heavier
 * than this limit, the scale cannot operate.
 * <p>
 * This is our base model. It provides a 5&nbsp;g sensitivity and a mass limit of 1 kg.
 * 
 * @author JJJW Electronics LLP
 */
public class ElectronicScaleBronze extends AbstractElectronicScale {
	private static final Mass MASS_LIMIT = new Mass(BigInteger.valueOf(1_000 * Mass.MICROGRAMS_PER_GRAM)); // 1 kg
	private static final Mass SENSITIVITY = new Mass(BigInteger.valueOf(5 * Mass.MICROGRAMS_PER_GRAM)); // 5 g

	/**
	 * Basic constructor.
	 */
	public ElectronicScaleBronze() {
		super(MASS_LIMIT, SENSITIVITY);
	}
}
