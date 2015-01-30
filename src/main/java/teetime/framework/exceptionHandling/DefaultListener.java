package teetime.framework.exceptionHandling;

import teetime.framework.Stage;

public class DefaultListener extends StageExceptionListener {

	public DefaultListener() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onStageException(final Exception e, final Stage throwingStage) {
		return true;
	}
}
