package teetime.stage.taskfarm.execution;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.Configuration;
import teetime.framework.Execution;
import teetime.framework.OutputPort;
import teetime.stage.InitialElementProducer;
import teetime.stage.basic.Sink;
import teetime.stage.taskfarm.TaskFarmDuplicable;
import teetime.stage.taskfarm.TaskFarmStage;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

public class TaskFarmControllerTest {

	private final static int NUMBER_OF_ITEMS = 1000;

	private static int numberOfEnclosedStage = 0;
	private ListMultimap<Integer, Integer> monitoredValues = null;

	@Test
	public void test() {
		final TestConfiguration configuration = new TestConfiguration();
		final Execution<TestConfiguration> execution = new Execution<TestConfiguration>(configuration);

		execution.executeBlocking();

		assertThat(monitoredValues.get(0).size(), is(greaterThan(0)));
		assertThat(monitoredValues.get(1).size(), is(greaterThan(0)));
		assertThat(monitoredValues.get(2).size(), is(greaterThan(0)));
		assertThat(monitoredValues.size(), is(equalTo(NUMBER_OF_ITEMS)));
	}

	private class SelfMonitoringPlusOneStage extends AbstractConsumerStage<Integer> implements TaskFarmDuplicable<Integer, Integer> {

		private final OutputPort<Integer> outputPort = this.createOutputPort();
		private final int myNumber;

		public SelfMonitoringPlusOneStage() {
			this.myNumber = numberOfEnclosedStage;
			numberOfEnclosedStage++;
		}

		@Override
		protected void execute(final Integer element) {
			monitoredValues.put(myNumber, element);
			Integer x = element + 1;
			this.outputPort.send(x);
		}

		@Override
		public OutputPort<Integer> getOutputPort() {
			return this.outputPort;
		}

		@Override
		public TaskFarmDuplicable<Integer, Integer> duplicate() {
			return new SelfMonitoringPlusOneStage();
		}
	}

	private class TaskFarmControllerControllerStage extends AbstractConsumerStage<Integer> {

		private final OutputPort<Integer> outputPort = this.createOutputPort();
		private final TaskFarmController<?, ?, ?> controller;
		private int numberOfElements = 0;

		public TaskFarmControllerControllerStage(final TaskFarmController<?, ?, ?> controller) {
			this.controller = controller;
		}

		@Override
		protected void execute(final Integer element) {
			if (numberOfElements == NUMBER_OF_ITEMS * 0.3) {
				System.out.println("Thread added");
				controller.addStageToTaskFarm();
			}

			if (numberOfElements == NUMBER_OF_ITEMS * 0.5) {
				System.out.println("Thread added");
				controller.addStageToTaskFarm();
			}

			if (numberOfElements == NUMBER_OF_ITEMS * 0.7) {
				System.out.println("Thread removed");
				controller.removeStageFromTaskFarm();
			}

			this.outputPort.send(element);

			numberOfElements++;
		}

		public OutputPort<Integer> getOutputPort() {
			return this.outputPort;
		}
	}

	private class TestConfiguration extends Configuration {

		public TestConfiguration() {
			ListMultimap<Integer, Integer> multimapNotSynchronized = LinkedListMultimap.create();
			monitoredValues = Multimaps.synchronizedListMultimap(multimapNotSynchronized);
			this.buildConfiguration();
		}

		private void buildConfiguration() {
			List<Integer> numbers = new LinkedList<Integer>();
			for (int i = 0; i < NUMBER_OF_ITEMS; i++) {
				numbers.add(i);
			}

			final InitialElementProducer<Integer> initialElementProducer =
					new InitialElementProducer<Integer>(numbers);

			SelfMonitoringPlusOneStage workerStage = new SelfMonitoringPlusOneStage();
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
	}

}
