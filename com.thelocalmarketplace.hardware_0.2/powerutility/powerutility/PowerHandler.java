package powerutility;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * Allows for exceptions to occur in another thread that are uncaught there, but
 * are handled in the current thread.
 */
public class PowerHandler implements UncaughtExceptionHandler {
	private UncaughtExceptionHandler inner;

	PowerHandler(UncaughtExceptionHandler h) {
		inner = h;
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		if(e instanceof PowerSurge)
			throw (PowerSurge)e;
		if(e instanceof NoPowerException)
			throw (NoPowerException)e;

		if(inner != null)
			inner.uncaughtException(t, e);
	}
}
