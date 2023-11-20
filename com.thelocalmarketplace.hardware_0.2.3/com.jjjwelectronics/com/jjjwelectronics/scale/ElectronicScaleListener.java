package com.jjjwelectronics.scale;

import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Mass;

/**
 * Listens for events emanating from an electronic scale.
 * 
 * @author JJJW Electronics LLP
 */
public interface ElectronicScaleListener extends IDeviceListener {
	/**
	 * Announces that the mass on the indicated scale has changed.
	 * 
	 * @param scale
	 *            The scale where the event occurred.
	 * @param mass
	 *            The new mass.
	 */
	void theMassOnTheScaleHasChanged(IElectronicScale scale, Mass mass);

	/**
	 * Announces that excessive mass has been placed on the indicated scale.
	 * 
	 * @param scale
	 *            The scale where the event occurred.
	 */
	void theMassOnTheScaleHasExceededItsLimit(IElectronicScale scale);

	/**
	 * Announces that the former excessive mass has been removed from the indicated
	 * scale, and it is again able to measure mass.
	 * 
	 * @param scale
	 *            The scale where the event occurred.
	 */
	void theMassOnTheScaleNoLongerExceedsItsLimit(IElectronicScale scale);
}
