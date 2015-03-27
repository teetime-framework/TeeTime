package teetime.framework.exceptionHandling;

import teetime.framework.AbstractConsumerStage;

public class ExceptionTestConsumerStage extends AbstractConsumerStage<Object> {

	private int numberOfExecutions = 0;

	@Override
	protected void execute(final Object element) {
		if (numberOfExecutions % 1000 == 0) {
			throw new IllegalStateException("1000 loops");
		}
		numberOfExecutions++;
	}

}
