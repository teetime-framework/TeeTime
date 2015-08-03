package teetime.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.pipe.SpScPipeFactory;
import teetime.framework.signal.InitializingSignal;
import teetime.framework.signal.StartingSignal;

public class DynamicConfigurationContext {

	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicConfigurationContext.class);
	private static final SpScPipeFactory PIPE_FACTORY = new SpScPipeFactory();

	public void beginThread(final Stage previousStage, final Stage stage) {
		RuntimeServiceFacade.INSTANCE.startWithinNewThread(previousStage, stage);
	}

	/**
	 * Sends the {@linkInitializingSignal} and the {@link StartingSignal}.
	 *
	 * @param outputPort
	 */
	public void sendSignals(final OutputPort<?> outputPort) {
		outputPort.sendSignal(new InitializingSignal());
		outputPort.sendSignal(new StartingSignal());
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
		if (sourcePort.getPipe() != null || targetPort.getPipe() != null) {
			LOGGER.warn("Overwriting existing pipe while connecting stages " +
					sourcePort.getOwningStage().getId() + " and " + targetPort.getOwningStage().getId() + ".");
		}
		PIPE_FACTORY.create(sourcePort, targetPort);
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
		if (sourcePort.getPipe() != null || targetPort.getPipe() != null) {
			LOGGER.warn("Overwriting existing pipe while connecting stages " +
					sourcePort.getOwningStage().getId() + " and " + targetPort.getOwningStage().getId() + ".");
		}
		PIPE_FACTORY.create(sourcePort, targetPort, capacity);
	}
}
