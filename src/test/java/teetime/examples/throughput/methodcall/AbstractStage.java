package teetime.examples.throughput.methodcall;

public abstract class AbstractStage<I, O> implements Stage<I, O> {

	private final InputPort<I> inputPort = new InputPort<I>();
	private final OutputPort<O> outputPort = new OutputPort<O>();

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
		// pass through the end signal
		InputPort<I> port = this.getInputPort();
		if (port.pipe != null) {
			I element = port.read();
			if (element == END_SIGNAL) {
				this.getOutputPort().send((O) END_SIGNAL);
				return;
			}
		}

		this.execute3();
	}

	protected abstract void execute3();

	// protected abstract O[] execute4(I[] elements, int size);
}
