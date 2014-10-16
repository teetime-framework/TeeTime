package teetime.framework.pipe;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;

public interface IPipeFactory {

	/**
	 * @deprecated Use {@link #create(OutputPort, InputPort)} or {@link #create(OutputPort, InputPort, int)} instead
	 *
	 * @param capacity
	 *            Number of elements the pipe can carry
	 * @return instance of the created pipe
	 */
	@Deprecated
	IPipe create(int capacity);

	/**
	 * Connects two stages with a pipe of default capacity.
	 *
	 * @param sourcePort
	 *            OutputPort of the stage, which produces data.
	 * @param targetPort
	 *            Input port of the receiving stage.
	 * @return The connecting pipe.
	 */
	<T> IPipe create(OutputPort<? extends T> sourcePort, InputPort<T> targetPort);

	/**
	 * Connects two stages with a pipe.
	 *
	 * @param sourcePort
	 *            OutputPort of the stage, which produces data.
	 * @param targetPort
	 *            Input port of the receiving stage.
	 * @param capacity
	 *            Number of elements the pipe can carry.
	 * @return The connecting pipe.
	 */
	<T> IPipe create(OutputPort<? extends T> sourcePort, InputPort<T> targetPort, int capacity);

	/**
	 * @return Type of ThreadCommunication, which is used by the created pipes.
	 */
	ThreadCommunication getThreadCommunication();

	/**
	 * @return Ordering type, which is used by the created pipes.
	 */
	PipeOrdering getOrdering();

	/**
	 * @return Wether or not the created pipes are growable
	 */
	boolean isGrowable();

}
