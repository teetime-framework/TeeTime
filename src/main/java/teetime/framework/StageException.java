package teetime.framework;

/**
 * Represents an Exception, which is thrown by stages, if uncatched exceptions are thrown.
 *
 */
public class StageException extends Exception {

	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = 6724637605943897808L;

	private final Stage throwingStage;

	public StageException(final Stage throwingStage) {
		super();
		this.throwingStage = throwingStage;
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
