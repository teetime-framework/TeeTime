package teetime.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.exceptionHandling.StageException;
import teetime.framework.exceptionHandling.StageExceptionListener;
import teetime.framework.exceptionHandling.StageExceptionListener.FurtherExecution;

abstract class RunnableStage implements Runnable {

	protected final Stage stage;
	@SuppressWarnings("PMD.LoggerIsNotStaticFinal")
	protected final Logger logger;
	private final StageExceptionListener listener;

	public RunnableStage(final Stage stage, final StageExceptionListener exceptionListener) {
		this.stage = stage;
		this.logger = LoggerFactory.getLogger(stage.getClass());
		this.listener = exceptionListener;
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
					if (this.listener.onStageException(e, e.getThrowingStage()) == FurtherExecution.TERMINATE) {
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
