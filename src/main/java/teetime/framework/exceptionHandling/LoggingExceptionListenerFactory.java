package teetime.framework.exceptionHandling;

public class LoggingExceptionListenerFactory implements IExceptionListenerFactory {

	@Override
	public AbstractExceptionListener newHandlerInstance() {
		return new LoggingExceptionListenerListener();
	}

}
