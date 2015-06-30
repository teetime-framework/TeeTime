package teetime.stage;

import java.util.Random;

import teetime.framework.AbstractProducerStage;

public final class RandomIntegerGenerator extends AbstractProducerStage<Integer> {

	private final int i;
	private final Random random = new Random();

	public RandomIntegerGenerator(final int n) {
		i = n;
	}

	@Override
	protected void execute() {
		this.getOutputPort().send(random.nextInt(i));
	}
}
