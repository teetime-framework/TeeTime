package teetime.variant.methodcallWithPorts.framework.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.variant.methodcallWithPorts.framework.core.signal.StartingSignal;
import teetime.variant.methodcallWithPorts.framework.core.signal.TerminatingSignal;
import teetime.variant.methodcallWithPorts.framework.core.signal.ValidatingSignal;

public class RunnableStage implements Runnable {

	private final StageWithPort stage;
	private final Logger logger;
	private boolean validationEnabled;

	public RunnableStage(final StageWithPort stage) {
		this.stage = stage;
		this.logger = LoggerFactory.getLogger(stage.getClass());
	}

	@Override
	public void run() {
		this.logger.debug("Executing runnable stage...");

		if (this.validationEnabled) {
			ValidatingSignal validatingSignal = new ValidatingSignal();
			this.stage.onSignal(validatingSignal, null);
			if (validatingSignal.getInvalidPortConnections().size() > 0) {
				// throw new RuntimeException(message);
				// TODO implement what to do on validation messages
			}
		}

		try {
			StartingSignal startingSignal = new StartingSignal();
			this.stage.onSignal(startingSignal, null);

			do {
				this.stage.executeWithPorts();
			} while (this.stage.isReschedulable());

			TerminatingSignal terminatingSignal = new TerminatingSignal();
			this.stage.onSignal(terminatingSignal, null);

		} catch (RuntimeException e) {
			this.logger.error("Terminating thread due to the following exception: ", e);
			throw e;
		}

		this.logger.debug("Finished runnable stage. (" + this.stage.getId() + ")");
	}

	public boolean isValidationEnabled() {
		return this.validationEnabled;
	}

	public void setValidationEnabled(final boolean validationEnabled) {
		this.validationEnabled = validationEnabled;
	}
}
