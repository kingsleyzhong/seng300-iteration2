package com.jjjwelectronics.card;

import java.io.IOException;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.card.Card.CardData;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Abstract base type of card readers.
 * 
 * @author JJJW Electronics LLP
 */
public interface ICardReader extends IDevice<CardReaderListener> {
	/**
	 * Swipe the card. Requires power.
	 * 
	 * @param card
	 *            The card to swipe.
	 * @return The card data.
	 * @throws IOException
	 *             If the swipe failed.
	 */
	CardData swipe(Card card) throws IOException;
}
