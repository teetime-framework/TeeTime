package teetime.stage;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import teetime.framework.Analysis;
import teetime.framework.AnalysisConfiguration;
import teetime.framework.OutputPort;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;

public class MultipleInstanceOfFilterTest {

	private static class TestConfiguration extends AnalysisConfiguration {

		public TestConfiguration(final List<Number> initialInput, final List<Integer> integerList, final List<Float> floatList) {
			// Create the stages
			final InitialElementProducer<Number> producer = new InitialElementProducer<Number>(initialInput.toArray(new Number[0]));
			final MultipleInstanceOfFilter<Number> filter = new MultipleInstanceOfFilter<Number>();
			final CollectorSink<Integer> integerSink = new CollectorSink<Integer>(integerList);
			final CollectorSink<Float> floatSink = new CollectorSink<Float>(floatList);

			// Connect the stages
			final IPipeFactory factory = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);
			factory.create(producer.getOutputPort(), filter.getInputPort());
			factory.create(filter.getOutputPortForType(Integer.class), integerSink.getInputPort());
			factory.create(filter.getOutputPortForType(Float.class), floatSink.getInputPort());

			super.addThreadableStage(producer);
		}

	}

	@Test
	@SuppressWarnings("unchecked")
	public void filteringShouldWork() {
		final List<Number> initialInput = new ArrayList<Number>(Arrays.asList(1, 1.5f, 2, 2.5f, 3, 3.5f));
		final List<Integer> integerList = new ArrayList<Integer>();
		final List<Float> floatList = new ArrayList<Float>();

		final Analysis analysis = new Analysis(new TestConfiguration(initialInput, integerList, floatList));
		analysis.init();
		try {
			analysis.start();
		} catch (Exception e) {
			assertTrue(false);
		}

		assertThat(integerList, contains(1, 2, 3));
		assertThat(floatList, contains(1.5f, 2.5f, 3.5f));
	}

	@Test
	public void outputPortForSameTypeShouldBeCached() {
		final MultipleInstanceOfFilter<Number> filter = new MultipleInstanceOfFilter<Number>();

		final OutputPort<Float> firstPort = filter.getOutputPortForType(Float.class);
		final OutputPort<Float> secondPort = filter.getOutputPortForType(Float.class);

		assertThat(firstPort, is(secondPort));
	}

}
