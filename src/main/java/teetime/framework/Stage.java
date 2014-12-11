package teetime.framework;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.signal.ISignal;
import teetime.framework.validation.InvalidPortConnection;

public abstract class Stage {

	private final String id;
	private static HashMap<String, Integer> instancesCounter = new HashMap<String, Integer>();
	/**
	 * A unique logger instance per stage instance
	 */
	protected final Logger logger; // NOPMD

	protected Stage() {
		this.id = this.nameInstance();
		// this.id = UUID.randomUUID().toString(); // the id should only be represented by a UUID, not additionally by the class name
		this.logger = LoggerFactory.getLogger(this.getClass().getName() + "(" + this.id + ")");
	}

	public String getId() {
		return this.id;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + ": " + this.getId();
	}

	private String nameInstance() {
		int instances = 0;
		String id;
		String simpleName = this.getClass().getSimpleName();

		if (instancesCounter.containsKey(simpleName)) {
			instances = instancesCounter.get(simpleName);
		}

		id = simpleName + "-" + instances;
		instancesCounter.put(simpleName, ++instances);
		return id;
	}

	// public abstract Stage getParentStage();
	//
	// public abstract void setParentStage(Stage parentStage, int index);

	/**
	 *
	 * @param invalidPortConnections
	 *            <i>(Passed as parameter for performance reasons)</i>
	 */
	public abstract void validateOutputPorts(List<InvalidPortConnection> invalidPortConnections);

	protected abstract void executeWithPorts();

	protected abstract void onSignal(ISignal signal, InputPort<?> inputPort);

	protected abstract TerminationStrategy getTerminationStrategy();

	protected abstract void terminate();

	protected abstract boolean shouldBeTerminated();
}
