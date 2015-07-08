package teetime.stage.taskfarm;

import java.util.LinkedList;
import java.util.List;

import teetime.framework.Configuration;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;
import teetime.stage.taskfarm.TaskFarmStageTest.CompositeTestStage;
import teetime.stage.taskfarm.TaskFarmStageTest.StringDuplicationStage;

class TaskFarmStageConfiguration extends Configuration {

	private final List<String> results = new LinkedList<String>();

	public TaskFarmStageConfiguration(final TaskFarmStageTest taskFarmStageTest) {
		this.buildConfiguration();
	}

	private void buildConfiguration() {
		final List<Integer> values = new LinkedList<Integer>();
		for (int i = 1; i <= TaskFarmStageTest.NUMBER_OF_TEST_ELEMENTS; i++) {
			values.add(i);
		}
		final InitialElementProducer<Integer> initialElementProducer = new InitialElementProducer<Integer>(values);
		final CompositeTestStage compositeTestStage = new CompositeTestStage();
		final CollectorSink<String> collectorSink = new CollectorSink<String>(this.results);

		final TaskFarmStage<Integer, String, CompositeTestStage> taskFarmStage =
				new TaskFarmStage<Integer, String, CompositeTestStage>(compositeTestStage);

		final StringDuplicationStage additionalDuplication = new StringDuplicationStage();
		final TaskFarmStage<String, String, StringDuplicationStage> secondTaskFarmStage =
				new TaskFarmStage<String, String, StringDuplicationStage>(additionalDuplication);

		connectPorts(initialElementProducer.getOutputPort(), taskFarmStage.getInputPort());
		connectPorts(taskFarmStage.getOutputPort(), secondTaskFarmStage.getInputPort());
		connectPorts(secondTaskFarmStage.getOutputPort(), collectorSink.getInputPort());
	}

	public List<String> getCollection() {
		return this.results;
	}
}
