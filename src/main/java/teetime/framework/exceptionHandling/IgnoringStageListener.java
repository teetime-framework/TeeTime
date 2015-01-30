package teetime.framework.exceptionHandling;

import teetime.framework.Stage;

public class IgnoringStageListener extends StageExceptionListener {

	public IgnoringStageListener() {
		super();
	}

	@Override
	public FurtherExecution onStageException(final Exception e, final Stage throwingStage) {
		return FurtherExecution.CONTINUE;
	}
}
