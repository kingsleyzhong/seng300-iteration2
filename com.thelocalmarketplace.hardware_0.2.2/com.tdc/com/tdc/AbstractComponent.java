package com.tdc;

import java.util.ArrayList;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

/**
 * The abstract base class for all components from TDC.
 * <p>
 * This class utilizes the Observer design pattern. Subclasses inherit the
 * attach method, but each must define its own notifyXXX methods.
 * </p>
 * <p>
 * Each component must be coupled to an appropriate listener interface, which
 * extends IComponentObserver; the type parameter T represents this listener.
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
public abstract class AbstractComponent<T extends IComponentObserver> implements IComponent<T> {
	private PowerGrid grid = null;
	private boolean isActivated = false;

	@Override
	public synchronized boolean isConnected() {
		return grid != null;
	}

	@Override
	public synchronized boolean isActivated() {
		return isActivated;
	}
	
	@Override
	public boolean hasPower() {
		if(grid != null)
			return grid.hasPower();
		
		return false;
	}

	@Override
	public synchronized void connect(PowerGrid grid) {
		if(grid == null)
			throw new NullPointerSimulationException();

		this.grid = grid;
	}

	@Override
	public synchronized void disconnect() {
		grid = null;
	}

	@Override
	public synchronized void activate() {
		isActivated = true;
	}

	@Override
	public synchronized void disactivate() {
		isActivated = false;
	}

	/**
	 * A list of the registered observers on this component.
	 */
	protected ArrayList<T> observers = new ArrayList<>();

	@Override
	public final synchronized boolean detach(T observer) {
		return observers.remove(observer);
	}

	@Override
	public final synchronized void detachAll() {
		observers.clear();
	}

	@Override
	public final synchronized void attach(T observer) {
		if(observer == null)
			throw new NullPointerSimulationException("observer");

		observers.add(observer);
	}

	private boolean disabled = false;

	@Override
	public final synchronized void disable() {
		if(!hasPower())
			throw new NoPowerException();

		disabled = true;
		notifyDisabled();
	}

	private void notifyDisabled() {
		for(T observer : observers)
			observer.disabled(this);
	}

	@Override
	public final synchronized void enable() {
		if(!hasPower())
			throw new NoPowerException();

		if(disabled) {
			disabled = false;
			notifyEnabled();
		}
	}

	private void notifyEnabled() {
		for(T listener : observers)
			listener.enabled(this);
	}

	@Override
	public final synchronized boolean isDisabled() {
		if(!hasPower())
			throw new NoPowerException();

		return disabled;
	}
}
