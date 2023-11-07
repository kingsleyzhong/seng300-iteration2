package com.tdc;

/**
 * This class represents the abstract interface for all component observers. All
 * subclasses should add their own event notification methods, the first
 * parameter of which should always be the component affected.
 *            
 * @author TDC, Inc.
 */
public interface IComponentObserver {
	/**
	 * Announces that the indicated component has been enabled.
	 * 
	 * @param component
	 *            The component that has been enabled.
	 */
	void enabled(IComponent<? extends IComponentObserver> component);

	/**
	 * Announces that the indicated component has been disabled.
	 * 
	 * @param component
	 *            The component that has been disabled.
	 */
	void disabled(IComponent<? extends IComponentObserver> component);

	/**
	 * Announces that the indicated component has been turned on.
	 * 
	 * @param component
	 *            The component that has been turned on.
	 */
	void turnedOn(IComponent<? extends IComponentObserver> component);

	/**
	 * Announces that the indicated component has been turned off.
	 * 
	 * @param component
	 *            The component that has been turned off.
	 */
	void turnedOff(IComponent<? extends IComponentObserver> component);
}
