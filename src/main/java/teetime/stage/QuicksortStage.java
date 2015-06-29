package teetime.stage;

import java.util.Arrays;

import teetime.framework.AbstractDCStage;
import teetime.framework.ConfigurationContext;

public final class QuicksortStage extends AbstractDCStage<int[]> {

	private final int[] array;
	private final int pivotElement;

	public QuicksortStage(final ConfigurationContext context) {
		super(context);
		array = null;
		pivotElement = 0;
	}

	@Override
	protected boolean splitCondition(final int[] arr) {
		return (arr.length >= 2) ? true : false;
	}

	@Override
	protected void divide(final int[] element) {
		final int low = 0;
		final int high = element.length - 1;

		// pick the pivot
		int middle = low + (high - low) / 2;
		int pivot = element[middle];

		// make left < pivot and right > pivot
		int i = low, j = high;
		while (i <= j) {
			while (element[i] < pivot) {
				i++;
			}

			while (element[j] > pivot) {
				j--;
			}

			if (i <= j) {
				final int temp = element[i];
				element[i] = element[j];
				element[j] = temp;
				i++;
				j--;
			}
		}

		// recursively sort two sub parts
		if (low < j) {
			final int[] left = Arrays.copyOfRange(element, low, j);
			leftOutputPort.send(left);
		}
		if (high > i) {
			final int[] right = Arrays.copyOfRange(element, i, high);
			rightOutputPort.send(right);
		}
	}

	@Override
	protected void conquer(final int[] eLeft, final int[] eRight) {
		outputPort.send(array);
	}
}
