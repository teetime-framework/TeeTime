package teetime.framework.exceptionHandling;

import teetime.framework.Stage;

public class TerminatingExceptionListener extends AbstractExceptionListener {

	@Override
	public FurtherExecution onStageException(final Exception e, final Stage throwingStage) {
		logger.warn("Exception occurred in " + throwingStage.getId(), e);
		return FurtherExecution.TERMINATE;
	}

}
