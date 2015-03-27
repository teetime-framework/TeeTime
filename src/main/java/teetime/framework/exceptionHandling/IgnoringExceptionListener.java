package teetime.framework.exceptionHandling;

import teetime.framework.Stage;

public class IgnoringExceptionListener extends AbstractExceptionListener {

	@Override
	public FurtherExecution onStageException(final Exception e, final Stage throwingStage) {
		return FurtherExecution.CONTINUE;
	}
}
