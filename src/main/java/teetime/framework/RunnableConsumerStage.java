package teetime.framework;

import java.util.Arrays;

import teetime.framework.exceptionHandling.StageExceptionListener;
import teetime.framework.idle.IdleStrategy;
import teetime.framework.idle.YieldStrategy;
import teetime.framework.pipe.IPipe;
import teetime.framework.signal.ISignal;

final class RunnableConsumerStage extends RunnableStage {

	private final IdleStrategy idleStrategy;

	/**
	 * Creates a new instance with the {@link YieldStrategy} as default idle strategy.
	 *
	 * @param stage
	 *            to execute within an own thread
	 */
	public RunnableConsumerStage(final Stage stage, final StageExceptionListener exceptionListener) {
		this(stage, new YieldStrategy(), exceptionListener);
	}

	public RunnableConsumerStage(final Stage stage, final IdleStrategy idleStrategy, final StageExceptionListener exceptionListener) {
		super(stage, exceptionListener);
		this.idleStrategy = idleStrategy;
	}

	@Override
	protected void beforeStageExecution() {
		logger.trace("ENTRY beforeStageExecution");

		do {
			checkforSignals();
			Thread.yield();
		} while (!stage.isStarted());

		logger.trace("EXIT beforeStageExecution");
	}

	@Override
	protected void executeStage() {
		try {
			this.stage.executeWithPorts();
		} catch (NotEnoughInputException e) {
			checkforSignals(); // check for termination
			executeIdleStrategy();
		}
	}

	private void executeIdleStrategy() {
		if (stage.shouldBeTerminated()) {
			return;
		}
		try {
			idleStrategy.execute();
		} catch (InterruptedException e) {
			// checkforSignals(); // check for termination
		}
	}

	@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
	private void checkforSignals() {
		// FIXME should getInputPorts() really be defined in Stage?
		InputPort<?>[] inputPorts = stage.getInputPorts();
		logger.debug("Checking signals for: " + Arrays.toString(inputPorts));
		for (InputPort<?> inputPort : inputPorts) {
			IPipe pipe = inputPort.getPipe();
			if (pipe instanceof AbstractInterThreadPipe) {
				AbstractInterThreadPipe intraThreadPipe = (AbstractInterThreadPipe) pipe;
				ISignal signal = intraThreadPipe.getSignal();
				if (null != signal) {
					stage.onSignal(signal, inputPort);
				}
			}
		}
	}

	@Override
	protected void afterStageExecution() {
		// do nothing
	}

}
