package teetime.framework.pipe;

public class CouldNotFindPipeImplException extends RuntimeException {

	private static final long serialVersionUID = 5242260988104493402L;

	public CouldNotFindPipeImplException(final String key) {
		super("Could not find any pipe implementation that conforms to the key: " + key);
	}

}
