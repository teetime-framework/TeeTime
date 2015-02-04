package teetime.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.exceptionHandling.StageException;
import teetime.framework.exceptionHandling.StageExceptionHandler;
import teetime.framework.exceptionHandling.StageExceptionHandler.FurtherExecution;

abstract class AbstractRunnableStage implements Runnable {

	private final StageExceptionHandler exceptionHandler;

	protected final Stage stage;
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
			beforeStageExecution();

			do {
				try {
					executeStage();
				} catch (StageException e) {
					final FurtherExecution furtherExecution = this.exceptionHandler.onStageException(e, e.getThrowingStage());
					if (furtherExecution == FurtherExecution.TERMINATE) {
						this.stage.terminate();
						failed = true;
					}
				}
			} while (!this.stage.shouldBeTerminated());

			afterStageExecution();

		} catch (Error e) {
			this.logger.error("Terminating thread due to the following exception: ", e);
			throw e;
		} catch (RuntimeException e) {
			this.logger.error("Terminating thread due to the following exception: ", e);
			throw e;
		}

		this.logger.debug("Finished runnable stage. (" + this.stage.getId() + ")");
		if (failed) {
			throw new IllegalStateException("Terminated by StageExceptionListener");
		}
	}

	protected abstract void beforeStageExecution();

	protected abstract void executeStage();

	protected abstract void afterStageExecution();
}
