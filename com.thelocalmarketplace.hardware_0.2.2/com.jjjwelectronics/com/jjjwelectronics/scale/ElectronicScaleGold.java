package com.jjjwelectronics.scale;

import java.math.BigInteger;

import com.jjjwelectronics.Mass;

/**
 * Represents electronic scales on which can be placed one or more items. Scales
 * have a sensitivity: changes to the mass smaller than this will go unnoticed.
 * Scales have a mass limit as well; when the items on the scale are heavier
 * than this limit, the scale cannot operate.
 * <p>
 * Our premium model provides a 0.5&nbsp;g sensitivity and a mass limit of 100
 * kg.
 * 
 * @author JJJW Electronics LLP
 */
public class ElectronicScaleGold extends AbstractElectronicScale {
	private static final Mass MASS_LIMIT = new Mass(
		BigInteger.valueOf(Mass.MICROGRAMS_PER_GRAM).multiply(BigInteger.valueOf(100_000))); // 100 kg
	private static final Mass SENSITIVITY = new Mass(BigInteger.valueOf(Mass.MICROGRAMS_PER_GRAM / 10)); // 100 mg

	/**
	 * Basic constructor.
	 */
	public ElectronicScaleGold() {
		super(MASS_LIMIT, SENSITIVITY);
	}
}
