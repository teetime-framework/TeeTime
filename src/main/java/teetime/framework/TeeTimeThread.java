package teetime.framework;

public class TeeTimeThread extends Thread {

	private final AbstractRunnableStage runnable;

	public TeeTimeThread(final AbstractRunnableStage runnable, final String name) {
		super(runnable, name);
		this.runnable = runnable;
	}

	public void sendInitializingSignal() {
		if (runnable instanceof RunnableProducerStage) {
			((RunnableProducerStage) runnable).triggerInitializingSignal();
		}
	}

	public void sendStartingSignal() {
		if (runnable instanceof RunnableProducerStage) {
			((RunnableProducerStage) runnable).triggerStartingSignal();
		}
	}

	@Override
	public synchronized void start() {
		runnable.stage.getOwningContext().getThreadService().getRunnableCounter().inc();
		super.start();
	}
}
