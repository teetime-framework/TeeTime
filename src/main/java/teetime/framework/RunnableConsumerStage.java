package teetime.framework;

public final class RunnableConsumerStage extends RunnableStage {

	public RunnableConsumerStage(final Stage stage) {
		super(stage);
	}

	@Override
	protected void beforeStageExecution() {
		// TODO wait for starting signal
	}

	@Override
	protected void afterStageExecution() {
		// do nothing
	}

}
