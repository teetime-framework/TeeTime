package teetime.stage;

import teetime.framework.AbstractDCStage;
import teetime.framework.DynamicConfigurationContext;
import teetime.stage.util.QuicksortProblem;

public final class QuicksortStage extends AbstractDCStage<QuicksortProblem> {

	public QuicksortStage(final DynamicConfigurationContext context) {
		super(context);
	}

	// TODO Get rid of this
	@Override
	protected AbstractDCStage<QuicksortProblem> debugCreateMethod() {
		return new QuicksortStage(super.getContext());
	}

	@Override
	protected void divide(final QuicksortProblem qsp) {
		final int low = qsp.getLow();
		final int high = qsp.getHigh();
		final int[] arr = qsp.getArr();

		// pick the pivot
		final int middle = low + (high - low) / 2;
		final int pivot = arr[middle];

		// make left < pivot and right > pivot
		int i = low, j = high;
		while (i <= j) {
			while (arr[i] < pivot) {
				i++;
			}

			while (arr[j] > pivot) {
				j--;
			}

			if (i <= j) {
				int temp = arr[i];
				arr[i] = arr[j];
				arr[j] = temp;
				i++;
				j--;
			}
		}

		// recursively sort two sub parts
		QuicksortProblem newQuicksortProblem1 = new QuicksortProblem(low, j, arr);
		leftOutputPort.send(newQuicksortProblem1);
		QuicksortProblem newQuicksortProblem2 = new QuicksortProblem(i, high, arr);
		rightOutputPort.send(newQuicksortProblem2);

	}

	@Override
	protected void solve(final QuicksortProblem qsp) {
		this.outputPort.send(qsp);
	}

	@Override
	protected void conquer(final QuicksortProblem eLeft, final QuicksortProblem eRight) {
		final int rlow = eRight.getLow();
		final int rhigh = eRight.getHigh();
		final int[] arr1 = eLeft.getArr();
		final int[] arr2 = eRight.getArr();
		for (int j = rlow; j <= rhigh; j++) {
			arr1[j] = arr2[j];
		}
		QuicksortProblem newQuicksortProblem = new QuicksortProblem(eLeft.getLow(), rhigh, arr1);
		this.outputPort.send(newQuicksortProblem);

	}

	@Override
	protected boolean splitCondition(final QuicksortProblem qsp) {
		return (((qsp.getHigh() - qsp.getLow()) >= 1) ? true : false);
	}
}
