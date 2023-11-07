package com.tdc;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import powerutility.PowerGrid;

/**
 * Base type for all components from TDC.
 * <p>
 * This class utilizes the Observer design pattern. Subclasses inherit the
 * attach method, but each must define its own notifyXXX methods.
 * </p>
 * <p>
 * Each component must be coupled to an appropriate listener interface, which
 * extends AbstractcomponentObserver; the type parameter T represents this
 * listener.
 * </p>
 * <p>
 * Any individual component can be disabled, which means it will not permit
 * physical movements to be caused by the software. Any method that could cause
 * a physical movement will declare that it throws DisabledException.
 * </p>
 * 
 * @param <T>
 *            The type of listeners used for this component. For a component
 *            whose class is X, its corresponding listener interface would
 *            typically be XObserver.
 * @author TDC, Inc.
 */
public interface IComponent<T extends IComponentObserver> {
	/**
	 * Checks whether this component is connected to the power grid.
	 * 
	 * @return true if this component is connected; otherwise, false.
	 */
	boolean isConnected();

	/**
	 * Checks whether this component is activated.
	 * 
	 * @return true if the component is activated; otherwise, false.
	 */
	boolean isActivated();

	/**
	 * Checks whether this component is connected to a power grid that has power.
	 * 
	 * @return true if the component is connected to a grid that has power;
	 *             otherwise, false.
	 */
	boolean hasPower();

	/**
	 * Connects this component to the electrical power grid, leaving the power off.
	 * If the component is already connected or if it is already activated, this
	 * method does nothing.
	 * 
	 * @param grid
	 *            The grid to connect to. Must not be null. Must have power.
	 */
	void connect(PowerGrid grid);

	/**
	 * Disconnects this component from the electrical power grid. Disconnection also
	 * disactivates the component. If the component is already disconnected, this
	 * method does nothing.
	 */
	void disconnect();

	/**
	 * Attempts to turn on the power to this component. This does not guarantee that
	 * the component is plugged in and receiving power.
	 */
	void activate();

	/**
	 * Attempts to turn off the power to this component. If the component is already
	 * disactivated or if it is disconnected from the power grid, this method does
	 * nothing.
	 */
	void disactivate();

	/**
	 * Locates the indicated observer and removes it such that it will no longer be
	 * informed of events from this component. If the observer is not currently
	 * registered with this component, calls to this method will return false, but
	 * otherwise have no effect.
	 * 
	 * @param observer
	 *            The observer to remove.
	 * @return true if the observer was found and removed, false otherwise.
	 */
	boolean detach(T observer);

	/**
	 * All observers registered with this component are removed. If there are none,
	 * calls to this method have no effect.
	 */
	void detachAll();

	/**
	 * Registers the indicated observer to receive event notifications from this
	 * component.
	 * 
	 * @param observer
	 *            The observer to be added.
	 * @throws NullPointerSimulationException
	 *             If the argument is null.
	 */
	void attach(T observer);

	/**
	 * Disables this component from receiving input and producing output. Announces
	 * "disabled" event. Requires power.
	 */
	void disable();

	/**
	 * Enables this component for receiving input and producing output. Announces
	 * "enabled" event. Requires power.
	 */
	void enable();

	/**
	 * Returns whether this component is currently disabled from receiving input and
	 * producing output. Requires power.
	 * 
	 * @return true if the component is disabled; false if the component is enabled.
	 */
	boolean isDisabled();
}