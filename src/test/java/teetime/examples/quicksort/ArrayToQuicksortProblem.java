package teetime.examples.quicksort;

import teetime.framework.AbstractProducerStage;
import teetime.stage.quicksort.QuicksortProblem;

public final class ArrayToQuicksortProblem extends AbstractProducerStage<QuicksortProblem> {

	private final int[] arr;
	private boolean firstTime = true;

	public ArrayToQuicksortProblem(final int[] arr) {
		this.arr = arr;
	}

	@Override
	protected void execute() {
		if (firstTime) {
			final QuicksortProblem qsp = new QuicksortProblem(1337, 0, arr.length - 1, this.arr);
			this.outputPort.send(qsp);
			this.firstTime = false;
		}
	}

}
