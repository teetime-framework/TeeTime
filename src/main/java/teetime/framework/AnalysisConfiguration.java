package teetime.framework;

import java.util.LinkedList;
import java.util.List;

import teetime.framework.pipe.PipeFactoryRegistry;

public class AnalysisConfiguration {

	protected static final PipeFactoryRegistry PIPE_FACTORY_REGISTRY = PipeFactoryRegistry.INSTANCE;

	private final List<Stage> threadableStageJobs = new LinkedList<Stage>();

	List<Stage> getThreadableStageJobs() {
		return threadableStageJobs;
	}

	public void addThreadableStage(final Stage stage) {
		threadableStageJobs.add(stage);
	}

}
