package teetime.framework.exceptionHandling;

import teetime.framework.Stage;

/**
 * Represents an Exception, which is thrown by stages in case of they throw exceptions.
 *
 */
public class StageException extends RuntimeException {

	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = 6724637605943897808L;

	private final Stage throwingStage;

	public StageException(final Exception e, final Stage throwingStage) {
		super(e);
		this.throwingStage = throwingStage;
	}

	public Throwable getOriginalException() {
		return this.getCause();
	}

	/**
	 * Returns the stage, which failed with an uncatched exception
	 *
	 * @return stage instance, which throws the exception
	 */
	public Stage getThrowingStage() {
		return throwingStage;
	}

}
