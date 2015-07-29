package teetime.examples.quicksort;

import teetime.framework.AbstractProducerStage;
import teetime.stage.quicksort.QuicksortProblem;

public final class ArrayToQuicksortProblem extends AbstractProducerStage<QuicksortProblem> {

	private final int[] numbers;
	private boolean firstTime = true;

	public ArrayToQuicksortProblem(final int[] numbers) {
		this.numbers = numbers;
	}

	@Override
	protected void execute() {
		if (firstTime) {
			final QuicksortProblem qsp = new QuicksortProblem(1337, 0, numbers.length - 1, this.numbers);
			System.out.println("ID: 1337, low: 0, high: " + (numbers.length - 1) + ", Array: ");
			this.outputPort.send(qsp);
			this.firstTime = false;
		}
	}

}
