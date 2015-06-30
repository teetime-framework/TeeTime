package teetime.stage.taskfarm;

import org.junit.Test;

@SuppressWarnings("deprecation")
public class TaskFarmStageTest {

	// private final static int NUMBER_OF_TEST_ELEMENTS = 1000;
	//
	// private class PlusOneInStringStage extends AbstractConsumerStage<Integer> {
	//
	// private final OutputPort<String> outputPort = this.createOutputPort();
	//
	// @Override
	// protected void execute(final Integer element) {
	// Integer x = element + 1;
	// this.outputPort.send(x.toString());
	// }
	//
	// public OutputPort<String> getOutputPort() {
	// return this.outputPort;
	// }
	// }

	// private class StringDuplicationStage extends AbstractConsumerStage<String> {
	//
	// private final OutputPort<String> outputPort = this.createOutputPort();
	//
	// @Override
	// protected void execute(final String element) {
	// this.outputPort.send(element + element);
	// }
	//
	// public OutputPort<String> getOutputPort() {
	// return outputPort;
	// }
	// }

	// private class CompositeTestStage extends AbstractCompositeStage implements TaskFarmDuplicable<Integer, String> {
	// PlusOneInStringStage pOne = new PlusOneInStringStage();
	// StringDuplicationStage sDup = new StringDuplicationStage();
	//
	// public CompositeTestStage() {
	// lastStages.add(sDup);
	// connectPorts(pOne.getOutputPort(), sDup.getInputPort());
	// }
	//
	// @Override
	// protected Stage getFirstStage() {
	// return pOne;
	// }
	//
	// @Override
	// public InputPort<Integer> getInputPort() {
	// return pOne.getInputPort();
	// }
	//
	// @Override
	// public OutputPort<String> getOutputPort() {
	// return sDup.getOutputPort();
	// }
	//
	// @Override
	// public TaskFarmDuplicable<Integer, String> duplicate() {
	// return new CompositeTestStage();
	// }
	//
	// }

	// private class TestConfiguration extends AnalysisConfiguration {
	// private final List<String> collection = new LinkedList<String>();
	//
	// public TestConfiguration() {
	// this.buildConfiguration();
	// }
	//
	// private void buildConfiguration() {
	// final Stage producerPipeline = this.buildProducerPipeline();
	// addThreadableStage(producerPipeline);
	// }
	//
	// private Stage buildProducerPipeline() {
	// List<Integer> values = new LinkedList<Integer>();
	// for (int i = 1; i <= NUMBER_OF_TEST_ELEMENTS; i++) {
	// values.add(i);
	// }
	// final InitialElementProducer<Integer> initialElementProducer = new InitialElementProducer<Integer>(values);
	// final CompositeTestStage compositeTestStage = new CompositeTestStage();
	// final CollectorSink<String> collectorSink = new CollectorSink<String>(collection);
	//
	// TaskFarmStage<Integer, String, CompositeTestStage> taskFarmStage = new TaskFarmStage<Integer, String, CompositeTestStage>(compositeTestStage);
	//
	// connectIntraThreads(initialElementProducer.getOutputPort(), taskFarmStage.getInputPort());
	// connectIntraThreads(taskFarmStage.getOutputPort(), collectorSink.getInputPort());
	//
	// return initialElementProducer;
	// }
	//
	// public List<String> getCollection() {
	// return this.collection;
	// }
	// }

	@Test
	public void simpleTaskFarmStageTest() {
		// TestConfiguration configuration = new TestConfiguration();
		//
		// final Analysis<TestConfiguration> analysis = new Analysis<TestConfiguration>(configuration);
		//
		// analysis.executeBlocking();
		//
		// List<String> result = configuration.getCollection();
		// assertTrue(result.contains("22"));
		// assertTrue(result.contains("33"));
		// assertTrue(result.contains("44"));
		// assertTrue(result.contains("55"));
		// assertTrue(result.contains("66"));
		// for (int i = 1; i <= NUMBER_OF_TEST_ELEMENTS; i++) {
		// int n = i + 1;
		// String s = Integer.toString(n) + Integer.toString(n);
		// assertTrue(s, result.contains(s));
		// }
		// assertTrue(Integer.toString(result.size()), result.size() == NUMBER_OF_TEST_ELEMENTS);
	}
}
