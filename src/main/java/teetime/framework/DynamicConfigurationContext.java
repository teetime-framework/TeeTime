package teetime.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.pipe.BoundedSynchedPipe;
import teetime.framework.pipe.UnboundedSynchedPipe;

final class DynamicConfigurationContext {

	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicConfigurationContext.class);

	public static final DynamicConfigurationContext INSTANCE = new DynamicConfigurationContext();

	private DynamicConfigurationContext() {
		// singleton
	}

	/**
	 * Connects two ports with a pipe with a default capacity of currently {@value #DEFAULT_CAPACITY}.
	 *
	 * @param sourcePort
	 *            {@link OutputPort} of the sending stage
	 * @param targetPort
	 *            {@link InputPort} of the sending stage
	 * @param <T>
	 *            the type of elements to be sent
	 */
	public <T> void connectPorts(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		if ((sourcePort.getPipe() != null || targetPort.getPipe() != null) && LOGGER.isWarnEnabled()) {
			LOGGER.warn("Overwriting existing pipe while connecting stages " +
					sourcePort.getOwningStage().getId() + " and " + targetPort.getOwningStage().getId() + ".");
		}
		// TODO: Unsynched?
		new UnboundedSynchedPipe<T>(sourcePort, targetPort);
	}

	/**
	 * Connects to ports with a pipe of a certain capacity
	 *
	 * @param sourcePort
	 *            {@link OutputPort} of the sending stage
	 * @param targetPort
	 *            {@link InputPort} of the sending stage
	 * @param capacity
	 *            the pipe is set to this capacity, if the value is greater than 0. If it is 0, than the pipe is unbounded, thus growing of the pipe is enabled.
	 * @param <T>
	 *            the type of elements to be sent
	 */
	public <T> void connectPorts(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		if ((sourcePort.getPipe() != null || targetPort.getPipe() != null) && LOGGER.isWarnEnabled()) {
			LOGGER.warn("Overwriting existing pipe while connecting stages " +
					sourcePort.getOwningStage().getId() + " and " + targetPort.getOwningStage().getId() + ".");
		}
		new BoundedSynchedPipe<T>(sourcePort, targetPort, capacity);
	}
}
