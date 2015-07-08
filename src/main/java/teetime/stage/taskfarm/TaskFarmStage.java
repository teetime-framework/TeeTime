package teetime.stage.taskfarm;

import java.util.LinkedList;
import java.util.List;

import teetime.framework.AbstractCompositeStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.stage.basic.distributor.dynamic.DynamicDistributor;
import teetime.stage.basic.merger.dynamic.DynamicMerger;

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

	private final List<ITaskFarmDuplicable<I, O>> enclosedStageInstances = new LinkedList<ITaskFarmDuplicable<I, O>>();

	private final DynamicDistributor<I> distributor = new DynamicDistributor<I>();
	private final DynamicMerger<O> merger = new DynamicMerger<O>();

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
		this.configuration = new TaskFarmConfiguration<I, O, T>();

		this.init(workerStage);
	}

	private void init(final T includedStage) {
		addThreadableStage(this.merger);
		addThreadableStage(includedStage.getInputPort().getOwningStage());
	
		final InputPort<I> stageInputPort = includedStage.getInputPort();
		connectPorts(this.distributor.getNewOutputPort(), stageInputPort);
	
		final OutputPort<O> stageOutputPort = includedStage.getOutputPort();
		connectPorts(stageOutputPort, this.merger.getNewInputPort());
	
		enclosedStageInstances.add(includedStage);
	}

	public InputPort<I> getInputPort() {
		return this.distributor.getInputPort();
	}

	public OutputPort<O> getOutputPort() {
		return this.merger.getOutputPort();
	}

	public TaskFarmConfiguration<I, O, T> getConfiguration() {
		return this.configuration;
	}

	public ITaskFarmDuplicable<I, O> getBasicEnclosedStage() {
		return enclosedStageInstances.get(0);
	}

	public List<ITaskFarmDuplicable<I, O>> getEnclosedStageInstances() {
		return enclosedStageInstances;
	}

	public DynamicDistributor<I> getDistributor() {
		return distributor;
	}

	public DynamicMerger<O> getMerger() {
		return merger;
	}
}
