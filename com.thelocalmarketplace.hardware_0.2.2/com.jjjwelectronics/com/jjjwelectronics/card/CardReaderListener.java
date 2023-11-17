package com.jjjwelectronics.card;

import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.card.Card.CardData;

/**
 * Listens for events emanating from the card reader.
 * 
 * @author JJJW Electronics LLP
 */
public interface CardReaderListener extends IDeviceListener {
	/**
	 * Announces that a card has swiped on the card reader.
	 */
	void aCardHasBeenSwiped();

	/**
	 * Announces that the data has been read from a card.
	 * 
	 * @param data
	 *            The data that was read. Note that this data may be corrupted.
	 */
	void theDataFromACardHasBeenRead(CardData data);
}
