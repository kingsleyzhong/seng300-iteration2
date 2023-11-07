package ca.ucalgary.seng300.simulation;

/**
 * An exception that can be raised when an invalid argument is passed inside the
 * simulation.
 */
@SuppressWarnings("serial")
public class InvalidStateSimulationException extends SimulationException {
	/**
	 * Basic constructor.
	 * 
	 * @param message
	 *            The message describing the problem.
	 */
	public InvalidStateSimulationException(String message) {
		super(message);
	}
}
