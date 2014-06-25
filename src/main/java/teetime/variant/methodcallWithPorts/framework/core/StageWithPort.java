package teetime.variant.methodcallWithPorts.framework.core;

import teetime.util.list.CommittableQueue;

public interface StageWithPort<I, O> {

	InputPort<I> getInputPort();

	OutputPort<O> getOutputPort();

	void executeWithPorts();

	CommittableQueue<O> execute2(CommittableQueue<I> elements);

	StageWithPort<?, ?> getParentStage();

	void setParentStage(StageWithPort<?, ?> parentStage, int index);

	// void setListener(OnDisableListener listener);

	boolean isReschedulable();

	void onIsPipelineHead();

	void onStart();
}
