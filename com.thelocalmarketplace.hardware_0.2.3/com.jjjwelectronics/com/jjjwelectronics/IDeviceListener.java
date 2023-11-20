package com.jjjwelectronics;

/**
 * This class represents the abstract interface for all device listeners. All
 * subclasses should add their own event notification methods, the first
 * parameter of which should always be the device affected.
 * 
 * @author JJJW Electronics LLP
 */
public interface IDeviceListener {
	/**
	 * Announces that the indicated device has been enabled.
	 * 
	 * @param device
	 *            The device that has been enabled.
	 */
	public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device);

	/**
	 * Announces that the indicated device has been disabled.
	 * 
	 * @param device
	 *            The device that has been disabled.
	 */
	public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device);

	/**
	 * Announces that the indicated device has been turned on.
	 * 
	 * @param device
	 *            The device that has been turned on.
	 */
	public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device);

	/**
	 * Announces that the indicated device has been turned off.
	 * 
	 * @param device
	 *            The device that has been turned off.
	 */
	public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device);
}
