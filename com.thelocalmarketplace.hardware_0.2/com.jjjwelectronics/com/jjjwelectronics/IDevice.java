package com.jjjwelectronics;

import java.util.List;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

/**
 * The abstract base type for all devices from JJJW Electronics.
 * 
 * @param <T>
 *            The type of listeners used for this device. For a device whose
 *            class is X, its corresponding listener interface would typically
 *            be XListener.
 *            
 * @author JJJW Electronics LLP
 */
public interface IDevice<T extends IDeviceListener> {
	/**
	 * Checks whether this device is plugged-in. Does not require power.
	 * 
	 * @return true if this device is plugged-in; otherwise, false.
	 */
	boolean isPluggedIn();

	/**
	 * Checks whether this device is turned on and powered up. Does not require
	 * power.
	 * 
	 * @return true if the device is plugged in, turned on, and receiving power;
	 *             otherwise, false.
	 */
	boolean isPoweredUp();

	/**
	 * Connects this device to the indicated electrical power grid, leaving the
	 * powered-up state unchanged. No events are announced.
	 * 
	 * @param grid
	 *            The grid to plug into. Cannot be null.
	 * @throws SimulationException
	 *             If the indicated grid is null.
	 * @throws NoPowerException
	 *             If the grid has a power outage.
	 */
	void plugIn(PowerGrid grid);

	/**
	 * Disconnects this device from the electrical power grid, also turning it off.
	 * No events are announced. If the device is already unplugged, this method has
	 * no effect. Does not require power.
	 */
	void unplug();

	/**
	 * Attempts to turn on the power to this device. If the state actually changes,
	 * a "turned-on" event is announced; otherwise, no events are announced. If the
	 * device is unplugged, throws an InvalidStateSimulationException. Does not
	 * require power.
	 */
	void turnOn();

	/**
	 * Attempts to turn off the power to this device. If the device is already
	 * turned off or if it is disconnected from the power grid, this method does
	 * nothing. If the state actually changes, a "turned-off" event is announced.
	 * Does not require power.
	 */
	void turnOff();

	/**
	 * Locates the indicated listener and removes it such that it will no longer be
	 * informed of events from this device. If the listener is not currently
	 * registered with this device, calls to this method will return false, but
	 * otherwise have no effect. Does not require power.
	 * 
	 * @param listener
	 *            The listener to remove.
	 * @return true if the listener was found and removed, false otherwise.
	 */
	boolean deregister(T listener);

	/**
	 * All listeners registered with this device are removed. If there are none,
	 * calls to this method have no effect. Does not require power.
	 */
	void deregisterAll();

	/**
	 * Registers the indicated listener to receive event notifications from this
	 * device. Does not require power.
	 * 
	 * @param listener
	 *            The listener to be added.
	 * @throws NullPointerSimulationException
	 *             If the argument is null.
	 */
	void register(T listener);

	/**
	 * Disables this device from receiving input and producing output. Announces
	 * "disabled" event. Requires power.
	 */
	void disable();

	/**
	 * Enables this device for receiving input and producing output. Announces
	 * "enabled" event. Requires power.
	 */
	void enable();

	/**
	 * Returns whether this device is currently disabled from receiving input and
	 * producing output. Requires power.
	 * 
	 * @return true if the device is disabled; false if the device is enabled.
	 */
	boolean isDisabled();
	
	/**
	 * Obtains the registered listeners on this device. Does not require power.
	 * 
	 * @return An unmodifiable list of the listeners registered on this device.
	 */
	List<T> listeners();
}