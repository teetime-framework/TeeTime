package teetime.examples.throughput.methodcall;

public class RunnableStage implements Runnable {

	private final StageWithPort<?, ?> stage;

	public RunnableStage(final StageWithPort<?, ?> stage) {
		this.stage = stage;
	}

	@Override
	public void run() {
		this.stage.onStart();

		do {
			this.stage.executeWithPorts();
		} while (this.stage.isReschedulable());
	}

}
