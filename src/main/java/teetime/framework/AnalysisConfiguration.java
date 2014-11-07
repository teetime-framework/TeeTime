package teetime.framework;

import java.util.LinkedList;
import java.util.List;

import teetime.framework.pipe.PipeFactoryRegistry;

public class AnalysisConfiguration {

	protected static final PipeFactoryRegistry PIPE_FACTORY_REGISTRY = PipeFactoryRegistry.INSTANCE;

	private final List<Runnable> threadableStageJobs = new LinkedList<Runnable>();

	private final List<Stage> consumerStages = new LinkedList<Stage>();
	private final List<Stage> finiteProducerStages = new LinkedList<Stage>();
	private final List<Stage> infiniteProducerStages = new LinkedList<Stage>();

	public List<Stage> getConsumerStages() {
		return this.consumerStages;
	}

	public List<Stage> getFiniteProducerStages() {
		return this.finiteProducerStages;
	}

	public List<Stage> getInfiniteProducerStages() {
		return this.infiniteProducerStages;
	}

	public void addThreadableStage(final Stage stage) {
		// wrap the stage categorization in a runnable
		// because the termination strategy could depend on port configuration that is set later
		final Runnable addThreadableStageJob = new Runnable() {
			@Override
			public void run() {
				switch (stage.getTerminationStrategy()) {
				case BY_SIGNAL:
					consumerStages.add(stage);
					break;
				case BY_SELF_DECISION:
					finiteProducerStages.add(stage);
					break;
				case BY_INTERRUPT:
					infiniteProducerStages.add(stage);
					break;
				}
			}
		};

		threadableStageJobs.add(addThreadableStageJob);
	}

	void init() {
		for (Runnable job : threadableStageJobs) {
			job.run();
		}
	}

}
