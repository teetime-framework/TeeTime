package teetime.framework;

@Deprecated
public class OldHeadPipeline<FirstStage extends AbstractBasicStage, LastStage extends IStage> extends OldPipeline<FirstStage, LastStage> implements IStage {

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
