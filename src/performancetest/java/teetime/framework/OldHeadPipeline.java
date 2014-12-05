package teetime.framework;

@Deprecated
public class OldHeadPipeline<FirstStage extends Stage, LastStage extends Stage> extends OldPipeline<FirstStage, LastStage> {

	public OldHeadPipeline() {}

	public OldHeadPipeline(final String name) {}

	@Override
	public boolean shouldBeTerminated() {
		return this.firstStage.shouldBeTerminated();
	}

	@Override
	public void terminate() {
		this.firstStage.terminate();
	}
}
