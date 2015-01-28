package teetime.framework.exceptionHandling;

import teetime.framework.Stage;

/**
 * Represents an Exception, which is thrown by stages, if uncatched exceptions are thrown.
 *
 */
public class StageException extends RuntimeException {

	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = 6724637605943897808L;

	private final Stage throwingStage;
	private final Exception originalException;

	public StageException(final Exception e, final Stage throwingStage) {
		super();
		this.originalException = e;
		this.throwingStage = throwingStage;
	}

	public Exception getOriginalException() {
		return originalException;
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
