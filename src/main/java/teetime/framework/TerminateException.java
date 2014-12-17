package teetime.framework;

public final class TerminateException extends RuntimeException {

	private static final long serialVersionUID = 6841651916837487909L;

	@SuppressWarnings("PMD.AvoidSynchronizedAtMethodLevel")
	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}

}
