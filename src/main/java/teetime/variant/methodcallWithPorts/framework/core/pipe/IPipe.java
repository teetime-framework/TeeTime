package teetime.variant.methodcallWithPorts.framework.core.pipe;

import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;
import teetime.variant.methodcallWithPorts.framework.core.signal.ISignal;

public interface IPipe {

	boolean add(Object element);

	boolean isEmpty();

	int size();

	Object removeLast();

	Object readLast();

	InputPort<?> getTargetPort();

	void setSignal(ISignal signal);

	@Deprecated
	<T> void connectPorts(OutputPort<? extends T> sourcePort, InputPort<T> targetPort);

	void reportNewElement();

}
