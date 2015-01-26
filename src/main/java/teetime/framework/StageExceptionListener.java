package teetime.framework;

public abstract class StageExceptionListener {

	private final Thread thread;

	public StageExceptionListener(final Thread thread) {
		this.thread = thread;
	}

	public abstract void onStageException(Stage throwingStage);

}
