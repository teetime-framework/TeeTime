package teetime.stage.taskfarm.execution;

import java.util.LinkedList;
import java.util.List;

import teetime.framework.Configuration;
import teetime.stage.InitialElementProducer;
import teetime.stage.basic.Sink;
import teetime.stage.taskfarm.TaskFarmStage;
import teetime.stage.taskfarm.execution.TaskFarmControllerTest.SelfMonitoringPlusOneStage;
import teetime.stage.taskfarm.execution.TaskFarmControllerTest.TaskFarmControllerControllerStage;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

class TaskFarmControllerConfiguration extends Configuration {

	private final ListMultimap<Integer, Integer> monitoredValues;

	public TaskFarmControllerConfiguration() {
		ListMultimap<Integer, Integer> multimapNotSynchronized = LinkedListMultimap.create();
		monitoredValues = Multimaps.synchronizedListMultimap(multimapNotSynchronized);
		this.buildConfiguration();
	}

	private void buildConfiguration() {
		List<Integer> numbers = new LinkedList<Integer>();
		for (int i = 0; i < TaskFarmControllerTest.NUMBER_OF_ITEMS; i++) {
			numbers.add(i);
		}

		final InitialElementProducer<Integer> initialElementProducer =
				new InitialElementProducer<Integer>(numbers);

		SelfMonitoringPlusOneStage workerStage = new SelfMonitoringPlusOneStage(monitoredValues);
		TaskFarmStage<Integer, Integer, SelfMonitoringPlusOneStage> taskFarmStage =
				new TaskFarmStage<Integer, Integer, SelfMonitoringPlusOneStage>(workerStage, this.getContext());

		TaskFarmController<Integer, Integer, SelfMonitoringPlusOneStage> controller =
				new TaskFarmController<Integer, Integer, SelfMonitoringPlusOneStage>(taskFarmStage.getConfiguration());
		TaskFarmControllerControllerStage taskFarmControllerControllerStage = new TaskFarmControllerControllerStage(controller);

		Sink<Integer> sink = new Sink<Integer>();

		connectPorts(initialElementProducer.getOutputPort(), taskFarmControllerControllerStage.getInputPort());
		connectPorts(taskFarmControllerControllerStage.getOutputPort(), taskFarmStage.getInputPort());
		connectPorts(taskFarmStage.getOutputPort(), sink.getInputPort());
	}

	public ListMultimap<Integer, Integer> getMonitoredValues() {
		return monitoredValues;
	}
}
