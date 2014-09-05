package teetime.variant.methodcallWithPorts.framework.core.pipe;

import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;
import teetime.variant.methodcallWithPorts.framework.core.pipe.PipeFactory.PipeOrdering;
import teetime.variant.methodcallWithPorts.framework.core.pipe.PipeFactory.ThreadCommunication;

public interface IPipeFactory {

	@Deprecated
	IPipe create(int capacity);

	<T> IPipe create(OutputPort<? extends T> sourcePort, InputPort<T> targetPort);

	<T> IPipe create(OutputPort<? extends T> sourcePort, InputPort<T> targetPort, int capacity);

	ThreadCommunication getThreadCommunication();

	PipeOrdering getOrdering();

	boolean isGrowable();

}
