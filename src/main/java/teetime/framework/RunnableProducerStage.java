package teetime.framework;

import teetime.framework.signal.StartingSignal;
import teetime.framework.signal.TerminatingSignal;

public final class RunnableProducerStage extends RunnableStage {

	public RunnableProducerStage(final Stage stage) {
		super(stage);
	}

	@Override
	protected void beforeStageExecution() {
		final StartingSignal startingSignal = new StartingSignal();
		this.stage.onSignal(startingSignal, null);
	}

	@Override
	protected void executeStage() {
		this.stage.executeWithPorts();
	}

	@Override
	protected void afterStageExecution() {
		final TerminatingSignal terminatingSignal = new TerminatingSignal();
		this.stage.onSignal(terminatingSignal, null);
	}

}
