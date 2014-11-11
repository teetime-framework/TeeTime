package teetime.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.signal.StartingSignal;
import teetime.framework.signal.TerminatingSignal;
import teetime.framework.signal.ValidatingSignal;
import teetime.framework.validation.AnalysisNotValidException;

@SuppressWarnings("PMD.BeanMembersShouldSerialize")
public class RunnableStage implements Runnable {

	private final HeadStage stage;
	private final Logger logger; // NOPMD
	private boolean validationEnabled;

	public RunnableStage(final HeadStage stage) {
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
				throw new AnalysisNotValidException(validatingSignal.getInvalidPortConnections());
			}
		}

		try {
			StartingSignal startingSignal = new StartingSignal();
			this.stage.onSignal(startingSignal, null);

			do {
				this.stage.executeWithPorts();
			} while (!this.stage.shouldBeTerminated());

			TerminatingSignal terminatingSignal = new TerminatingSignal();
			this.stage.onSignal(terminatingSignal, null);

		} catch (Error e) {
			this.logger.error("Terminating thread due to the following exception: ", e);
			throw e;
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
