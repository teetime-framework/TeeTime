package teetime.framework.pipe;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.signal.ISignal;

public interface IPipe {

	boolean add(Object element);

	boolean isEmpty();

	int size();

	Object removeLast();

	Object readLast();

	InputPort<?> getTargetPort();

	void sendSignal(ISignal signal);

	@Deprecated
	<T> void connectPorts(OutputPort<? extends T> sourcePort, InputPort<T> targetPort);

	void reportNewElement();

}
