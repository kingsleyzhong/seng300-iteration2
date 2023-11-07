package com.jjjwelectronics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.ucalgary.seng300.simulation.InvalidStateSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

/**
 * The abstract base class for all devices from JJJW Electronics.
 * <p>
 * This class utilizes the Observer design pattern.
 * </p>
 * <p>
 * Each device will normally be coupled to an appropriate listener interface,
 * which extends AbstractDeviceListener; the type parameter T represents this
 * listener. The associated, specialized subinterface must be designed in
 * connection with the device class itself for the sake of a unified event
 * notification protocol.
 * </p>
 * 
 * @param <T>
 *            The type of listeners used for this kind of device. For a device
 *            whose class is <i>X</i>, its corresponding listener interface would
 *            typically be <i>X</i>Listener.
 *            
 * @author JJJW Electronics LLP
 */
public abstract class AbstractDevice<T extends IDeviceListener> implements IDevice<T> {
	private PowerGrid grid = null;
	private boolean poweredUp;

	@Override
	public synchronized boolean isPluggedIn() {
		return grid != null;
	}

	@Override
	public synchronized boolean isPoweredUp() {
		if(grid != null) {
			if(poweredUp) {
				if(grid.hasPower())
					return true;
			}
		}

		return false;
	}

	@Override
	public synchronized void plugIn(PowerGrid grid) {
		if(grid == null)
			throw new NullPointerSimulationException("You cannot plug into a non-existent grid.");

		this.grid = grid;
	}

	@Override
	public synchronized void unplug() {
		grid = null;
		poweredUp = false;
	}

	@Override
	public synchronized void turnOn() {
		if(grid != null) {
			if(poweredUp == false) {
				poweredUp = true;

				notifyTurnedOn();
			}

			// else do nothing
		}
		else
			throw new InvalidStateSimulationException("The device is not plugged in.");
	}

	private void notifyTurnedOn() {
		for(T listener : listeners)
			listener.aDeviceHasBeenTurnedOn(this);
	}

	@Override
	public synchronized void turnOff() {
		if(poweredUp == true) {
			poweredUp = false;

			notifyTurnedOff();
		}
	}

	private void notifyTurnedOff() {
		for(T listener : listeners)
			listener.aDeviceHasBeenTurnedOff(this);
	}

	private ArrayList<T> listeners = new ArrayList<>();

	@Override
	public List<T> listeners() {
		@SuppressWarnings("unchecked")
		List<T> clone = (List<T>)listeners.clone();

		return Collections.unmodifiableList(clone);
	}

	@Override
	public synchronized boolean deregister(T listener) {
		return listeners.remove(listener);
	}

	@Override
	public synchronized void deregisterAll() {
		listeners.clear();
	}

	@Override
	public final synchronized void register(T listener) {
		if(listener == null)
			throw new NullPointerSimulationException("listener");

		listeners.add(listener);
	}

	private boolean disabled = false;

	@Override
	public synchronized void disable() {
		if(!isPoweredUp())
			throw new NoPowerException();

		disabled = true;
		notifyDisabled();
	}

	private void notifyDisabled() {
		for(T listener : listeners())
			listener.aDeviceHasBeenDisabled(this);
	}

	@Override
	public synchronized void enable() {
		if(!isPoweredUp())
			throw new NoPowerException();

		disabled = false;
		notifyEnabled();
	}

	private void notifyEnabled() {
		for(T listener : listeners())
			listener.aDeviceHasBeenEnabled(this);
	}

	@Override
	public final synchronized boolean isDisabled() {
		if(!isPoweredUp())
			throw new NoPowerException();

		return disabled;
	}
}
