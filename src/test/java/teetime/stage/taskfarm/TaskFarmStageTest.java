package teetime.stage.taskfarm;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import teetime.framework.AbstractCompositeStage;
import teetime.framework.AbstractConsumerStage;
import teetime.framework.Configuration;
import teetime.framework.ConfigurationContext;
import teetime.framework.Execution;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;

public class TaskFarmStageTest {

	private static final int NUMBER_OF_TEST_ELEMENTS = 1000;

	@Test
	public void simpleTaskFarmStageTest() {
		final TestConfiguration configuration = new TestConfiguration();
		final Execution<TestConfiguration> execution = new Execution<TestConfiguration>(configuration);

		execution.executeBlocking();

		final List<String> results = configuration.getCollection();
		for (int i = 1; i <= NUMBER_OF_TEST_ELEMENTS; i++) {
			final int n = i + 1;
			final String s = Integer.toString(n) + Integer.toString(n) + Integer.toString(n) + Integer.toString(n);
			assertTrue("Does not contain: " + s, results.contains(s));
		}
		assertThat(results.size(), is(equalTo(NUMBER_OF_TEST_ELEMENTS)));
	}

	private class PlusOneInStringStage extends AbstractConsumerStage<Integer> {

		private final OutputPort<String> outputPort = this.createOutputPort();

		@Override
		protected void execute(final Integer element) {
			final Integer x = element + 1;
			this.outputPort.send(x.toString());
		}

		public OutputPort<String> getOutputPort() {
			return this.outputPort;
		}
	}

	private class StringDuplicationStage extends AbstractConsumerStage<String> implements ITaskFarmDuplicable<String, String> {

		private final OutputPort<String> outputPort = this.createOutputPort();

		@Override
		protected void execute(final String element) {
			this.outputPort.send(element + element);
		}

		@Override
		public OutputPort<String> getOutputPort() {
			return outputPort;
		}

		@Override
		public ITaskFarmDuplicable<String, String> duplicate() {
			return new StringDuplicationStage();
		}
	}

	private class CompositeTestStage extends AbstractCompositeStage implements ITaskFarmDuplicable<Integer, String> {
		private final PlusOneInStringStage pOne = new PlusOneInStringStage();
		private final StringDuplicationStage sDup = new StringDuplicationStage();

		public CompositeTestStage(final ConfigurationContext context) {
			super(context);
			connectPorts(this.pOne.getOutputPort(), this.sDup.getInputPort());
		}

		@Override
		public InputPort<Integer> getInputPort() {
			return this.pOne.getInputPort();
		}

		@Override
		public OutputPort<String> getOutputPort() {
			return this.sDup.getOutputPort();
		}

		@Override
		public ITaskFarmDuplicable<Integer, String> duplicate() {
			return new CompositeTestStage(this.getContext());
		}

	}

	private class TestConfiguration extends Configuration {
		private final List<String> results = new LinkedList<String>();

		public TestConfiguration() {
			this.buildConfiguration();
		}

		private void buildConfiguration() {
			final List<Integer> values = new LinkedList<Integer>();
			for (int i = 1; i <= NUMBER_OF_TEST_ELEMENTS; i++) {
				values.add(i);
			}
			final InitialElementProducer<Integer> initialElementProducer = new InitialElementProducer<Integer>(values);
			final CompositeTestStage compositeTestStage = new CompositeTestStage(this.getContext());
			final CollectorSink<String> collectorSink = new CollectorSink<String>(this.results);

			final TaskFarmStage<Integer, String, CompositeTestStage> taskFarmStage =
					new TaskFarmStage<Integer, String, CompositeTestStage>(compositeTestStage, this.getContext());

			final StringDuplicationStage additionalDuplication = new StringDuplicationStage();
			final TaskFarmStage<String, String, StringDuplicationStage> secondTaskFarmStage =
					new TaskFarmStage<String, String, StringDuplicationStage>(additionalDuplication, this.getContext());

			connectPorts(initialElementProducer.getOutputPort(), taskFarmStage.getInputPort());
			connectPorts(taskFarmStage.getOutputPort(), secondTaskFarmStage.getInputPort());
			connectPorts(secondTaskFarmStage.getOutputPort(), collectorSink.getInputPort());
		}

		public List<String> getCollection() {
			return this.results;
		}
	}
}
