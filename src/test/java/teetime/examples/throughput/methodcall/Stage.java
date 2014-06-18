package teetime.examples.throughput.methodcall;

import teetime.util.list.CommittableQueue;

public interface Stage<I, O> {

	Object executeRecursively(Object element);

	O execute(Object element);

	// CommittableQueue<O> execute2();

	CommittableQueue<O> execute2(CommittableQueue<I> elements);

	SchedulingInformation getSchedulingInformation();

	Stage getParentStage();

	void setParentStage(Stage parentStage, int index);

	void setListener(OnDisableListener listener);

	Stage next();

	void setSuccessor(Stage<?, ?> successor);

	boolean isReschedulable();

	void onIsPipelineHead();

	void onStart();

}
