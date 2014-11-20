package teetime.framework.signal;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.AbstractStage;

public class TerminatingSignal implements ISignal {

	private static final Logger LOGGER = LoggerFactory.getLogger(TerminatingSignal.class);
	private final List<Exception> catchedExceptions = new LinkedList<Exception>();

	public TerminatingSignal() {}

	@Override
	public void trigger(final AbstractStage stage) {
		try {
			stage.onTerminating();
		} catch (Exception e) { // NOCS (Stages can throw any arbitrary Exception)
			this.catchedExceptions.add(e);
			LOGGER.error("Exception while sending the termination signal", e);
		}
	}

	public List<Exception> getCatchedExceptions() {
		return this.catchedExceptions;
	}

}
