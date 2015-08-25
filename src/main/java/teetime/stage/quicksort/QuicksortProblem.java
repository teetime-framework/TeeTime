package teetime.stage.quicksort;

import java.util.Arrays;

import teetime.framework.AbstractDivideAndConquerProblem;
import teetime.util.divideAndConquer.DividedDCProblem;

/**
 * @since 2.x
 *
 * @author Robin Mohr
 *
 */
public final class QuicksortProblem extends AbstractDivideAndConquerProblem<QuicksortProblem, QuicksortSolution> {

	private final int low;
	private final int high;
	private final int[] numbers;

	/**
	 * An implementation of a quicksort problem.
	 *
	 * @param low
	 *            Pointer to the lower bound of indices to be compared in the array
	 * @param high
	 *            Pointer to the upper bound of indices to be compared in the array
	 * @param numbers
	 *            Array to be sorted
	 */
	public QuicksortProblem(final int id, final int low, final int high, final int[] numbers) {
		super(id);
		this.low = low;
		this.high = high;
		this.numbers = numbers;
	}

	public int getLow() {
		return this.low;
	}

	public int getHigh() {
		return this.high;
	}

	public int[] getNumbers() {
		return this.numbers;
	}

	@Override
	public String toString() {
		return "Problem ID: " + this.getID() + " contains Array: " + Arrays.toString(numbers);
	}

	@Override
	public boolean isBaseCase() {
		return high - low < 1;
	}

	@Override
	public DividedDCProblem<QuicksortProblem> divide() {

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
		return new DividedDCProblem<QuicksortProblem>(
				new QuicksortProblem(this.getID(), low, j, numbers),
				new QuicksortProblem(this.getID(), i, high, numbers));
	}

	@Override
	public QuicksortSolution solve() {
		return new QuicksortSolution(
				this.getID(),
				this.low,
				this.high,
				this.numbers);
	}
}
