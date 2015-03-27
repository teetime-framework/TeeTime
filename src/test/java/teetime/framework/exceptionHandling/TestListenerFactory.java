package teetime.framework.exceptionHandling;

public class TestListenerFactory implements IExceptionListenerFactory {

	@Override
	public AbstractExceptionListener newHandlerInstance() {
		return new TestListener();
	}

}
