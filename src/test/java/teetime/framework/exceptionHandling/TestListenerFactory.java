package teetime.framework.exceptionHandling;

public class TestListenerFactory implements IExceptionListenerFactory {

	@Override
	public AbstractExceptionListener create() {
		return new TestListener();
	}

}
