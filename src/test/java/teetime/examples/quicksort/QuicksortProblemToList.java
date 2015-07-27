package teetime.examples.quicksort;

import java.util.ArrayList;
import java.util.List;

import teetime.framework.AbstractConsumerStage;
import teetime.stage.quicksort.QuicksortSolution;

public final class QuicksortProblemToList extends AbstractConsumerStage<QuicksortSolution> {

	@Override
	protected void execute(final QuicksortSolution qsp) {
		final List<Integer> list = new ArrayList<Integer>();
		final int[] arr = qsp.getNumbers();
		for (int j = 0; j < arr.length; j++) {
			list.add(arr[j]);
		}
		System.out.println(list.toString());
	}
}
