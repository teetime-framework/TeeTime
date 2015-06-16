package teetime.framework.exceptionHandling;

public class TaskFarmInvalidStageException extends RuntimeException {

	private static final long serialVersionUID = -2024432280298919911L;

	public TaskFarmInvalidStageException(final String s) {
		super(s);
	}

}
