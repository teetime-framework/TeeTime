package teetime.framework;

public interface HeadStage extends StageWithPort {

	boolean shouldBeTerminated();

	void terminate();
}
