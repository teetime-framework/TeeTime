package teetime.variant.methodcallWithPorts.framework.core;

public interface StageWithPort {

	String getId();

	void executeWithPorts();

	StageWithPort getParentStage();

	void setParentStage(StageWithPort parentStage, int index);

	// void setListener(OnDisableListener listener);

	boolean isReschedulable();

	void onIsPipelineHead();

	void onStart();

	void onSignal(Signal signal, InputPort<?> inputPort);
}
