package teetime.stage.taskfarm;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static teetime.framework.test.StageTester.*;

import java.util.*;

import org.junit.Test;

import teetime.framework.AbstractStage;
import teetime.stage.Counter;

public class StaticTaskFarmStageTest {

	private static class TestTuple {
		final int numWorkerStages;
		final Integer[] inputElements;
		Iterable<Integer> expectedOutputElements;

		TestTuple(final int numWorkerStages, final Integer... inputElements) {
			super();
			this.numWorkerStages = numWorkerStages;
			this.inputElements = inputElements;
		}

		static TestTuple use(final int numWorkerStages, final Integer... inputElements) {
			return new TestTuple(numWorkerStages, inputElements);
		}

		TestTuple expect(final Integer... expectedOutputElements) {
			this.expectedOutputElements = Arrays.asList(expectedOutputElements);
			return this;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNegativeNumberOfStages() throws Exception {
		new StaticTaskFarmStage<Integer, Integer, Counter<Integer>>(new Counter<Integer>(), 1, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testZeroNumberOfStages() throws Exception {
		new StaticTaskFarmStage<Integer, Integer, Counter<Integer>>(new Counter<Integer>(), 1, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNegativePipeCapacity() throws Exception {
		new StaticTaskFarmStage<Integer, Integer, Counter<Integer>>(new Counter<Integer>(), 1, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testZeroPipeCapacity() throws Exception {
		new StaticTaskFarmStage<Integer, Integer, Counter<Integer>>(new Counter<Integer>(), 1, 0);
	}

	@Test
	public void testStaticTaskFarmStage() throws Exception {
		TestTuple[] testInputTuples = {
			TestTuple.use(1, 1, 2).expect(1, 2),
			TestTuple.use(2, 1, 2).expect(1, 2),
		};

		for (TestTuple testTuple : testInputTuples) {
			Counter<Integer> workerStage = new Counter<Integer>();
			StaticTaskFarmStage<Integer, Integer, Counter<Integer>> taskFarmStage = new StaticTaskFarmStage<Integer, Integer, Counter<Integer>>(workerStage,
					testTuple.numWorkerStages);

			List<Integer> outputElements = new ArrayList<Integer>();

			AbstractStage firstInternalStage = taskFarmStage.getInputPort().getOwningStage();
			test(firstInternalStage).and()
					.send(testTuple.inputElements).to(taskFarmStage.getInputPort()).and()
					.receive(outputElements).from(taskFarmStage.getOutputPort())
					.start();

			assertThat(outputElements, is(testTuple.expectedOutputElements));
		}

	}

}
