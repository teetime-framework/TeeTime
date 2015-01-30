package teetime.framework;

import teetime.framework.exceptionHandling.StageExceptionListener;
import teetime.framework.signal.StartingSignal;
import teetime.framework.signal.TerminatingSignal;

public final class RunnableProducerStage extends RunnableStage {

	public RunnableProducerStage(final Stage stage, final StageExceptionListener listener) {
		super(stage, listener);
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
