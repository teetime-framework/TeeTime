package teetime.variant.methodcallWithPorts.framework.core.pipe;

import teetime.variant.methodcallWithPorts.framework.core.pipe.PipeFactory.PipeOrdering;
import teetime.variant.methodcallWithPorts.framework.core.pipe.PipeFactory.ThreadCommunication;

public interface IPipeFactory {

	<T> IPipe<T> create(int capacity);

	ThreadCommunication getThreadCommunication();

	PipeOrdering getOrdering();

	boolean isGrowable();

}
