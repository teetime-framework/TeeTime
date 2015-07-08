package teetime.stage.taskfarm.adaptation.execution;

import teetime.framework.DynamicOutputPort;
import teetime.framework.OutputPort;
import teetime.framework.exceptionHandling.TaskFarmControllerException;
import teetime.stage.basic.distributor.dynamic.CreatePortActionDistributor;
import teetime.stage.basic.distributor.dynamic.DynamicDistributor;
import teetime.stage.basic.merger.dynamic.CreatePortActionMerger;
import teetime.stage.taskfarm.ITaskFarmDuplicable;
import teetime.stage.taskfarm.TaskFarmStage;
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

	private final TaskFarmStage<I, O, T> taskFarmStage;

	/**
	 * Constructor.
	 *
	 * @param configuration
	 *            TaskFarmConfiguration of the Task Farm which
	 *            this controller is used for
	 */
	public TaskFarmController(final TaskFarmStage<I, O, T> taskFarmStage) {
		this.taskFarmStage = taskFarmStage;
	}

	/**
	 * Dynamically adds a stage to the controlled task farm.
	 */
	public void addStageToTaskFarm() {
		@SuppressWarnings("unchecked")
		final T newStage = (T) this.taskFarmStage.getBasicEnclosedStage().duplicate();

		final CreatePortActionMerger<O> mergerPortAction =
				new CreatePortActionMerger<O>(newStage.getOutputPort());
		this.taskFarmStage.getMerger().addPortActionRequest(mergerPortAction);
		mergerPortAction.waitForCompletion();

		final PortAction<DynamicDistributor<I>> distributorPortAction =
				new CreatePortActionDistributor<I>(newStage.getInputPort());
		this.taskFarmStage.getDistributor().addPortActionRequest(distributorPortAction);

		this.addNewTaskFarmTriple(newStage);
	}

	private void addNewTaskFarmTriple(final T newStage) {
		this.taskFarmStage.getEnclosedStageInstances().add(newStage);
	}

	/**
	 * Dynamically removes a stage from the controlled task farm.
	 */
	public void removeStageFromTaskFarm() {
		final ITaskFarmDuplicable<I, O> stageToBeRemoved = this.getStageToBeRemoved();
		final OutputPort<?> distributorOutputPort = this.getRemoveableDistributorOutputPort(stageToBeRemoved);

		try {
			@SuppressWarnings("unchecked")
			final PortAction<DynamicDistributor<I>> distributorPortAction =
					new teetime.stage.basic.distributor.dynamic.RemovePortAction<I>((DynamicOutputPort<I>) distributorOutputPort);
			this.taskFarmStage.getDistributor().addPortActionRequest(distributorPortAction);
		} catch (ClassCastException e) {
			throw new TaskFarmControllerException("Merger and Distributor have a different type than the Task Farm or the Task Farm Controller.");
		}
	}

	private OutputPort<?> getRemoveableDistributorOutputPort(final ITaskFarmDuplicable<I, O> stageToBeRemoved) {
		final OutputPort<?> distributorOutputPort = stageToBeRemoved.getInputPort().getPipe().getSourcePort();
		return distributorOutputPort;
	}

	private ITaskFarmDuplicable<I, O> getStageToBeRemoved() {
		return this.taskFarmStage.getEnclosedStageInstances().get(this.taskFarmStage.getEnclosedStageInstances().size() - 1);
	}

}
