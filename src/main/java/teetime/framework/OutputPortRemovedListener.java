package teetime.framework;

public interface OutputPortRemovedListener {

	void onOutputPortRemoved(AbstractStage stage, OutputPort<?> removedOutputPort);

}
