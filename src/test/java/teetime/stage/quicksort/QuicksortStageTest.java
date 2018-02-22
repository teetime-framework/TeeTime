package teetime.stage.quicksort;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static teetime.framework.test.StageTester.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import teetime.framework.AbstractStage;
import teetime.framework.test.StageTestResult;

public class QuicksortStageTest {

	private QuicksortStage quicksortStage;

	@Before
	public void setUp() throws Exception {
		quicksortStage = new QuicksortStage();
	}

	@Test
	public void sortElementsInDescendingOrder() throws Exception {
		AbstractStage stage = quicksortStage.getInputPort().getOwningStage();

		int[] unorderedElements = { 5, 4, 3, 2, 1 };
		int[] expectedElements = { 1, 2, 3, 4, 5 };

		StageTestResult testResult = test(stage).and().send(unorderedElements).to(quicksortStage.getInputPort()).and()
				.receive(new ArrayList<int[]>()).from(quicksortStage.getOutputPort()).and().start();

		List<? extends int[]> outputElements = testResult.getElementsFrom(quicksortStage.getOutputPort());

		assertThat(outputElements.get(0), is(expectedElements));
	}

	@Test
	public void sortElementsInRandomOrder() throws Exception {
		AbstractStage stage = quicksortStage.getInputPort().getOwningStage();

		int[] unorderedElements = { 4, 2, 5, 1, 3 };
		int[] expectedElements = { 1, 2, 3, 4, 5 };

		StageTestResult testResult = test(stage).and().send(unorderedElements).to(quicksortStage.getInputPort()).and()
				.receive(new ArrayList<int[]>()).from(quicksortStage.getOutputPort()).and().start();

		List<? extends int[]> outputElements = testResult.getElementsFrom(quicksortStage.getOutputPort());

		assertThat(outputElements.get(0), is(expectedElements));
	}

	@Test
	public void sortElementsInOrderedOrder() throws Exception {
		AbstractStage stage = quicksortStage.getInputPort().getOwningStage();

		int[] unorderedElements = { 1, 2, 3, 4, 5 };
		int[] expectedElements = { 1, 2, 3, 4, 5 };

		StageTestResult testResult = test(stage).and().send(unorderedElements).to(quicksortStage.getInputPort()).and()
				.receive(new ArrayList<int[]>()).from(quicksortStage.getOutputPort()).and().start();

		List<? extends int[]> outputElements = testResult.getElementsFrom(quicksortStage.getOutputPort());

		assertThat(outputElements.get(0), is(expectedElements));
	}

	@Test
	@Ignore("throws an exception (not intended)")
	public void sortEmptyInput() throws Exception {
		AbstractStage stage = quicksortStage.getInputPort().getOwningStage();

		int[] unorderedElements = {};
		int[] expectedElements = {};

		StageTestResult testResult = test(stage).and().send(unorderedElements).to(quicksortStage.getInputPort()).and()
				.receive(new ArrayList<int[]>()).from(quicksortStage.getOutputPort()).and().start();

		List<? extends int[]> outputElements = testResult.getElementsFrom(quicksortStage.getOutputPort());

		assertThat(outputElements.get(0), is(expectedElements));
	}

	@Test
	public void sortElementsInSequence() throws Exception {
		AbstractStage stage = quicksortStage.getInputPort().getOwningStage();

		int[][] unorderedElements = { { 1, 2, 3, 4, 5 }, { 5, 4, 3, 2, 1 }, { 4, 2, 5, 1, 3 } };
		int[] expectedElements = { 1, 2, 3, 4, 5 };

		StageTestResult testResult = test(stage).and().send(unorderedElements).to(quicksortStage.getInputPort()).and()
				.receive(new ArrayList<int[]>()).from(quicksortStage.getOutputPort()).and().start();

		List<? extends int[]> outputElements = testResult.getElementsFrom(quicksortStage.getOutputPort());

		assertThat(outputElements.get(0), is(expectedElements));
		assertThat(outputElements.get(1), is(expectedElements));
		assertThat(outputElements.get(2), is(expectedElements));
	}
}
