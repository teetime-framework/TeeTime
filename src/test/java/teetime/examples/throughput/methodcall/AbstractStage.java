package teetime.examples.throughput.methodcall;

public abstract class AbstractStage<I, O> implements Stage<I, O> {

	Runnable inputPortIsUsed = new Runnable() {
		@Override
		public void run() {
			// pass through the end signal
			I element = AbstractStage.this.getInputPort().read();
			if (element == END_SIGNAL) {
				AbstractStage.this.getOutputPort().send((O) END_SIGNAL);
				return;
			}

			AbstractStage.this.execute3();
		}
	};

	Runnable inputPortIsNotUsed = new Runnable() {
		@Override
		public void run() {
			// do not check

			AbstractStage.this.execute3();
		}
	};

	private final InputPort<I> inputPort = new InputPort<I>();
	private final OutputPort<O> outputPort = new OutputPort<O>();
	protected Runnable endSignalCheck = this.inputPortIsUsed;

	@Override
	public InputPort<I> getInputPort() {
		return this.inputPort;
	}

	@Override
	public OutputPort<O> getOutputPort() {
		return this.outputPort;
	}

	@Override
	public final void execute2() {
		this.endSignalCheck.run();
	}

	protected abstract void execute3();
}
