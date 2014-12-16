package teetime.framework;

import java.util.LinkedList;
import java.util.List;

import teetime.framework.pipe.PipeFactoryRegistry;

/**
 * Represents a configuration of connected stages, which is needed to run a analysis.
 * Stages can be added by executing {@link #addThreadableStage(Stage)}.
 */
public class AnalysisConfiguration {

	protected static final PipeFactoryRegistry PIPE_FACTORY_REGISTRY = PipeFactoryRegistry.INSTANCE;
	private final List<Stage> threadableStageJobs = new LinkedList<Stage>();

	public AnalysisConfiguration() {}

	List<Stage> getThreadableStageJobs() { 
		return this.threadableStageJobs;
	}

	/**
	 * Execute this method, to add a stage to the configuration, which should be executed in a own thread.
	 *
	 * @param stage
	 *            A arbitrary stage, which will be added to the configuration und executed in a thread.
	 */
	public void addThreadableStage(final Stage stage) {
		this.threadableStageJobs.add(stage);
	}

}
