package teetime.framework.test;

/**
 * Represents an exception in the setup of a test case.
 *
 * @author Christian Wulf (chw)
 *
 */
public class InvalidTestCaseSetupException extends RuntimeException {

	private static final long serialVersionUID = -1380841389726636785L;

	public InvalidTestCaseSetupException(final String message) {
		super(message);
	}

}
