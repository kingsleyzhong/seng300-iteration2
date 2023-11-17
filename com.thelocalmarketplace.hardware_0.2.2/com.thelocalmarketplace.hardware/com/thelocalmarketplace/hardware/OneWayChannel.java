package com.thelocalmarketplace.hardware;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.Sink;

/**
 * Represents a simple device (like, say, a tube) that allows things to move in
 * one direction between other devices.
 * 
 * @param <T>
 *            The type of the things to be transported.
 */
final class OneWayChannel<T> implements Sink<T> {
	private Sink<T> sink;

	/**
	 * Constructs a new coin channel whose output is connected to the indicated
	 * sink.
	 * 
	 * @param sink
	 *            The device at the output end of the channel.
	 */
	public OneWayChannel(Sink<T> sink) {
		this.sink = sink;
	}

	/**
	 * Moves the indicated thing to the sink. This method should be called by the
	 * source device, and not by an external application.
	 * 
	 * @param thing
	 *            The thing to transport via the channel.
	 * @throws CashOverloadException
	 *             If the sink has no space for the thing.
	 * @throws DisabledException
	 *             If the sink is currently disabled.
	 */
	public synchronized void receive(T thing) throws CashOverloadException, DisabledException {
		sink.receive(thing);
	}

	/**
	 * Returns whether the sink has space for at least one more thing.
	 * 
	 * @return true if the channel can accept a thing; false otherwise.
	 */
	public synchronized boolean hasSpace() {
		return sink.hasSpace();
	}
}
