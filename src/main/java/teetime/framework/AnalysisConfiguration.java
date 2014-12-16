package teetime.framework;

import java.util.LinkedList;
import java.util.List;

import teetime.framework.pipe.PipeFactoryRegistry;

public class AnalysisConfiguration {

	protected static final PipeFactoryRegistry PIPE_FACTORY_REGISTRY = PipeFactoryRegistry.INSTANCE;
	private final List<Stage> threadableStageJobs = new LinkedList<Stage>();

	public AnalysisConfiguration() {}

	List<Stage> getThreadableStageJobs() { 
		return this.threadableStageJobs;
	}

	public void addThreadableStage(final Stage stage) {
		this.threadableStageJobs.add(stage);
	}

}
