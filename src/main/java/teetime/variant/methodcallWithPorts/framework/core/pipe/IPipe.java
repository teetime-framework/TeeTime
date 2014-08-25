package teetime.variant.methodcallWithPorts.framework.core.pipe;

import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;
import teetime.variant.methodcallWithPorts.framework.core.signal.Signal;

public interface IPipe<T> {

	boolean add(T element);

	T removeLast();

	boolean isEmpty();

	int size();

	T readLast();

	// void close();
	//
	// boolean isClosed();

	InputPort<T> getTargetPort();

	void setTargetPort(InputPort<T> targetPort);

	void setSignal(Signal signal);

	void connectPorts(OutputPort<T> sourcePort, InputPort<T> targetPort);

}
