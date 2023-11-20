package com.jjjwelectronics.card;

/**
 * Represents the card reader, capable of tap, chip insert, and swipe. Either
 * the reader or the card may fail, or the data read in can be corrupted, with
 * varying probabilities.
 * <p>
 * As our premium model, it is extremely unlikely to give false readings.
 * 
 * @author JJJW Electronics LLP
 */
public class CardReaderGold extends AbstractCardReader {
	/**
	 * Basic constructor.
	 */
	public CardReaderGold() {
		probabilityOfSwipeFailure = 0;
	}
}
