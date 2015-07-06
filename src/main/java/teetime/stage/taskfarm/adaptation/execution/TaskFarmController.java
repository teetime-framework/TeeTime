package teetime.stage.taskfarm.adaptation.execution;

import teetime.framework.DynamicOutputPort;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.exceptionHandling.TaskFarmControllerException;
import teetime.framework.pipe.IPipe;
import teetime.stage.basic.distributor.dynamic.CreatePortActionDistributor;
import teetime.stage.basic.distributor.dynamic.DynamicDistributor;
import teetime.stage.basic.merger.dynamic.CreatePortActionMerger;
import teetime.stage.taskfarm.ITaskFarmDuplicable;
import teetime.stage.taskfarm.TaskFarmConfiguration;
import teetime.stage.taskfarm.TaskFarmTriple;
import teetime.util.framework.port.PortAction;

/**
 * The TaskFarmController is able to dynamically add stages to and remove
 * stages from a Task Farm with the same type at runtime.
 *
 * @author Christian Claus Wiechmann
 *
 * @param <I>
 *            Input type of Task Farm
 * @param <O>
 *            Output type of Task Farm
 * @param <T>
 *            Type of enclosed Stage (must extend {@link ITaskFarmDuplicable})
 */
public class TaskFarmController<I, O, T extends ITaskFarmDuplicable<I, O>> {

	private final TaskFarmConfiguration<I, O, T> configuration;

	/**
	 * Constructor.
	 *
	 * @param configuration
	 *            TaskFarmConfiguration of the Task Farm which
	 *            this controller is used for
	 */
	public TaskFarmController(final TaskFarmConfiguration<I, O, T> taskFarmConfiguration) {
		this.configuration = taskFarmConfiguration;
	}

	/**
	 * Dynamically adds a stage to the controlled task farm.
	 */
	public void addStageToTaskFarm() {
		@SuppressWarnings("unchecked")
		final T newStage = (T) this.configuration.getFirstStage().duplicate();

		final CreatePortActionMerger<O> mergerPortAction =
				new CreatePortActionMerger<O>(newStage.getOutputPort());
		this.configuration.getMerger().addPortActionRequest(mergerPortAction);
		mergerPortAction.waitForCompletion();

		final PortAction<DynamicDistributor<I>> distributorPortAction =
				new CreatePortActionDistributor<I>(newStage.getInputPort());
		this.configuration.getDistributor().addPortActionRequest(distributorPortAction);

		this.addNewTaskFarmTriple(newStage);
	}

	private void addNewTaskFarmTriple(final T newStage) {
		final TaskFarmTriple<I, O, T> newTriple =
				new TaskFarmTriple<I, O, T>(
						newStage.getInputPort().getPipe(),
						newStage.getOutputPort().getPipe(),
						newStage);
		this.configuration.getTriples().add(newTriple);
	}

	/**
	 * Dynamically removes a stage from the controlled task farm.
	 */
	public void removeStageFromTaskFarm() {
		final ITaskFarmDuplicable<I, O> stageToBeRemoved = this.getStageToBeRemoved();
		final OutputPort<?> distributorOutputPort = this.getRemoveableDistributorOutputPort(stageToBeRemoved);
		// final InputPort<?> mergerInputPort = this.getRemoveableMergerInputPort(stageToBeRemoved);

		try {
			@SuppressWarnings("unchecked")
			final PortAction<DynamicDistributor<I>> distributorPortAction =
					new teetime.stage.basic.distributor.dynamic.RemovePortAction<I>((DynamicOutputPort<I>) distributorOutputPort);
			this.configuration.getDistributor().addPortActionRequest(distributorPortAction);

			// @SuppressWarnings("unchecked")
			// final PortAction<DynamicMerger<O>> mergerPortAction =
			// new teetime.stage.basic.merger.dynamic.RemovePortAction<O>((DynamicInputPort<O>) mergerInputPort);
			// this.configuration.getMerger().addPortActionRequest(mergerPortAction);
		} catch (ClassCastException e) {
			throw new TaskFarmControllerException("Merger and Distributor have a different type than the Task Farm or the Task Farm Controller.");
		}
	}

	private InputPort<?> getRemoveableMergerInputPort(final ITaskFarmDuplicable<I, O> stageToBeRemoved) {
		final InputPort<?> mergerInputPort = stageToBeRemoved.getOutputPort().getPipe().getTargetPort();
		return mergerInputPort;
	}

	private OutputPort<?> getRemoveableDistributorOutputPort(final ITaskFarmDuplicable<I, O> stageToBeRemoved) {
		final OutputPort<?>[] potentialDistributorOutputPorts = this.configuration.getDistributor().getOutputPorts();
		OutputPort<?> distributorOutputPort = null;
		for (int i = 0; i < potentialDistributorOutputPorts.length; i++) {
			final IPipe pipe = potentialDistributorOutputPorts[i].getPipe();
			if (pipe.equals(stageToBeRemoved.getInputPort().getPipe())) {
				distributorOutputPort = potentialDistributorOutputPorts[i];
				break;
			}
		}

		if (distributorOutputPort == null) {
			throw new TaskFarmControllerException("The pipe between Distributor and enclosed Stage to be removed does not exist.");
		}

		return distributorOutputPort;
	}

	private ITaskFarmDuplicable<I, O> getStageToBeRemoved() {
		final TaskFarmTriple<I, O, T> triple =
				this.configuration.getTriples().get(this.configuration.getTriples().size() - 1);
		return triple.getStage();
	}
}
