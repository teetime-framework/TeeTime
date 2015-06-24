package teetime.framework;

public class DynamicActuator {

	/**
	 * @deprecated Use {@link #startWithinNewThread(Stage)} instead.
	 */
	@Deprecated
	public Runnable wrap(final Stage stage) {
		if (stage.getInputPorts().length > 0) {
			return new RunnableConsumerStage(stage);
		}
		return new RunnableProducerStage(stage);
	}

	public void startWithinNewThread(final Stage stage) {
		Runnable runnable = wrap(stage);
		Thread thread = new Thread(runnable);
		thread.start();
	}
}
