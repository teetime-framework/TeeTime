package teetime.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class RunnableStage implements Runnable {

	protected final Stage stage;
	@SuppressWarnings("PMD.LoggerIsNotStaticFinal")
	protected final Logger logger;

	public RunnableStage(final Stage stage) {
		this.stage = stage;
		this.logger = LoggerFactory.getLogger(stage.getClass());
	}

	@Override
	public final void run() {
		this.logger.debug("Executing runnable stage...");

		try {
			beforeStageExecution();

			do {
				executeStage();
			} while (!this.stage.shouldBeTerminated());

			afterStageExecution();

		} catch (Error e) {
			this.logger.error("Terminating thread due to the following exception: ", e);
			throw e;
		}

		this.logger.debug("Finished runnable stage. (" + this.stage.getId() + ")");
	}

	protected abstract void beforeStageExecution();

	protected abstract void executeStage();

	protected abstract void afterStageExecution();
}
