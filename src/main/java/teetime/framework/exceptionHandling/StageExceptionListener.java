package teetime.framework.exceptionHandling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.RunnableStage;
import teetime.framework.Stage;

/**
 * Represent a minimalistic StageExceptionListener. Listener which extend from this one, must a least implement this functionality.
 * This abstract class provides a Logger {@link #logger} and a method to terminate the threads execution {@link #terminateExecution()}.
 */
public abstract class StageExceptionListener {

	private RunnableStage runnable;

	/**
	 * The default logger, which can be used by all subclasses
	 */
	protected final Logger logger;

	public StageExceptionListener() {
		this.logger = LoggerFactory.getLogger(this.getClass().getCanonicalName());
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
	 * This method can be used to terminate the execution of the thread.
	 */
	protected final void terminateExecution() {
		this.runnable.abortExecution();
	}

	public final void setRunnableStage(final RunnableStage runnableStage) {
		this.runnable = runnableStage;
	}
}
