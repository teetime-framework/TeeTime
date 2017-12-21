package teetime.framework.scheduling.pushpull;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import teetime.framework.Configuration;
import teetime.framework.Execution;
import teetime.framework.TeeTimeService;
import teetime.framework.scheduling.pushpullmodel.PushPullScheduling;
import teetime.stage.CollectorSink;
import teetime.stage.Counter;
import teetime.stage.StreamProducer;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ThreeStagesPushPullIT {

	private static final int NUM_ELEMENTS = 1_000_000;

	@Test
	public void shouldExecuteWith01Thread() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 1);
	}

	@Test
	public void shouldExecuteWith02Thread() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 2);
	}

	@Test
	public void shouldExecuteWith04Thread() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 4);
	}

	private void shouldExecutePipelineCorrectlyManyElements(final int numElements, final int numThreads) {
		List<Integer> processedElements = new ArrayList<>();

		IntStream inputElements = IntStream.iterate(0, i -> i + 1).limit(numElements);
		Configuration config = new Configuration()
				.from(new StreamProducer<>(inputElements))
				.to(new Counter<>())
				.end(new CollectorSink<>(processedElements));

		TeeTimeService scheduling = new PushPullScheduling(config);
		Execution<Configuration> execution = new Execution<>(config, true, scheduling);
		execution.executeBlocking();

		for (int i = 0; i < numElements; i++) {
			Integer actualElement = processedElements.get(i);
			assertThat(actualElement, is(i));
		}
		assertThat(processedElements, hasSize(numElements));
	}
}
