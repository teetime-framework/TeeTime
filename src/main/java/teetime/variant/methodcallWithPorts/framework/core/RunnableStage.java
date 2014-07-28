package teetime.variant.methodcallWithPorts.framework.core;

import kieker.common.logging.Log;
import kieker.common.logging.LogFactory;

public class RunnableStage<I> implements Runnable {

	private final ConsumerStage<I> stage;
	private final Log logger;

	public RunnableStage(final ConsumerStage<I> stage) {
		this.stage = stage;
		this.logger = LogFactory.getLog(stage.getClass());
	}

	@Override
	public void run() {
		this.logger.debug("Executing runnable stage...");

		try {
			this.stage.onStart();

			do {
				this.stage.executeWithPorts();
			} while (this.stage.isReschedulable());

			this.stage.onSignal(Signal.FINISHED, this.stage.getInputPort());

		} catch (RuntimeException e) {
			this.logger.error("Terminating thread due to the following exception: ", e);
			throw e;
		}

		this.logger.debug("Finished runnable stage. (" + this.stage.getId() + ")");
	}
}
