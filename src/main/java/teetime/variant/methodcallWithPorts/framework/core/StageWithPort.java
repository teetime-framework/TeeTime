package teetime.variant.methodcallWithPorts.framework.core;

import teetime.util.list.CommittableQueue;

public interface StageWithPort<I, O> {

	InputPort<I> getInputPort();

	OutputPort<O> getOutputPort();

	void executeWithPorts();

	// void executeWithPorts(Object element);

	// O execute(Object element);

	// CommittableQueue<O> execute2();

	CommittableQueue<O> execute2(CommittableQueue<I> elements);

	// SchedulingInformation getSchedulingInformation();

	StageWithPort<?, ?> getParentStage();

	void setParentStage(StageWithPort<?, ?> parentStage, int index);

	// void setListener(OnDisableListener listener);

	StageWithPort<?, ?> next();

	void setSuccessor(StageWithPort<? super O, ?> successor);

	boolean isReschedulable();

	void onIsPipelineHead();

	void onStart();
}
