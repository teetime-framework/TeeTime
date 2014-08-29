package teetime.variant.methodcallWithPorts.framework.core;

public interface HeadStage extends StageWithPort {

	boolean shouldBeTerminated();

	void terminate();
}
