package teetime.framework;

public class DynamicActuator {

	public Runnable wrap(final Stage stage) {
		return new RunnableConsumerStage(stage);
	}
}
