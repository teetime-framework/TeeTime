package teetime.stage.util;

/**
 * @since 2.x
 *
 * @author Robin Mohr
 *
 */
public final class QuicksortProblem {

	private final int low;
	private final int high;
	private final int[] arr;

	/**
	 * An implementation of a quicksort problem.
	 *
	 * @param low
	 *            Pointer to the lower bound of indices to be compared in the array
	 * @param high
	 *            Pointer to the upper bound of indices to be compared in the array
	 * @param arr
	 *            Array to be sorted
	 */
	public QuicksortProblem(final int low, final int high, final int[] arr) {
		this.low = low;
		this.high = high;
		this.arr = arr;
	}

	public int getLow() {
		return this.low;
	}

	public int getHigh() {
		return this.high;
	}

	public int[] getArr() {
		return this.arr;
	}
}
