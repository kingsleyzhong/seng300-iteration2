package com.jjjwelectronics.card;

import java.io.IOException;
import java.util.Random;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Represents plastic cards (e.g., credit cards, debit cards, membership cards).
 * 
 * @author JJJW Electronics LLP
 */
public final class Card {
	/**
	 * The kind of card (Visa, Mastercard, etc.). Reading this field simulates
	 * visually reading the physical card.
	 */
	public final String kind;
	/**
	 * The account number of the card. Reading this field simulates visually reading
	 * the physical card.
	 */
	public final String number;
	/**
	 * The name of the account holder of the card. Reading this field simulates
	 * visually reading the physical card.
	 */
	public final String cardholder;
	/**
	 * The security code on the back of the card. Reading this field simulates
	 * visually reading the physical card.
	 */
	public final String cvv;
	private int failedTrials = 0;
	private boolean isBlocked;

	/**
	 * Create a card instance.
	 * 
	 * @param type
	 *            The type of the card.
	 * @param number
	 *            The number of the card. This has to be a string of digits.
	 * @param cardholder
	 *            The name of the cardholder.
	 * @param cvv
	 *            The card verification value (CVV), a 3- or 4-digit value often on
	 *            the back of the card. This can be null.
	 * @throws SimulationException
	 *             If type, number, or cardholder is null.
	 * @throws SimulationException
	 *             If hasChip is true but pin is null.
	 */
	public Card(String type, String number, String cardholder, String cvv) {
		if(type == null)
			throw new NullPointerSimulationException("type");

		if(number == null)
			throw new NullPointerSimulationException("number");

		if(cardholder == null)
			throw new NullPointerSimulationException("cardholder");

		this.kind = type;
		this.number = number;
		this.cardholder = cardholder;
		this.cvv = cvv;
	}

	private static final Random random = new Random(0);
	private static final double PROBABILITY_OF_MAGNETIC_STRIPE_FAILURE = 0.01;
	private static final double PROBABILITY_OF_TAP_FAILURE = 0.005;
	private static final double PROBABILITY_OF_INSERT_FAILURE = 0.001;
	private static final double PROBABILITY_OF_MAGNETIC_STRIPE_CORRUPTION = 0.001;
	private static final double PROBABILITY_OF_CHIP_CORRUPTION = 0.00001;

	/**
	 * Simulates the action of swiping the card.
	 * 
	 * @return The card data.
	 * @throws IOException
	 *             If anything went wrong with the data transfer.
	 */
	public final synchronized CardSwipeData swipe() throws IOException {
		if(isBlocked)
			throw new BlockedCardException();

		if(random.nextDouble() <= PROBABILITY_OF_MAGNETIC_STRIPE_FAILURE)
			throw new MagneticStripeFailureException();

		return new CardSwipeData();
	}

	private String randomize(String original, double probability) {
		if(random.nextDouble() <= probability) {
			int length = original.length();
			int index = random.nextInt(length);
			String first;

			if(index == 0)
				first = "";
			else
				first = original.substring(0, index);

			char second = original.charAt(index);
			second++;

			String third;

			if(index == length - 1)
				third = "";
			else
				third = original.substring(index + 1, length);

			return first + second + third;
		}

		return original;
	}

	/**
	 * The abstract base type of card data.
	 */
	public interface CardData {
		/**
		 * Gets the type of the card.
		 * 
		 * @return The type of the card.
		 */
		public String getType();

		/**
		 * Gets the number of the card.
		 * 
		 * @return The number of the card.
		 */
		public String getNumber();

		/**
		 * Gets the cardholder's name.
		 * 
		 * @return The cardholder's name.
		 */
		public String getCardholder();

		/**
		 * Gets the card verification value (CVV) of the card.
		 * 
		 * @return The CVV of the card.
		 * @throws UnsupportedOperationException
		 *             If this operation is unsupported by this object.
		 */
		public String getCVV();
	}

	/**
	 * The data from swiping a card.
	 */
	public class CardSwipeData implements CardData {
		@Override
		public String getType() {
			return randomize(kind, PROBABILITY_OF_MAGNETIC_STRIPE_CORRUPTION);
		}

		@Override
		public String getNumber() {
			return randomize(number, PROBABILITY_OF_MAGNETIC_STRIPE_CORRUPTION);
		}

		@Override
		public String getCardholder() {
			return randomize(cardholder, PROBABILITY_OF_MAGNETIC_STRIPE_CORRUPTION);
		}

		@Override
		public String getCVV() {
			throw new UnsupportedOperationException();
		}
	}
}
