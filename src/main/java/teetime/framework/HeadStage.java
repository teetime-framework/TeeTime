package teetime.framework;

public interface HeadStage extends Stage {

	boolean shouldBeTerminated();

	void terminate();
}
