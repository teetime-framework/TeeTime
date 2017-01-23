package teetime.stage.taskfarm;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static teetime.framework.test.StageTester.*;

import java.util.*;

import org.junit.Test;

import teetime.framework.AbstractStage;
import teetime.stage.Counter;
import teetime.stage.basic.distributor.strategy.BlockingBusyWaitingRoundRobinStrategy;
import teetime.stage.basic.merger.strategy.BusyWaitingRoundRobinStrategy;
import teetime.testutil.ArrayCreator;

public class StaticTaskFarmStageTest {

	private static class TestTuple {
		final int numWorkerStages;
		final List<Integer> inputElements;
		// List<Integer> expectedOutputElements;
		Integer[] expectedOutputElements;

		TestTuple(final int numWorkerStages, final List<Integer> inputElements) {
			super();
			this.numWorkerStages = numWorkerStages;
			this.inputElements = inputElements;
		}

		static TestTuple use(final int numWorkerStages, final Integer... inputElements) {
			return new TestTuple(numWorkerStages, Arrays.asList(inputElements));
		}

		static TestTuple use(final int numWorkerStages, final List<Integer> inputElements) {
			return new TestTuple(numWorkerStages, inputElements);
		}

		TestTuple expect(final Integer... expectedOutputElements) {
			this.expectedOutputElements = expectedOutputElements;
			return this;
		}

		TestTuple expect(final List<Integer> expectedOutputElements) {
			this.expectedOutputElements = expectedOutputElements.toArray(new Integer[0]);
			return this;
		}

	}

	private static final long SEED = 1234567890;

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
	public void testStaticTaskFarmStageWithSingleStage() throws Exception {
		ArrayCreator creator = new ArrayCreator(SEED);
		List<Integer> randomNumbers = creator.createFilledList(1024);

		TestTuple[] testInputTuples = {
			// tuple semantics: number of worker stages, input elements, and (expect) output elements
			TestTuple.use(1, 1, 2).expect(1, 2),
			TestTuple.use(1, randomNumbers).expect(randomNumbers), // expect in any order
		};

		for (TestTuple testTuple : testInputTuples) {
			StaticTaskFarmStage<Integer, Integer, Counter<Integer>> taskFarmStage = createTaskFarm(testTuple.numWorkerStages);

			List<Integer> outputElements = new ArrayList<Integer>();

			AbstractStage firstInternalStage = taskFarmStage.getInputPort().getOwningStage();
			test(firstInternalStage).and()
					.send(testTuple.inputElements).to(taskFarmStage.getInputPort()).and()
					.receive(outputElements).from(taskFarmStage.getOutputPort())
					.start();

			assertThat(outputElements, contains(testTuple.expectedOutputElements));
		}
	}

	@Test
	public void testOrderedStaticTaskFarmStageWithMultipleStage() throws Exception {
		ArrayCreator creator = new ArrayCreator(SEED);
		List<Integer> randomNumbers = creator.createFilledList(1024);

		TestTuple[] testInputTuples = {
			// tuple semantics: number of worker stages, input elements, and (expect) output elements
			TestTuple.use(2, randomNumbers).expect(randomNumbers), // expect in correct order
			TestTuple.use(3, randomNumbers).expect(randomNumbers), // expect in correct order
		};

		for (TestTuple testTuple : testInputTuples) {
			StaticTaskFarmStage<Integer, Integer, Counter<Integer>> taskFarmStage = createTaskFarm(testTuple.numWorkerStages);
			// ordered element passing
			taskFarmStage.getDistributor().setStrategy(new BlockingBusyWaitingRoundRobinStrategy());
			taskFarmStage.getMerger().setStrategy(new BusyWaitingRoundRobinStrategy());

			List<Integer> outputElements = new ArrayList<Integer>();

			AbstractStage firstInternalStage = taskFarmStage.getInputPort().getOwningStage();
			test(firstInternalStage).and()
					.send(testTuple.inputElements).to(taskFarmStage.getInputPort()).and()
					.receive(outputElements).from(taskFarmStage.getOutputPort())
					.start();

			assertThat(outputElements, contains(testTuple.expectedOutputElements));
		}
	}

	private StaticTaskFarmStage<Integer, Integer, Counter<Integer>> createTaskFarm(final int numWorkerStages) {
		Counter<Integer> workerStage = new Counter<Integer>();
		StaticTaskFarmStage<Integer, Integer, Counter<Integer>> taskFarmStage = new StaticTaskFarmStage<Integer, Integer, Counter<Integer>>(workerStage,
				numWorkerStages);

		return taskFarmStage;
	}

}
