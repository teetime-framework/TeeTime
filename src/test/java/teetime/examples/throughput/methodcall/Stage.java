package teetime.examples.throughput.methodcall;

import teetime.util.list.CommittableQueue;

public interface Stage<I, O> {

	public static final Object END_SIGNAL = new Object();

	// CommittableQueue<O> execute2();

	// InputPort<I> getInputPort();

	CommittableQueue<O> execute2(CommittableQueue<I> elements);

	// OutputPort<O> getOutputPort();
}
