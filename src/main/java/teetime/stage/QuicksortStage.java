package teetime.stage;

import teetime.framework.AbstractDCStage;
import teetime.framework.ConfigurationContext;

public final class QuicksortStage extends AbstractDCStage<int[]> {

	private final int[] inputArray;

	public QuicksortStage(final ConfigurationContext context) {
		super(context);
		inputArray = null;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean splitCondition(final int[] arr) {
		return (arr.length >= 2) ? true : false;
	}

	@Override
	protected void divide(final int[] element) {

	}

	@Override
	protected void conquer(final int[] eLeft, final int[] eRight) {
		// TODO Auto-generated method stub

	}

	// // FIXME Should be a method of AbstractDCStage
	// @SuppressWarnings("null")
	// @Override
	// protected QuicksortStage[] createCopies() {
	// QuicksortStage q1 = new QuicksortStage();
	// QuicksortStage q2 = new QuicksortStage();
	// QuicksortStage[] qq = null;
	// qq[0] = q1;
	// qq[1] = q2;
	// return qq;
	// }

	// @Override
	// protected int[][] divide(final int[] arr) {
	// int q, l, r;
	// l = 0;
	// r = arr.length;
	// int[][] arrs = null;
	// if (l < r) {
	// q = partition(l, r);
	//
	// int[] left = Arrays.copyOfRange(arr, 0, q);
	// int[] right = Arrays.copyOfRange(arr, q + 1, arr.length);
	// arrs[0] = left;
	// arrs[1] = right;
	//
	// }
	// return arrs;
	// }
	//
	// @Override
	// protected int[] conquer(final int[] eLeft, final int[] eRight) {
	// final int[] arr = null;
	// return arr;
	// }
	//
	// private int partition(final int l, final int r) {
	// int i, j, x = inputArray[(l + r) / 2];
	// i = l - 1;
	// j = r + 1;
	//
	// do {
	// i++;
	// } while (inputArray[i] < x);
	//
	// do {
	// j--;
	// } while (inputArray[j] > x);
	//
	// if (i < j) {
	// int k = inputArray[i];
	// inputArray[i] = inputArray[j];
	// inputArray[j] = k;
	// } else {
	// return j;
	// }
	// return -1;
	// }
}
