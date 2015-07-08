package teetime.stage.taskfarm.adaptation.execution;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import teetime.framework.Execution;
import teetime.stage.basic.AbstractFilter;
import teetime.stage.taskfarm.ITaskFarmDuplicable;

import com.google.common.collect.ListMultimap;

/**
 * @author Christian Claus Wiechmann
 */
public class TaskFarmControllerTest {

	static final int NUMBER_OF_ITEMS = 1000;

	private static int numberOfEnclosedStage = 0;

	@Test
	public void test() {
		final TaskFarmControllerConfiguration configuration = new TaskFarmControllerConfiguration();
		final Execution<TaskFarmControllerConfiguration> execution = new Execution<TaskFarmControllerConfiguration>(configuration);

		execution.executeBlocking();

		final ListMultimap<Integer, Integer> monitoredValues = configuration.getMonitoredValues();
		assertThat(monitoredValues.get(0).size(), is(greaterThan(0)));
		assertThat(monitoredValues.get(1).size(), is(greaterThan(0)));
		assertThat(monitoredValues.get(2).size(), is(greaterThan(0)));
		assertThat(monitoredValues.size(), is(equalTo(NUMBER_OF_ITEMS)));
	}

	/**
	 * @author Christian Claus Wiechmann
	 */
	static class SelfMonitoringPlusOneStage extends AbstractFilter<Integer> implements ITaskFarmDuplicable<Integer, Integer> {

		private final ListMultimap<Integer, Integer> monitoredValues;
		private final int stageId;

		public SelfMonitoringPlusOneStage(final ListMultimap<Integer, Integer> monitoredValues) {
			this.monitoredValues = monitoredValues;
			this.stageId = numberOfEnclosedStage;
			numberOfEnclosedStage++;
		}

		@Override
		protected void execute(final Integer element) {
			this.monitoredValues.put(this.stageId, element);
			final Integer x = element + 1;
			this.outputPort.send(x);
		}

		@Override
		public ITaskFarmDuplicable<Integer, Integer> duplicate() {
			return new SelfMonitoringPlusOneStage(monitoredValues);
		}
	}

	/**
	 * @author Christian Claus Wiechmann
	 */
	static class TaskFarmControllerControllerStage extends AbstractFilter<Integer> {

		private final TaskFarmController<?, ?, ?> controller;
		private int numberOfElements = 0;

		public TaskFarmControllerControllerStage(final TaskFarmController<?, ?, ?> controller) {
			this.controller = controller;
		}

		@Override
		protected void execute(final Integer element) {
			if (this.numberOfElements == NUMBER_OF_ITEMS * 0.3) {
				this.controller.addStageToTaskFarm();
			}

			if (this.numberOfElements == NUMBER_OF_ITEMS * 0.5) {
				this.controller.addStageToTaskFarm();
			}

			if (this.numberOfElements == NUMBER_OF_ITEMS * 0.7) {
				this.controller.removeStageFromTaskFarm();
			}

			this.outputPort.send(element);

			this.numberOfElements++;
		}

	}

}
