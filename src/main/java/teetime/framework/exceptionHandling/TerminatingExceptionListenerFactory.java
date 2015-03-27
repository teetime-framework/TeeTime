package teetime.framework.exceptionHandling;

public class TerminatingExceptionListenerFactory implements IExceptionListenerFactory {

	@Override
	public AbstractExceptionListener newHandlerInstance() {
		return new TerminatingExceptionListener();
	}

}
