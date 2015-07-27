package teetime.stage.quicksort;

import teetime.util.divideAndConquer.Solution;

/**
 * @since 2.x
 *
 * @author Robin Mohr
 *
 */
public final class QuicksortSolution extends Solution {

	private final int low;
	private final int high;
	private final int[] numbers;
	private final int key;

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

	public QuicksortSolution(final int key, final int low, final int high, final int[] numbers) {
		super(key);
		this.key = key;
		this.low = low;
		this.high = high;
		this.numbers = numbers;
	}

	@Override
	public int getKey() {
		return this.key;
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
}
