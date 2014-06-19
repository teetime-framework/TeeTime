package teetime.variant.methodcallWithPorts.framework.core;

import teetime.util.list.CommittableQueue;

public abstract class AbstractStage<I, O> implements StageWithPort<I, O> {

	private final InputPort<I> inputPort = new InputPort<I>();
	private final OutputPort<O> outputPort = new OutputPort<O>();

	// protected final CommittableQueue<O> outputElements = new CommittableResizableArrayQueue<O>(null, 4);
	// private final CommittableQueue<O> outputElements = null;

	private StageWithPort<?, ?> parentStage;

	private int index;

	private StageWithPort<?, ?> successor;

	private boolean reschedulable;

	@Override
	public InputPort<I> getInputPort() {
		return this.inputPort;
	}

	@Override
	public OutputPort<O> getOutputPort() {
		return this.outputPort;
	}

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

		this.execute4(elements);

		// this.outputElements.commit();

		// return this.outputElements;
		return null;
	}

	// @Override
	// public void executeWithPorts() {
	// CommittableQueue execute;
	// do {
	// // execute = this.next().execute2(this.outputElements);
	// // execute = this.next().execute2(this.getOutputPort().pipe.getElements());
	// this.next().executeWithPorts();
	// } while (this.next().isReschedulable());
	// }

	protected abstract void execute4(CommittableQueue<I> elements);

	protected abstract void execute5(I element);

	protected final void send(final O element) {
		// this.outputElements.addToTailUncommitted(element);
		// this.outputElements.commit();

		this.getOutputPort().send(element);

		// CommittableQueue execute;

		StageWithPort<?, ?> next = this.next();
		do {
			// execute = this.next().execute2(this.outputElements);
			// execute = this.next().execute2(this.getOutputPort().pipe.getElements());
			next.executeWithPorts();
			// System.out.println("Executed " + this.next().getClass().getSimpleName());
		} while (next.isReschedulable());
		// } while (this.next().getInputPort().pipe.size() > 0);
		// } while (execute.size() > 0);
	}

	// @Override
	// public SchedulingInformation getSchedulingInformation() {
	// return this.schedulingInformation;
	// }

	// public void disable() {
	// this.schedulingInformation.setActive(false);
	// this.fireOnDisable();
	// }

	// private void fireOnDisable() {
	// if (this.listener != null) {
	// this.listener.onDisable(this, this.index);
	// }
	// }

	@Override
	public void onStart() {
		// empty default implementation
	}

	@Override
	public StageWithPort<?, ?> getParentStage() {
		return this.parentStage;
	}

	@Override
	public void setParentStage(final StageWithPort<?, ?> parentStage, final int index) {
		this.index = index;
		this.parentStage = parentStage;
	}

	@Override
	public StageWithPort<?, ?> next() {
		return this.successor;
	}

	@Override
	public void setSuccessor(final StageWithPort<? super O, ?> successor) {
		this.successor = successor;
	}

	@Override
	public boolean isReschedulable() {
		return this.reschedulable;
	}

	public void setReschedulable(final boolean reschedulable) {
		this.reschedulable = reschedulable;
	}

}
