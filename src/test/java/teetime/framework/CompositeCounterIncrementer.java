package teetime.framework;

class CompositeCounterIncrementer extends AbstractCompositeStage {

	private final InputPort<CounterContainer> inputPort;
	private final OutputPort<CounterContainer> outputPort;

	public CompositeCounterIncrementer(final int depth) {
		if (depth <= 0) { // one counter incrementer is always created
			throw new IllegalArgumentException();
		}

		CounterIncrementer incrementer = new CounterIncrementer();
		this.inputPort = incrementer.getInputPort();

		InputPort<CounterContainer> lastStageInputPort;
		if (depth > 1) {
			CompositeCounterIncrementer lastStage = new CompositeCounterIncrementer(depth - 1);
			lastStageInputPort = lastStage.getInputPort();
			outputPort = lastStage.getOutputPort();
		} else {
			// NoopFilter<CounterContainer> lastStage = new NoopFilter<CounterContainer>();
			CounterIncrementer lastStage = incrementer;
			lastStageInputPort = lastStage.getInputPort();
			outputPort = lastStage.getOutputPort();
		}

		connectPorts(incrementer.getOutputPort(), lastStageInputPort);
	}

	public InputPort<CounterContainer> getInputPort() {
		return inputPort;
	}

	public OutputPort<CounterContainer> getOutputPort() {
		return outputPort;
	}

}
