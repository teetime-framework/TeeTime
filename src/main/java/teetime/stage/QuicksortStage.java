package teetime.stage;

import teetime.framework.AbstractDCStage;
import teetime.framework.DynamicConfigurationContext;
import teetime.stage.taskfarm.ITaskFarmDuplicable;
import teetime.stage.util.QuicksortProblem;

public final class QuicksortStage extends AbstractDCStage<QuicksortProblem, QuicksortProblem> {

	public QuicksortStage(final DynamicConfigurationContext context) {
		super(context);
	}

	@Override
	protected boolean isBaseCase(final QuicksortProblem quickSortProblem) {
		return (quickSortProblem.getHigh() - quickSortProblem.getLow() >= 1 ? false : true);
	}

	@Override
	protected void divide(final QuicksortProblem problem) {
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
		// FIXME Put following code in AbstraceDCStage
		QuicksortProblem newQuicksortProblem1 = new QuicksortProblem(low, j, numbers);
		leftOutputPort.send(newQuicksortProblem1);
		QuicksortProblem newQuicksortProblem2 = new QuicksortProblem(i, high, numbers);
		rightOutputPort.send(newQuicksortProblem2);

	}

	@Override
	protected QuicksortProblem solve(final QuicksortProblem problem) {
		return problem;
	}

	@Override
	protected void combine(final QuicksortProblem s1, final QuicksortProblem s2) {
		s1.setHigh(s2.getHigh());
		this.getOutputPort().send(s1);
	}

	@Override
	public ITaskFarmDuplicable<QuicksortProblem, QuicksortProblem> duplicate() {
		return new QuicksortStage(this.getContext());
	}
}
