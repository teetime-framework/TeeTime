package teetime.examples.throughput.methodcall;

import teetime.util.list.CommittableQueue;
import teetime.util.list.CommittableResizableArrayQueue;

abstract class AbstractStage<I, O> implements Stage<I, O> {

	// private final InputPort<I> inputPort = new InputPort<I>();
	// private final OutputPort<O> outputPort = new OutputPort<O>();

	protected final CommittableQueue<O> outputElements = new CommittableResizableArrayQueue<O>(null, 4);

	private final SchedulingInformation schedulingInformation = new SchedulingInformation();

	private Stage parentStage;

	private OnDisableListener listener;

	private int index;

	// @Override
	// public InputPort<I> getInputPort() {
	// return this.inputPort;
	// }

	// @Override
	// public OutputPort<O> getOutputPort() {
	// return this.outputPort;
	// }

	@Override
	public CommittableQueue<O> execute2(final CommittableQueue<I> elements) {
		// pass through the end signal
		// InputPort<I> port = this.getInputPort();
		// if (elements != null) {
		// // I element = port.read();
		// // I element = elements.getTail();
		// // if (element == END_SIGNAL) {
		// // this.send((O) END_SIGNAL);
		// // } else {
		// // // elements = this.getInputPort().pipe.getElements();
		// // }
		//
		// this.execute4(elements);
		// } else {
		// throw new IllegalStateException();
		// }

		// boolean inputIsEmpty = elements.isEmpty();

		this.execute4(elements);

		this.outputElements.commit();

		// boolean outputIsEmpty = this.outputElements.isEmpty();
		//
		// if (inputIsEmpty && outputIsEmpty) {
		// this.disable();
		// }

		return this.outputElements;
	}

	// protected abstract void execute3();

	protected abstract void execute4(CommittableQueue<I> elements);

	protected final void send(final O element) {
		this.outputElements.addToTailUncommitted(element);
	}

	@Override
	public SchedulingInformation getSchedulingInformation() {
		return this.schedulingInformation;
	}

	public void disable() {
		this.schedulingInformation.setActive(false);
		this.fireOnDisable();
	}

	private void fireOnDisable() {
		if (this.listener != null) {
			this.listener.onDisable(this, this.index);
		}
	}

	@Override
	public Stage getParentStage() {
		return this.parentStage;
	}

	@Override
	public void setParentStage(final Stage parentStage, final int index) {
		this.index = index;
		this.parentStage = parentStage;
	}

	public OnDisableListener getListener() {
		return this.listener;
	}

	public void setListener(final OnDisableListener listener) {
		this.listener = listener;
	}

}
