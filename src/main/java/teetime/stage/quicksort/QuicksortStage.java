package teetime.stage.quicksort;

import org.apache.commons.math3.util.Pair;

import teetime.framework.AbstractDCStage;

public final class QuicksortStage extends AbstractDCStage<QuicksortProblem, QuicksortSolution> {

	@Override
	protected boolean isBaseCase(final QuicksortProblem quickSortProblem) {
		return (quickSortProblem.getHigh() - quickSortProblem.getLow() >= 1 ? false : true);
	}

	@Override
	protected Pair<QuicksortProblem, QuicksortProblem> divide(final QuicksortProblem problem) {
		final int low = problem.getLow();
		final int high = problem.getHigh();
		final int[] numbers = problem.getNumbers();

		// pick the pivot
		final int middle = low + (high - low) / 2;
		final int pivot = numbers[middle];

		// make left < pivot and right > pivot
		int i = low;
		int j = high;
		while (i <= j) {
			while (numbers[i] < pivot) {
				i++;
			}

			while (numbers[j] > pivot) {
				j--;
			}

			if (i <= j) {
				int temp = numbers[i];
				numbers[i] = numbers[j];
				numbers[j] = temp;
				i++;
				j--;
			}
		}
		// recursively sort two sub parts
		return new Pair<QuicksortProblem, QuicksortProblem>(
				new QuicksortProblem(problem.getID(), low, j, numbers),
				new QuicksortProblem(problem.getID(), i, high, numbers));
	}

	@Override
	protected QuicksortSolution solve(final QuicksortProblem problem) {
		return new QuicksortSolution(
				problem.getID(),
				problem.getLow(),
				problem.getHigh(),
				problem.getNumbers());
	}

	@Override
	protected QuicksortSolution combine(final QuicksortSolution s1, final QuicksortSolution s2) {
		return s1;
	}

	@Override
	public AbstractDCStage<QuicksortProblem, QuicksortSolution> duplicate() {
		return new QuicksortStage();
	}
}
