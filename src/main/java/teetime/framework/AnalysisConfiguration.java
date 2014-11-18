package teetime.framework;

import java.util.LinkedList;
import java.util.List;

import teetime.framework.pipe.PipeFactoryRegistry;

public class AnalysisConfiguration {

	protected static final PipeFactoryRegistry PIPE_FACTORY_REGISTRY = PipeFactoryRegistry.INSTANCE;
	private final List<IStage> threadableStageJobs = new LinkedList<IStage>();

	public AnalysisConfiguration() {}

	List<IStage> getThreadableStageJobs() {
		return this.threadableStageJobs;
	}

	public void addThreadableStage(final IStage stage) {
		this.threadableStageJobs.add(stage);
	}

}
