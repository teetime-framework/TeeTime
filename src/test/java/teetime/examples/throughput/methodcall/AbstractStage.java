package teetime.examples.throughput.methodcall;

import teetime.util.list.CommittableQueue;
import teetime.util.list.CommittableResizableArrayQueue;

public abstract class AbstractStage<I, O> implements Stage<I, O> {

	// private final InputPort<I> inputPort = new InputPort<I>();
	// private final OutputPort<O> outputPort = new OutputPort<O>();

	CommittableQueue<O> outputElements = new CommittableResizableArrayQueue<O>(null, 4);

	// @Override
	// public InputPort<I> getInputPort() {
	// return this.inputPort;
	// }

	// @Override
	// public OutputPort<O> getOutputPort() {
	// return this.outputPort;
	// }

	@Override
	public final CommittableQueue<O> execute2(final CommittableQueue<I> elements) {

		// pass through the end signal
		// InputPort<I> port = this.getInputPort();
		if (elements != null) {
			// I element = port.read();
			I element = elements.getTail();
			if (element == END_SIGNAL) {
				this.send((O) END_SIGNAL);
			} else {
				// elements = this.getInputPort().pipe.getElements();
				this.execute4(elements);
			}
		} else {
			throw new IllegalStateException();
		}

		this.outputElements.commit();
		return this.outputElements;
	}

	// protected abstract void execute3();

	protected abstract void execute4(CommittableQueue<I> elements);

	void send(final O element) {
		this.outputElements.addToTailUncommitted(element);
	}
}
