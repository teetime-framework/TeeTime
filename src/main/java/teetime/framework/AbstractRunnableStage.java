package teetime.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.exceptionHandling.StageException;
import teetime.framework.exceptionHandling.StageExceptionHandler;
import teetime.framework.exceptionHandling.StageExceptionHandler.FurtherExecution;
import teetime.framework.signal.TerminatingSignal;

abstract class AbstractRunnableStage implements Runnable {

	private final StageExceptionHandler exceptionHandler;

	private final Stage stage;
	@SuppressWarnings("PMD.LoggerIsNotStaticFinal")
	protected final Logger logger;

	public AbstractRunnableStage(final Stage stage, final StageExceptionHandler exceptionHandler) {
		this.stage = stage;
		this.logger = LoggerFactory.getLogger(stage.getClass());
		this.exceptionHandler = exceptionHandler;
	}

	@Override
	public final void run() {
		this.logger.debug("Executing runnable stage...");
		boolean failed = false;
		try {
			beforeStageExecution(stage);

			do {
				try {
					executeStage(stage);
				} catch (StageException e) {
					final FurtherExecution furtherExecution = this.exceptionHandler.onStageException(e, e.getThrowingStage());
					if (furtherExecution == FurtherExecution.TERMINATE) {
						this.stage.terminate();
						failed = true;
					}
				}
			} while (!stage.shouldBeTerminated());

			afterStageExecution(stage);

		} catch (Error e) {
			this.logger.error("Terminating thread due to the following exception: ", e);
			throw e;
		} catch (RuntimeException e) {
			this.logger.error("Terminating thread due to the following exception: ", e);
			throw e;
		} catch (InterruptedException e) {
			this.logger.error("Terminating thread due to the following exception: ", e);
		}

		this.logger.debug("Finished runnable stage. (" + this.stage.getId() + ")");
		if (failed) {
			if (stage.getTerminationStrategy() == TerminationStrategy.BY_SIGNAL) {
				TerminatingSignal signal = new TerminatingSignal();
				// TODO: Check if this is really needed... it seems like signals are passed on after their first arrival
				InputPort<?>[] inputPorts = stage.getInputPorts();
				for (int i = 0; i < inputPorts.length; i++) {
					stage.onSignal(signal, inputPorts[i]);
				}
			}
			throw new IllegalStateException("Terminated by StageExceptionListener");
		}

	}

	protected abstract void beforeStageExecution(Stage stage) throws InterruptedException;

	protected abstract void executeStage(Stage stage);

	protected abstract void afterStageExecution(Stage stage);
}
