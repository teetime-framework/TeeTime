package teetime.framework.pipe;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.signal.ISignal;

/**
 * Represents an interface, which should be adapted by all implementations of pipes.
 */
public interface IPipe {

	/**
	 * Adds an element to the Pipe.
	 *
	 * @param element
	 *            Element which will be added
	 * @return True if the element could be added, false otherwise
	 */
	boolean add(Object element);

	/**
	 * Checks whether the pipe is empty or not.
	 *
	 * @return True if the pipe is empty, false otherwise.
	 */
	boolean isEmpty();

	/**
	 * Retrieves the number of elements, the pipe is capable to carry at the same time.
	 *
	 * @return Number of elements
	 */
	int size();

	/**
	 * Retrieves the last element of the pipe and deletes it.
	 *
	 * @return The last element in the pipe.
	 */
	Object removeLast();

	/**
	 * Reads the pipe's last element, but does not delete it.
	 *
	 * @return The last element in the pipe.
	 */
	Object readLast();

	/**
	 * Retrieves the receiving port.
	 *
	 * @return InputPort which is connected to the pipe.
	 */
	InputPort<?> getTargetPort();

	/**
	 * A stage can pass on a signal by executing this method. The signal will be sent to the receiving stage.
	 *
	 * @param signal
	 *            The signal which needs to be passed on.
	 */
	void sendSignal(ISignal signal);

	@Deprecated
	<T> void connectPorts(OutputPort<? extends T> sourcePort, InputPort<T> targetPort);

	/**
	 * Stages report new elements with this method.
	 */
	void reportNewElement();

}
