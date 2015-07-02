package teetime.stage.taskfarm;

import teetime.framework.AbstractCompositeStage;
import teetime.framework.ConfigurationContext;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.exceptionHandling.TaskFarmInvalidPipeException;
import teetime.framework.pipe.IMonitorablePipe;
import teetime.framework.pipe.IPipe;

public class TaskFarmStage<I, O, TFS extends TaskFarmDuplicable<I, O>> extends AbstractCompositeStage {

	private final TaskFarmConfiguration<I, O, TFS> configuration;

	public TaskFarmStage(final TFS workerStage, final ConfigurationContext context) {
		super(context);
		configuration = new TaskFarmConfiguration<I, O, TFS>(workerStage);

		init(workerStage);
	}

	public InputPort<I> getInputPort() {
		return configuration.getDistributor().getInputPort();
	}

	public OutputPort<O> getOutputPort() {
		return configuration.getMerger().getOutputPort();
	}

	private void init(final TFS includedStage) {

		InputPort<I> stageInputPort = includedStage.getInputPort();
		connectPorts(configuration.getDistributor().getNewOutputPort(), stageInputPort);

		OutputPort<O> stageOutputPort = includedStage.getOutputPort();
		connectPorts(stageOutputPort, configuration.getMerger().getNewInputPort());

		checkIfPipeIsMonitorable(stageInputPort.getPipe());
		checkIfPipeIsMonitorable(stageOutputPort.getPipe());
		configuration.getTriples().put(0, new TaskFarmTriple<I, O, TFS>(
				(IMonitorablePipe) stageInputPort.getPipe(),
				(IMonitorablePipe) stageOutputPort.getPipe(),
				includedStage));

		addThreadableStage(configuration.getMerger());
		addThreadableStage(configuration.getFirstStage().getInputPort().getOwningStage());
	}

	private void checkIfPipeIsMonitorable(final IPipe pipe) {
		if (!(pipe instanceof IMonitorablePipe)) {
			throw new TaskFarmInvalidPipeException("Pipe is not monitorable, which is required for a Task Farm.");
		}
	}

	public TaskFarmConfiguration<?, ?, ?> getConfiguration() {
		return this.configuration;
	}
}
