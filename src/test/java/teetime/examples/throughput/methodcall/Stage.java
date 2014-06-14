package teetime.examples.throughput.methodcall;

import teetime.util.list.CommittableQueue;

public interface Stage<I, O> {

	public static final Object END_SIGNAL = new Object();

	O execute(Object element);

	// CommittableQueue<O> execute2();

	// InputPort<I> getInputPort();

	CommittableQueue<O> execute2(CommittableQueue<I> elements);

	SchedulingInformation getSchedulingInformation();

	// OutputPort<O> getOutputPort();

	Stage getParentStage();

	void setParentStage(Stage parentStage, int index);

	void setListener(OnDisableListener listener);
}
