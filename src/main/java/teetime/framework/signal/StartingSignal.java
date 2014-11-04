package teetime.framework.signal;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.AbstractStage;

public class StartingSignal implements ISignal {

	private static final Logger LOGGER = LoggerFactory.getLogger(StartingSignal.class);
	private final List<Exception> catchedExceptions = new LinkedList<Exception>();

	@Override
	public void trigger(final AbstractStage stage) {
		try {
			stage.onStarting();
			LOGGER.info(stage + " started.");
		} catch (Exception e) {
			catchedExceptions.add(e);
			LOGGER.error("Exception while sending the start signal", e);
		}
	}

	public List<Exception> getCatchedExceptions() {
		return catchedExceptions;
	}

}
