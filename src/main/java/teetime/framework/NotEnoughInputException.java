package teetime.framework;

public final class NotEnoughInputException extends RuntimeException {

	private static final long serialVersionUID = -2517233596919204396L;

	@SuppressWarnings("PMD.AvoidSynchronizedAtMethodLevel")
	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}

}
