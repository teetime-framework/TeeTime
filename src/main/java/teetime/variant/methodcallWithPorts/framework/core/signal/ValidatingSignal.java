package teetime.variant.methodcallWithPorts.framework.core.signal;

import java.util.LinkedList;
import java.util.List;

import teetime.variant.methodcallWithPorts.framework.core.AbstractStage;
import teetime.variant.methodcallWithPorts.framework.core.validation.InvalidPortConnection;

public class ValidatingSignal implements Signal {

	private final List<InvalidPortConnection> invalidPortConnections = new LinkedList<InvalidPortConnection>();

	@Override
	public void trigger(final AbstractStage stage) {
		stage.onValidating(this.invalidPortConnections);
	}

	public List<InvalidPortConnection> getInvalidPortConnections() {
		return invalidPortConnections;
	}

}
