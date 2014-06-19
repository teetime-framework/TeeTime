package teetime.variant.methodcall.framework.core;

import teetime.util.list.CommittableQueue;
import teetime.util.list.CommittableResizableArrayQueue;

public abstract class AbstractStage<I, O> implements Stage<I, O> {

	protected final CommittableQueue<O> outputElements = new CommittableResizableArrayQueue<O>(null, 4);

	private Stage<?, ?> parentStage;

	private int index;

	private Stage<?, ?> successor;

	private boolean reschedulable;

	@Override
	public Object executeRecursively(final Object element) {
		O result = this.execute(element);
		if (result == null) {
			return null;
		}
		Stage<?, ?> next = this.next();
		// if (next != null) {
		// return next.executeRecursively(result);
		// } else {
		// return result;
		// }
		return next.executeRecursively(result);
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

		this.outputElements.commit();

		return this.outputElements;
	}

	protected abstract void execute4(CommittableQueue<I> elements);

	protected void send(final O element) {
		this.outputElements.addToTailUncommitted(element);
	}

	@Override
	public void onStart() {
		// empty default implementation
	}

	@Override
	public Stage<?, ?> getParentStage() {
		return this.parentStage;
	}

	@Override
	public void setParentStage(final Stage<?, ?> parentStage, final int index) {
		this.index = index;
		this.parentStage = parentStage;
	}

	@Override
	public Stage<?, ?> next() {
		return this.successor;
	}

	@Override
	public void setSuccessor(final Stage<? super O, ?> successor) {
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
