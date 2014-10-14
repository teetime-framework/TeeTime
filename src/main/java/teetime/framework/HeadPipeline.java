package teetime.framework;

public class HeadPipeline<FirstStage extends HeadStage, LastStage extends StageWithPort> extends OldPipeline<FirstStage, LastStage> implements HeadStage {

	public HeadPipeline() {}

	public HeadPipeline(final String name) {}

	@Override
	public boolean shouldBeTerminated() {
		return this.firstStage.shouldBeTerminated();
	}

	@Override
	public void terminate() {
		this.firstStage.terminate();
	}
}
