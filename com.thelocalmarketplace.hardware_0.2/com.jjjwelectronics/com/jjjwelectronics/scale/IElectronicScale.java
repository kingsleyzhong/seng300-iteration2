package com.jjjwelectronics.scale;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;

import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Abstract base type of electronic scales on which can be placed one or more
 * items. Scales have a sensitivity: changes to the mass smaller than this will
 * go unnoticed. Scales have a mass limit as well; when the items on the scale
 * are heavier than this limit, the scale cannot operate.
 * 
 * @author JJJW Electronics LLP
 */
public interface IElectronicScale extends IDevice<ElectronicScaleListener> {
	/**
	 * Gets the mass limit for the scale. Masses greater than this will not be
	 * measurable by the scale, but will cause overload. Does not require power.
	 * 
	 * @return The mass limit.
	 */
	Mass getMassLimit();

	/**
	 * Gets the sensitivity of the scale. Changes smaller than this limit are not
	 * noticed or announced. Does not require power.
	 * 
	 * @return The sensitivity.
	 */
	Mass getSensitivityLimit();

	/**
	 * Adds an item to the scale. If the addition is successful, a "mass changed"
	 * event is announced. If the mass is greater than the mass limit, announces
	 * "overload" event. Requires power.
	 * 
	 * @param item
	 *            The item to add.
	 * @throws SimulationException
	 *             If the same item is added more than once or is null.
	 */
	void addAnItem(Item item);

	/**
	 * Removes an item from the scale. If the operation is successful, announces
	 * "mass changed" event. If the scale was overloaded and this removal causes it
	 * to no longer be overloaded, announces "outOfOverload" event. Does not require
	 * power.
	 * 
	 * @param item
	 *            The item to remove.
	 * @throws SimulationException
	 *             If the item is not on the scale (including if it is null).
	 */
	void removeAnItem(Item item);
}