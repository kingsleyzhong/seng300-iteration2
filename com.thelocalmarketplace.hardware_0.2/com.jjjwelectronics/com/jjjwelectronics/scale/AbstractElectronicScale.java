package com.jjjwelectronics.scale;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

import com.jjjwelectronics.AbstractDevice;
import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Mass.MassDifference;
import com.jjjwelectronics.OverloadedDevice;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import powerutility.NoPowerException;

/**
 * Abstract base class of electronic scales on which can be placed one or more
 * items. Scales have a sensitivity: changes to the mass smaller than this will
 * go unnoticed. Scales have a mass limit as well; when the items on the scale
 * are heavier than this limit, the scale cannot operate.
 * <p>
 * Different models in our product line have different levels of sensitivity,
 * different mass limits, and different and different precisions.
 * 
 * @author JJJW Electronics LLP
 */
public abstract class AbstractElectronicScale extends AbstractDevice<ElectronicScaleListener>
	implements IElectronicScale {
	protected ArrayList<Item> items = new ArrayList<>();
	protected Mass massLimit;
	protected Mass currentMass = Mass.ZERO;
	private Mass massAtLastEvent = Mass.ZERO;
	protected Mass sensitivityLimit;

	protected AbstractElectronicScale(Mass limit, Mass sensitivityLimit) {
		super();

		if(limit == null)
			throw new NullPointerSimulationException("The mass limit cannot be non-existent.");

		if(limit.compareTo(Mass.ZERO) <= 0)
			throw new InvalidArgumentSimulationException("The mass limit must be positive.");

		if(sensitivityLimit == null)
			throw new NullPointerSimulationException("The sensitivity cannot be non-existent.");

		if(sensitivityLimit.compareTo(Mass.ZERO) <= 0)
			throw new InvalidArgumentSimulationException("The sensitivity must be positive.");

		massLimit = limit;
		this.sensitivityLimit = sensitivityLimit;
	}

	@Override
	public Mass getMassLimit() {
		return massLimit;
	}

	@Override
	public Mass getSensitivityLimit() {
		return sensitivityLimit;
	}

	@Override
	public synchronized void addAnItem(Item item) {
		if(!isPoweredUp())
			throw new NoPowerException();

		if(item == null)
			throw new NullPointerSimulationException("item");

		if(items.contains(item))
			throw new InvalidArgumentSimulationException("The same item cannot be added more than once to the scale.");

		currentMass = currentMass.sum(item.getMass());

		items.add(item);

		if(currentMass.compareTo(massLimit) > 0)
			notifyOverload();

		MassDifference difference = currentMass.difference(massAtLastEvent);
		if(difference.compareTo(sensitivityLimit) >= 0)
			notifyMassChanged();
	}

	@Override
	public synchronized void removeAnItem(Item item) {
		if(!isPoweredUp())
			throw new NoPowerException();

		if(!items.remove(item))
			throw new InvalidArgumentSimulationException("The item was not found amongst those on the scale.");

		// To avoid drift in the sum due to round-off error, recalculate the mass.
		Mass newMass = Mass.ZERO;
		for(Item itemOnScale : items)
			newMass = newMass.sum(itemOnScale.getMass());

		currentMass = newMass;

		if(massAtLastEvent.compareTo(massLimit) > 0 && newMass.compareTo(massLimit) <= 0)
			notifyOutOfOverload();

		if(currentMass.compareTo(massLimit) <= 0
			&& massAtLastEvent.difference(currentMass).abs().compareTo(sensitivityLimit) >= 0)
			notifyMassChanged();
	}

	protected void notifyOverload() {
		for(ElectronicScaleListener l : listeners())
			l.theMassOnTheScaleHasExceededItsLimit(this);
	}

	protected void notifyOutOfOverload() {
		massAtLastEvent = currentMass;

		for(ElectronicScaleListener l : listeners())
			l.theMassOnTheScaleNoLongerExceedsItsLimit(this);
	}

	protected void notifyMassChanged() {
		massAtLastEvent = currentMass;

		for(ElectronicScaleListener l : listeners())
			l.theMassOnTheScaleHasChanged(this, currentMass);
	}

	/**
	 * Gets the current mass on the scale. Requires power.
	 * 
	 * @return The current mass.
	 * @throws OverloadedDevice
	 *             If the mass has overloaded the scale.
	 */
	public synchronized Mass getCurrentMassOnTheScale() throws OverloadedDevice {
		if(!isPoweredUp())
			throw new NoPowerException();

		if(currentMass.compareTo(massLimit) <= 0) {
			long s = sensitivityLimit.inMicrograms().longValue();
			return currentMass.sum(new Mass(BigInteger.valueOf(Math.max(new Random().nextLong(s) - s / 2, 0))));
		}

		throw new OverloadedDevice();
	}
}