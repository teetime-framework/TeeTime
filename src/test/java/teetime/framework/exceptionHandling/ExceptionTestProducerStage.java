package teetime.framework.exceptionHandling;

import teetime.framework.AbstractProducerStage;
import teetime.framework.InputPort;
import teetime.framework.TerminationStrategy;

public class ExceptionTestProducerStage extends AbstractProducerStage<Object> {

	private static int instances = 0;
	private TerminationStrategy strategy;
	public int numberOfExecutions = 0;
	private final InputPort<Object> input = createInputPort();

	ExceptionTestProducerStage() {
		switch (instances) {
		case 0: {
			strategy = TerminationStrategy.BY_SELF_DECISION;
			break;
		}
		case 1: {
			strategy = TerminationStrategy.BY_INTERRUPT;
			break;
		}
		default: {
			strategy = TerminationStrategy.BY_SELF_DECISION;
		}
		}

		instances++;
	}

	@Override
	protected void execute() {
		getOutputPort().send(new Object());
		if (numberOfExecutions++ >= 10000 && strategy == TerminationStrategy.BY_SELF_DECISION) {
			this.terminate();
		}
	}

	@Override
	public TerminationStrategy getTerminationStrategy() {
		return strategy;
	}

	@Override
	public String getId() {
		if (strategy == TerminationStrategy.BY_INTERRUPT) {
			return "Infinite" + super.getId();
		}
		return "Finite" + super.getId();
	}
}
