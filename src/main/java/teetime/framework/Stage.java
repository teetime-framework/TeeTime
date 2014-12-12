package teetime.framework;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.signal.ISignal;
import teetime.framework.validation.InvalidPortConnection;

public abstract class Stage { // NOPMD (should not start with "Abstract")

	private static final ConcurrentMap<String, Integer> INSTANCES_COUNTER = new ConcurrentHashMap<String, Integer>();

	private final String id;
	/**
	 * A unique logger instance per stage instance
	 */
	protected final Logger logger; // NOPMD

	protected Stage() {
		this.id = this.createId();
		this.logger = LoggerFactory.getLogger(this.id);
	}

	/**
	 * Retrieves the identifier associated with the stage
	 *
	 * @return An id as String
	 */
	public String getId() {
		return this.id;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + ": " + this.getId();
	}

	private String createId() {
		String simpleName = this.getClass().getSimpleName();

		Integer numInstances = INSTANCES_COUNTER.get(simpleName);
		if (null == numInstances) {
			numInstances = 0;
		}

		String newId = simpleName + "-" + numInstances;
		INSTANCES_COUNTER.put(simpleName, ++numInstances);
		return newId;
	}

	static void clearInstanceCounters() { // NOPMD (package-private to clear map in tests)
		INSTANCES_COUNTER.clear();
	}

	// public abstract Stage getParentStage();
	//
	// public abstract void setParentStage(Stage parentStage, int index);

	/**
	 * This should check, if the OutputPorts are connected correctly. This is needed to avoid NullPointerExceptions and other errors.
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
