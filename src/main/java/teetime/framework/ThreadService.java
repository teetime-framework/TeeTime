package teetime.framework;

public class ThreadService {

	public Runnable startWithinNewThread(final Stage stage) {
		Runnable runnable = wrap(stage);
		Thread thread = new Thread(runnable);
		thread.start();
		return runnable;
	}

	private AbstractRunnableStage wrap(final Stage stage) {
		if (stage.getInputPorts().size() > 0) {
			return new RunnableConsumerStage(stage);
		}
		return new RunnableProducerStage(stage);
	}
}
