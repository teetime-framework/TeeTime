package teetime.stage.quicksort;

import java.util.Arrays;

import teetime.util.divideAndConquer.Identifiable;

/**
 * @since 2.x
 *
 * @author Robin Mohr
 *
 */
public final class QuicksortSolution extends Identifiable {

	private final int low;
	private final int high;
	private final int[] numbers;

	/**
	 * An implementation of a quicksort solution.
	 *
	 * @param low
	 *            Pointer to the lower bound of indices to be compared in the array
	 * @param high
	 *            Pointer to the upper bound of indices to be compared in the array
	 * @param numbers
	 *            Array to be sorted
	 */

	public QuicksortSolution(final int id, final int low, final int high, final int[] numbers) {
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
		String s = "Solution ID: " + this.getID() + " contains Array: " + Arrays.toString(numbers);
		return s;

	}
}
