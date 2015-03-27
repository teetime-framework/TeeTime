package teetime.framework.exceptionHandling;

public class TerminatingExceptionListenerFactory implements IExceptionListenerFactory {

	@Override
	public AbstractExceptionListener create() {
		return new TerminatingExceptionListener();
	}

}
