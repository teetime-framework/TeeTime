package teetime.framework.exceptionHandling;

public class IgnoringExceptionListenerFactory implements IExceptionListenerFactory {

	@Override
	public AbstractExceptionListener create() {
		return new IgnoringExceptionListener();
	}

}
