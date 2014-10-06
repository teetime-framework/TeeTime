package teetime.framework.signal;

import java.util.LinkedList;
import java.util.List;

import teetime.framework.AbstractStage;
import teetime.framework.validation.InvalidPortConnection;

public class ValidatingSignal implements ISignal {

	private final List<InvalidPortConnection> invalidPortConnections = new LinkedList<InvalidPortConnection>();

	@Override
	public void trigger(final AbstractStage stage) {
		stage.onValidating(this.invalidPortConnections);
	}

	public List<InvalidPortConnection> getInvalidPortConnections() {
		return invalidPortConnections;
	}

}
