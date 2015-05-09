package teetime.framework.signal;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractSignal implements ISignal {

	protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractSignal.class);

	protected final List<Exception> catchedExceptions = new LinkedList<Exception>();

	protected AbstractSignal() {
		super();
	}

	public List<Exception> getCatchedExceptions() {
		return this.catchedExceptions;
	}

}
