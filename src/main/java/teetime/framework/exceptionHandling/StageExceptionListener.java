package teetime.framework.exceptionHandling;

import teetime.framework.Stage;

/**
 * Represent a minimalistic StageExceptionListener. Listener which extend from this one, must a least implement this functionality.
 *
 */
public abstract class StageExceptionListener {

	private final Thread thread;

	public StageExceptionListener(final Thread thread) {
		this.thread = thread;
	}

	/**
	 * This method will be executed if an exception arises.
	 *
	 * @param e
	 *            thrown exception
	 * @param throwingStage
	 *            the stage, which has thrown the exception.
	 */
	public abstract void onStageException(Exception e, Stage throwingStage);

	/**
	 * Retrieves the thread in which the exception occurred.
	 *
	 * @return exception throwing thread
	 */
	public Thread getThread() {
		return thread;
	}

}
