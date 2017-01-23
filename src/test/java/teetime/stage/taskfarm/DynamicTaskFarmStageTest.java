package teetime.stage.taskfarm;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import teetime.framework.Configuration;
import teetime.framework.Execution;
import teetime.stage.CollectorSink;
import teetime.stage.Counter;

public class DynamicTaskFarmStageTest {

	@Test
	@Ignore // ignore since class under test is not yet implemented
	public void testDynamicTaskFarmStage() throws Exception {
		final Integer[] elements = { 1, 2, 3 };

		final ElementTrigger<Integer> producer = new ElementTrigger<Integer>(elements);
		final DynamicTaskFarmStage<Integer, Integer, Counter<Integer>> dynamicTaskFarmStage = new DynamicTaskFarmStage<Integer, Integer, Counter<Integer>>(
				new Counter<Integer>(), 1);
		final CollectorSink<Integer> collectorSink = new CollectorSink<Integer>();

		Configuration configuration = new Configuration() {
			{
				connectPorts(producer.getOutputPort(), dynamicTaskFarmStage.getInputPort());
				connectPorts(dynamicTaskFarmStage.getOutputPort(), collectorSink.getInputPort());
			}
		};
		Execution<Configuration> execution = new Execution<Configuration>(configuration);
		execution.executeNonBlocking();

		producer.trigger();
		assertThat(dynamicTaskFarmStage.getMerger().isActive(), is(false));

		dynamicTaskFarmStage.addStageAtRuntime();
		producer.trigger();
		// assertThat(dynamicTaskFarmStage.getMerger().isActive(), is(true)); // TODO uncomment if "declareActive at runtime" is implemented

		dynamicTaskFarmStage.removeStageAtRuntime();
		producer.trigger();
		// assertThat(dynamicTaskFarmStage.getMerger().isActive(), is(false)); // TODO uncomment if "declareActive at runtime" is implemented

		execution.abortEventually();

	}
}
