package teetime.examples.quicksort;

import java.util.ArrayList;
import java.util.List;

import teetime.framework.AbstractConsumerStage;
import teetime.stage.util.QuicksortProblem;

public final class QuicksortProblemToList extends AbstractConsumerStage<QuicksortProblem> {

	@Override
	protected void execute(final QuicksortProblem qsp) {
		final List<Integer> list = new ArrayList<Integer>();
		final int[] arr = qsp.getArr();
		for (int j = 0; j < arr.length; j++) {
			list.add(arr[j]);
		}
		System.out.println(list.toString());
	}
}
