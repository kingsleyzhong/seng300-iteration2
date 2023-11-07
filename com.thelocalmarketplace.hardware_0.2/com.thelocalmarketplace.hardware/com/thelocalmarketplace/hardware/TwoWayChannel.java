package com.thelocalmarketplace.hardware;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.PassiveSource;
import com.tdc.Sink;

/**
 * Represents a simple device (like, say, a tube or just a physical connection)
 * that moves things between other devices. This channel is bidirectional.
 * 
 * @param <T>
 *            The type of the things to move.
 */
final class TwoWayChannel<T> implements Sink<T>, PassiveSource<T> {
	private PassiveSource<T> source;
	private Sink<T> sink;

	/**
	 * Constructs a new channel whose input is connected to the indicated source and
	 * whose output is connected to the indicated sink.
	 * 
	 * @param source
	 *            The device at the output end of the channel.
	 * @param sink
	 *            The device at the output end of the channel.
	 */
	public TwoWayChannel(PassiveSource<T> source, Sink<T> sink) {
		this.source = source;
		this.sink = sink;
	}

	/**
	 * Moves the indicated thing to the source. This method should be called by the
	 * sink device, and not by an external application.
	 * 
	 * @param thing
	 *            The thing to transport via the channel.
	 * @throws CashOverloadException
	 *             if the sink has no space for the banknote.
	 * @throws DisabledException
	 *             if the sink is currently disabled.
	 */
	public synchronized void reject(T thing) throws CashOverloadException, DisabledException {
		source.reject(thing);
	}

	/**
	 * Moves the indicated banknote to the sink. This method should be called by the
	 * source device, and not by an external application.
	 * 
	 * @param banknote
	 *            The banknote to transport via the channel.
	 * @throws CashOverloadException
	 *             if the sink has no space for the banknote.
	 * @throws DisabledException
	 *             if the sink is currently disabled.
	 */
	public synchronized void receive(T banknote) throws CashOverloadException, DisabledException {
		sink.receive(banknote);
	}

	/**
	 * Returns whether the sink has space for at least one more banknote.
	 * 
	 * @return true if the sink can accept a banknote; false otherwise.
	 */
	public synchronized boolean hasSpace() {
		return sink.hasSpace();
	}
}
