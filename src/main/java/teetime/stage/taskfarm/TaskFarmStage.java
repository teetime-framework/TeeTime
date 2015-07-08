package teetime.stage.taskfarm;

import teetime.framework.AbstractCompositeStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;

/**
 * The TaskFarmStage implements the task farm parallelization pattern in
 * TeeTime. It dynamically adds CPU resources at runtime depending on
 * the current CPU load and the behavior of the enclosed stage.
 *
 * @author Christian Claus Wiechmann
 *
 * @param <I>
 *            Input type of Task Farm
 * @param <O>
 *            Output type of Task Farm
 * @param <T>
 *            Type of enclosed stage
 */
public class TaskFarmStage<I, O, T extends ITaskFarmDuplicable<I, O>> extends AbstractCompositeStage {

	private final TaskFarmConfiguration<I, O, T> configuration;

	/**
	 * Constructor.
	 *
	 * @param workerStage
	 *            instance of enclosed stage
	 * @param context
	 *            current execution context
	 */
	public TaskFarmStage(final T workerStage) {
		super();
		this.configuration = new TaskFarmConfiguration<I, O, T>(workerStage);

		this.init(workerStage);
	}

	public InputPort<I> getInputPort() {
		return this.configuration.getDistributor().getInputPort();
	}

	public OutputPort<O> getOutputPort() {
		return this.configuration.getMerger().getOutputPort();
	}

	private void init(final T includedStage) {
		addThreadableStage(this.configuration.getMerger());
		addThreadableStage(this.configuration.getFirstStage().getInputPort().getOwningStage());

		final InputPort<I> stageInputPort = includedStage.getInputPort();
		connectPorts(this.configuration.getDistributor().getNewOutputPort(), stageInputPort);

		final OutputPort<O> stageOutputPort = includedStage.getOutputPort();
		connectPorts(stageOutputPort, this.configuration.getMerger().getNewInputPort());

		// TODO: Check pipes at start somehow... Here, it would only be an InstantiationPipe.
		// checkIfPipeIsMonitorable(stageInputPort.getPipe());
		// checkIfPipeIsMonitorable(stageOutputPort.getPipe());

		this.configuration.getTriples().add(new TaskFarmTriple<I, O, T>(
				stageInputPort.getPipe(),
				stageOutputPort.getPipe(),
				includedStage));
	}

	// private void checkIfPipeIsMonitorable(final IPipe pipe) {
	// if (!(pipe instanceof IMonitorablePipe)) {
	// throw new TaskFarmInvalidPipeException("Pipe is not monitorable, which is required for a Task Farm. Instead \"" + pipe.getClass().getSimpleName()
	// + "\" was used.");
	// }
	// }

	public TaskFarmConfiguration<I, O, T> getConfiguration() {
		return this.configuration;
	}
}
