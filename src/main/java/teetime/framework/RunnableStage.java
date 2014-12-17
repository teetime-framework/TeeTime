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
				try {
					this.stage.executeWithPorts();
				} catch (NotEnoughInputException e) {
					// 1. check for terminating signal
					// new Thread().getState() == State.WAITING

					// 2. check for no input reaction: this.getStrategy()
					// 2.1 if BUSY_WAITING with timeout to then sleep(to)
					// 2.2 if BLOCKING_WAIT then

				}
			} while (!this.stage.shouldBeTerminated());

			afterStageExecution();

		} catch (Error e) {
			this.logger.error("Terminating thread due to the following exception: ", e);
			throw e;
		}

		this.logger.debug("Finished runnable stage. (" + this.stage.getId() + ")");
	}

	protected abstract void beforeStageExecution();

	protected abstract void afterStageExecution();
}
