package teetime.variant.methodcallWithPorts.framework.core;

public interface StageWithPort<I, O> {

	String getId();

	InputPort<I> getInputPort();

	OutputPort<O> getOutputPort();

	void executeWithPorts();

	StageWithPort<?, ?> getParentStage();

	void setParentStage(StageWithPort<?, ?> parentStage, int index);

	// void setListener(OnDisableListener listener);

	boolean isReschedulable();

	void onIsPipelineHead();

	void onStart();

	void onSignal(Signal signal, InputPort<?> inputPort);
}
