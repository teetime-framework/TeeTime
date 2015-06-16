package teetime.framework;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.InstantiationPipe;
import teetime.framework.pipe.SingleElementPipeFactory;
import teetime.framework.pipe.SpScPipeFactory;
import teetime.framework.pipe.UnboundedSpScPipeFactory;

class AnalysisInstantiation {

	private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisInstantiation.class);

	private static final IPipeFactory interBoundedThreadPipeFactory = new SpScPipeFactory();
	private static final IPipeFactory interUnboundedThreadPipeFactory = new UnboundedSpScPipeFactory();
	private static final IPipeFactory intraThreadPipeFactory = new SingleElementPipeFactory();

	@SuppressWarnings("rawtypes")
	static Integer colorAndConnectStages(final Integer i, final Map<Stage, Integer> colors, final Stage threadableStage, final AnalysisConfiguration configuration) {
		Integer createdConnections = new Integer(0);
		Set<Stage> threadableStageJobs = configuration.getThreadableStages();
		for (OutputPort outputPort : threadableStage.getOutputPorts()) {
			if (outputPort.pipe != null) {
				if (outputPort.pipe instanceof InstantiationPipe) {
					InstantiationPipe pipe = (InstantiationPipe) outputPort.pipe;
					Stage targetStage = pipe.getTargetPort().getOwningStage();
					Integer targetColor = new Integer(0);
					if (colors.containsKey(targetStage)) {
						targetColor = colors.get(targetStage);
					}
					if (threadableStageJobs.contains(targetStage) && targetColor.compareTo(i) != 0) {
						if (pipe.getCapacity() != 0) {
							interBoundedThreadPipeFactory.create(outputPort, pipe.getTargetPort(), pipe.getCapacity());
						} else {
							interUnboundedThreadPipeFactory.create(outputPort, pipe.getTargetPort(), 4);
						}
					} else {
						if (colors.containsKey(targetStage)) {
							if (!colors.get(targetStage).equals(i)) {
								throw new IllegalStateException("Crossing threads"); // One stage is connected to a stage of another thread (but not its "headstage")
							}
						}
						intraThreadPipeFactory.create(outputPort, pipe.getTargetPort());
						colors.put(targetStage, i);
						createdConnections += colorAndConnectStages(i, colors, targetStage, configuration);
					}
					createdConnections++;
				}
			}

		}
		return createdConnections;
	}

	static void instantiatePipes(final AnalysisConfiguration configuration) {
		Integer i = new Integer(0);
		Map<Stage, Integer> colors = new HashMap<Stage, Integer>();
		Set<Stage> threadableStageJobs = configuration.getThreadableStages();
		Integer createdConnections = 0;
		for (Stage threadableStage : threadableStageJobs) {
			i++;
			colors.put(threadableStage, i);
			createdConnections = AnalysisInstantiation.colorAndConnectStages(i, colors, threadableStage, configuration);
		}
		LOGGER.debug("Created " + createdConnections + "connections");
	}

}
