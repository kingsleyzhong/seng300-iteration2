package com.jjjwelectronics.card;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import com.jjjwelectronics.AbstractDevice;
import com.jjjwelectronics.card.Card.CardData;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import powerutility.NoPowerException;

/**
 * Abstract base class for card readers.
 * 
 * @author JJJW Electronics LLP
 */
public abstract class AbstractCardReader extends AbstractDevice<CardReaderListener> implements ICardReader {
	protected boolean cardIsInserted = false;
	protected static final ThreadLocalRandom random = ThreadLocalRandom.current();
	protected double probabilityOfSwipeFailure = 0.5;

	@Override
	public synchronized CardData swipe(Card card) throws IOException {
		if(!isPoweredUp())
			throw new NoPowerException();
	
		notifyCardSwiped();
	
		if(random.nextDouble(0.0, 1.0) > probabilityOfSwipeFailure) {
			CardData data = card.swipe();
	
			notifyCardDataRead(data);
	
			return data;
		}
	
		throw new MagneticStripeFailureException();
	}

	protected void notifyCardSwiped() {
		for(CardReaderListener l : listeners())
			l.aCardHasBeenSwiped();
	}

	protected void notifyCardDataRead(CardData data) {
		for(CardReaderListener l : listeners())
			l.theDataFromACardHasBeenRead(data);
	}
}