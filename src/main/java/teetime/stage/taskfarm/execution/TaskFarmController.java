package teetime.stage.taskfarm.execution;

import teetime.framework.DynamicInputPort;
import teetime.framework.DynamicOutputPort;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.IPipe;
import teetime.stage.basic.distributor.dynamic.DynamicDistributor;
import teetime.stage.basic.merger.dynamic.DynamicMerger;
import teetime.stage.taskfarm.TaskFarmConfiguration;
import teetime.stage.taskfarm.TaskFarmDuplicable;
import teetime.stage.taskfarm.TaskFarmTriple;
import teetime.util.framework.port.PortAction;

public class TaskFarmController<I, O, TFS extends TaskFarmDuplicable<I, O>> {

	final private TaskFarmConfiguration<I, O, TFS> configuration;

	public TaskFarmController(final TaskFarmConfiguration<I, O, TFS> taskFarmConfiguration) {
		this.configuration = taskFarmConfiguration;
	}

	public void addStageToTaskFarm() {
		@SuppressWarnings("unchecked")
		TFS newStage = (TFS) configuration.getFirstStage().duplicate();

		PortAction<DynamicMerger<O>> mergerPortAction =
				new teetime.stage.basic.merger.dynamic.CreatePortAction<O>(newStage.getOutputPort());
		configuration.getMerger().addPortActionRequest(mergerPortAction);

		PortAction<DynamicDistributor<I>> distributorPortAction =
				new teetime.stage.basic.distributor.dynamic.CreatePortAction<I>(newStage.getInputPort());
		configuration.getDistributor().addPortActionRequest(distributorPortAction);

		addNewTaskFarmTriple(newStage);
	}

	private void addNewTaskFarmTriple(TFS newStage) {
		TaskFarmTriple<I, O, TFS> newTriple =
				new TaskFarmTriple<I, O, TFS>(
						newStage.getInputPort().getPipe(),
						newStage.getOutputPort().getPipe(),
						newStage);
		configuration.getTriples().add(newTriple);
	}

	public void removeStageFromTaskFarm() {
		// FIXME: Do not remove if number of stages == 1

		TaskFarmDuplicable<I, O> stageToBeRemoved = getStageToBeRemoved();
		OutputPort<?> distributorOutputPort = getRemoveableDistributorOutputPort(stageToBeRemoved);
		InputPort<?> mergerInputPort = getRemoveableMergerInputPort(stageToBeRemoved);

		try {
			@SuppressWarnings("unchecked")
			PortAction<DynamicDistributor<I>> distributorPortAction =
					new teetime.stage.basic.distributor.dynamic.RemovePortAction<I>((DynamicOutputPort<I>) distributorOutputPort);
			configuration.getDistributor().addPortActionRequest(distributorPortAction);

			@SuppressWarnings("unchecked")
			PortAction<DynamicMerger<O>> mergerPortAction =
					new teetime.stage.basic.merger.dynamic.RemovePortAction<O>((DynamicInputPort<O>) mergerInputPort);
			configuration.getMerger().addPortActionRequest(mergerPortAction);
		} catch (ClassCastException e) {
			// TODO: Exception thrown because of wrong types. Should not happen, theoretically.
		}
	}

	private InputPort<?> getRemoveableMergerInputPort(final TaskFarmDuplicable<I, O> stageToBeRemoved) {
		InputPort<?> mergerInputPort = stageToBeRemoved.getOutputPort().getPipe().getTargetPort();
		return mergerInputPort;
	}

	private OutputPort<?> getRemoveableDistributorOutputPort(final TaskFarmDuplicable<I, O> stageToBeRemoved) {
		OutputPort<?>[] potentialDistributorOutputPorts = configuration.getDistributor().getOutputPorts();
		OutputPort<?> distributorOutputPort = null;
		for (int i = 0; i < potentialDistributorOutputPorts.length; i++) {
			IPipe pipe = potentialDistributorOutputPorts[i].getPipe();
			if (pipe.equals(stageToBeRemoved.getInputPort().getPipe())) {
				distributorOutputPort = potentialDistributorOutputPorts[i];
				break;
			}
		}

		if (distributorOutputPort == null) {
			// TODO: Better exception
			throw new RuntimeException();
		}

		return distributorOutputPort;
	}

	private TaskFarmDuplicable<I, O> getStageToBeRemoved() {
		// just get last added stage
		TaskFarmTriple<I, O, TFS> triple = configuration.getTriples().get(configuration.getTriples().size() - 1);
		return triple.getStage();
	}
}
