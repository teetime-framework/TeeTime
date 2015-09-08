package teetime.stage.quicksort;

import java.util.Arrays;

import teetime.framework.divideandconquer.AbstractDivideAndConquerProblem;
import teetime.framework.divideandconquer.DividedDCProblem;

/**
 * A problem to be solved with the quicksort algorithm.
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
	public QuicksortProblem(final int low, final int high, final int... numbers) {
		super();
		this.low = low;
		this.high = high;
		this.numbers = numbers;
	}

	public QuicksortProblem(final int identifier, final int low, final int high, final int... numbers) {
		super(identifier);
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
		final int middle = low + (high - low) / 2; // pick the pivot
		final int pivot = numbers[middle];

		// make left < pivot and right > pivot
		int lowPointer = low;
		int highPointer = high;
		while (lowPointer <= highPointer) {
			while (numbers[lowPointer] < pivot) {
				lowPointer++;
			}

			while (numbers[highPointer] > pivot) {
				highPointer--;
			}

			if (lowPointer <= highPointer) {
				int temp = numbers[lowPointer];
				numbers[lowPointer] = numbers[highPointer];
				numbers[highPointer] = temp;
				lowPointer++;
				highPointer--;
			}
		}
		// recursively sort two sub parts
		return new DividedDCProblem<QuicksortProblem>(
				new QuicksortProblem(this.getID(), low, highPointer, numbers),
				new QuicksortProblem(this.getID(), lowPointer, high, numbers));
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
