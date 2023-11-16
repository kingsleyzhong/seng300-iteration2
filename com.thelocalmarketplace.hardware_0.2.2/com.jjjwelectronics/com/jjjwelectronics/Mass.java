package com.jjjwelectronics;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

/**
 * Represents the mass of an item, measured in micrograms by default.
 * 
 * @author JJJW Electronics LLP
 */
public class Mass {
	/**
	 * Represents how many micrograms there are in one gram.
	 */
	public static final int MICROGRAMS_PER_GRAM = 1_000_000;
	/**
	 * Represents a mass of zero.
	 */
	public static final Mass ZERO = new Mass(0);
	/**
	 * Represents a mass of one gram.
	 */
	public static final Mass ONE_GRAM = new Mass(MICROGRAMS_PER_GRAM);
	private BigInteger value;

	/**
	 * Constructor from BigInteger.
	 * 
	 * @param mass
	 *            The value of the mass, in micrograms. Cannot be null. Must be &gt;
	 *            0.
	 * @throws NullPointerSimulationException
	 *             If <code>mass</code> is null.
	 * @throws InvalidArgumentSimulationException
	 *             If <code>mass</code> &lt; 0.
	 */
	public Mass(BigInteger mass) {
		if(mass == null)
			throw new NullPointerSimulationException("The mass cannot be non-existent.");

		if(mass.compareTo(BigInteger.ZERO) < 0)
			throw new InvalidArgumentSimulationException("The mass cannot be negative.");

		value = mass;
	}

	/**
	 * Constructor from long.
	 * 
	 * @param mass
	 *            The value of the mass, in micrograms. Must be &gt; 0.
	 * @throws InvalidArgumentSimulationException
	 *             If <code>mass</code> &lt; 0.
	 */
	public Mass(long mass) {
		if(mass < 0)
			throw new InvalidArgumentSimulationException("The mass cannot be negative.");

		value = BigInteger.valueOf(mass);
	}

	/**
	 * Constructor from BigDecimal.
	 * 
	 * @param mass
	 *            The value of the mass, in grams. Cannot be null. Must be &gt;
	 *            0.
	 * @throws NullPointerSimulationException
	 *             If <code>mass</code> is null.
	 * @throws InvalidArgumentSimulationException
	 *             If <code>mass</code> &lt; 0.
	 */
	public Mass(BigDecimal mass) {
		if(mass == null)
			throw new NullPointerSimulationException("The mass cannot be non-existent.");

		if(mass.compareTo(BigDecimal.ZERO) < 0)
			throw new InvalidArgumentSimulationException("The mass cannot be negative.");

		value = mass.toBigInteger().multiply(BigInteger.valueOf(MICROGRAMS_PER_GRAM));
	}

	/**
	 * Constructor from double.
	 * 
	 * @param mass
	 *            The value of the mass, in grams. Must be &gt; 0.
	 * @throws InvalidArgumentSimulationException
	 *             If <code>mass</code> &lt; 0.
	 */
	public Mass(double mass) {
		this(BigDecimal.valueOf(mass));
	}

	/**
	 * Compares this mass with the specified mass. This method is provided in
	 * preference to individual methods for each of the six boolean comparison
	 * operators (<, ==,>, >=, !=, <=). The suggested idiom for performing these
	 * comparisons is: <code>(x.compareTo(y) <i>&lt;op&gt;</i> 0)</code>, where
	 * <code><i>&lt;op&gt;</i></code> is one of the six comparison operators.
	 * 
	 * @param other
	 *            The other mass to which this one is to be compared.
	 * @return -1, 0 or 1 as this Mass is numerically less than, equal to, or
	 *             greater than <code>other</code>.
	 * @throws NullPointerSimulationException
	 *             If <code>other</code> is null.
	 */
	public int compareTo(Mass other) {
		if(other == null)
			throw new NullPointerSimulationException("The mass cannot be non-existent.");

		return value.compareTo(other.value);
	}

	/**
	 * Obtains the difference between this mass and the indicated mass.
	 * 
	 * @param other
	 *            The other mass to compare this one against.
	 * @return The difference.
	 * @throws NullPointerSimulationException
	 *             If <code>other</code> is null.
	 */
	public MassDifference difference(Mass other) {
		if(other == null)
			throw new NullPointerSimulationException("The mass cannot be non-existent.");

		return new MassDifference(BigInteger.valueOf(value.longValue() - other.inMicrograms().longValue()));
	}

	/**
	 * Obtains the sum of this mass and the indicated mass.
	 * 
	 * @param other
	 *            The other mass to sum.
	 * @return The sum.
	 * @throws NullPointerSimulationException
	 *             If <code>other</code> is null.
	 */
	public Mass sum(Mass other) {
		if(other == null)
			throw new NullPointerSimulationException("The mass cannot be non-existent.");

		return new Mass(value.add(other.value));
	}

	/**
	 * Obtains the value of this mass in micrograms.
	 * 
	 * @return The value of this mass in micrograms.
	 */
	public BigInteger inMicrograms() {
		return value;
	}

	/**
	 * Obtains the value of this mass in micrograms.
	 * 
	 * @return The value of this mass in micrograms.
	 */
	public BigDecimal inGrams() {
		return new BigDecimal(value.divide(BigInteger.valueOf(MICROGRAMS_PER_GRAM)));
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Mass) {
			Mass mass = (Mass)obj;

			return value.equals(mass.value);
		}

		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public String toString() {
		DecimalFormat formatter = new DecimalFormat("#,###");
		return formatter.format(value) + " mcg";
	}

	/**
	 * Represents the difference between two masses.
	 */
	public static class MassDifference {
		BigInteger value;

		MassDifference(BigInteger value) {
			this.value = value;
		}

		/**
		 * Compares this difference against another mass.
		 * 
		 * @param mass
		 *            The other mass to compare against.
		 * @return +1 if this mass is greater; -1 if this mass is lesser; or 0 if the
		 *             two masses are equal.
		 * @throws NullPointerSimulationException
		 *             If the other mass is null.
		 */
		public int compareTo(Mass mass) {
			if(mass == null)
				throw new NullPointerSimulationException("The mass cannot be non-existent.");

			long other = mass.inMicrograms().longValue();
			long thisValue = value.longValue();

			if(thisValue > other)
				return 1;

			if(thisValue < other)
				return -1;

			return 0;
		}

		/**
		 * Determines the absolute value of this mass difference.
		 * 
		 * @return The absolute value.
		 */
		public Mass abs() {
			return new Mass(value.abs());
		}
	}
}
