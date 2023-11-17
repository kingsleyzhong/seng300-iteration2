package com.jjjwelectronics.card;

/**
 * Represents the card reader, capable of tap, chip insert, and swipe. Either
 * the reader or the card may fail, or the data read in can be corrupted, with
 * varying probabilities.
 * <P>
 * As a more economical model than Gold, our Silver model can give false
 * readings sometimes but with a low probability.
 * 
 * @author JJJW Electronics LLP
 */
public class CardReaderSilver extends AbstractCardReader {
	/**
	 * Basic constructor.
	 */
	public CardReaderSilver() {
		probabilityOfSwipeFailure = 0.1;
	}
}
