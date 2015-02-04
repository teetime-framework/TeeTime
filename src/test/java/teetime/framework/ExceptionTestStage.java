package teetime.framework;

public class ExceptionTestStage extends AbstractProducerStage<Object> {

	private static int instances = 0;
	private TerminationStrategy strategy;
	public int loops = 0;

	ExceptionTestStage() {
		switch (instances) {
		case 0: {
			strategy = TerminationStrategy.BY_SELF_DECISION;
			break;
		}
		case 1: {
			strategy = TerminationStrategy.BY_SIGNAL;
			break;
		}
		case 2: {
			strategy = TerminationStrategy.BY_INTERRUPT;
			break;
		}
		}
		instances++;
	}

	@Override
	protected void execute() {
		if (strategy == TerminationStrategy.BY_SELF_DECISION) {
			if (loops % 1000 == 0) {
				throw new IllegalStateException("1000 loops");
			}
			loops++;
		}
	}

	@Override
	public TerminationStrategy getTerminationStrategy() {
		return strategy;
	}
}
