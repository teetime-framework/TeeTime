package teetime.stage.taskfarm.execution;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.Execution;
import teetime.framework.OutputPort;
import teetime.stage.taskfarm.TaskFarmDuplicable;

import com.google.common.collect.ListMultimap;

public class TaskFarmControllerTest {

	final static int NUMBER_OF_ITEMS = 1000;

	private static int numberOfEnclosedStage = 0;

	@Test
	public void test() {
		final TaskFarmControllerConfiguration configuration = new TaskFarmControllerConfiguration();
		final Execution<TaskFarmControllerConfiguration> execution = new Execution<TaskFarmControllerConfiguration>(configuration);

		execution.executeBlocking();

		ListMultimap<Integer, Integer> monitoredValues = configuration.getMonitoredValues();
		assertThat(monitoredValues.get(0).size(), is(greaterThan(0)));
		assertThat(monitoredValues.get(1).size(), is(greaterThan(0)));
		assertThat(monitoredValues.get(2).size(), is(greaterThan(0)));
		assertThat(monitoredValues.size(), is(equalTo(NUMBER_OF_ITEMS)));
	}

	static class SelfMonitoringPlusOneStage extends AbstractConsumerStage<Integer> implements TaskFarmDuplicable<Integer, Integer> {

		private final OutputPort<Integer> outputPort = this.createOutputPort();

		private final ListMultimap<Integer, Integer> monitoredValues;
		private final int myNumber;

		public SelfMonitoringPlusOneStage(final ListMultimap<Integer, Integer> monitoredValues) {
			this.monitoredValues = monitoredValues;
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
			return new SelfMonitoringPlusOneStage(monitoredValues);
		}
	}

	static class TaskFarmControllerControllerStage extends AbstractConsumerStage<Integer> {

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

}
