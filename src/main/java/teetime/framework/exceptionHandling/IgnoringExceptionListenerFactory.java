package teetime.framework.exceptionHandling;

public class IgnoringExceptionListenerFactory implements IExceptionListenerFactory {

	@Override
	public AbstractExceptionListener newHandlerInstance() {
		return new IgnoringExceptionListener();
	}

}
