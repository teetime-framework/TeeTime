package teetime.variant.methodcallWithPorts.framework.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunnableStage implements Runnable {

	private final StageWithPort stage;
	private final Logger logger;

	public RunnableStage(final StageWithPort stage) {
		this.stage = stage;
		this.logger = LoggerFactory.getLogger(stage.getClass());
	}

	@Override
	public void run() {
		this.logger.debug("Executing runnable stage...");

		try {
			this.stage.onStart();

			do {
				this.stage.executeWithPorts();
			} while (this.stage.isReschedulable());

			this.stage.onSignal(Signal.FINISHED, null);

		} catch (RuntimeException e) {
			this.logger.error("Terminating thread due to the following exception: ", e);
			throw e;
		}

		this.logger.debug("Finished runnable stage. (" + this.stage.getId() + ")");
	}
}
