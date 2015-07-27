package teetime.stage.quicksort;

import teetime.util.divideAndConquer.Problem;

/**
 * @since 2.x
 *
 * @author Robin Mohr
 *
 */
public final class QuicksortProblem extends Problem {

	private final int key;
	private int low;
	private int high;
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
	public QuicksortProblem(final int key, final int low, final int high, final int[] numbers) {
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

	public void setHigh(final int high) {
		this.high = high;
	}

	public void setLow(final int low) {
		this.low = low;
	}

	public int[] getNumbers() {
		return this.numbers;
	}
}
