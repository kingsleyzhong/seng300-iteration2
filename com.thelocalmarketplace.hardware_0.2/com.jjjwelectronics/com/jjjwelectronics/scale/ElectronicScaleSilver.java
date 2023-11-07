package com.jjjwelectronics.scale;

import java.math.BigInteger;

import com.jjjwelectronics.Mass;

/**
 * Represents electronic scales on which can be placed one or more items. Scales
 * have a sensitivity: changes to the mass smaller than this will go unnoticed.
 * Scales have a mass limit as well; when the items on the scale are heavier
 * than this limit, the scale cannot operate.
 * <p>
 * As a more economical option than Gold, our Silver level model provides a
 * 1&nbsp;g sensitivity and a mass limit of 10 kg.
 * 
 * @author JJJW Electronics LLP
 */
public class ElectronicScaleSilver extends AbstractElectronicScale {
	private static final Mass MASS_LIMIT = new Mass(new BigInteger("10000000000")); // 10 kg
	private static final Mass SENSITIVITY = new Mass(BigInteger.valueOf(Mass.MICROGRAMS_PER_GRAM)); // 1 g

	/**
	 * Basic constructor.
	 */
	public ElectronicScaleSilver() {
		super(MASS_LIMIT, SENSITIVITY);
	}
}
