package teetime.framework.exceptionHandling;

public class LoggingExceptionListenerFactory implements IExceptionListenerFactory {

	@Override
	public AbstractExceptionListener create() {
		return new LoggingExceptionListener();
	}

}
