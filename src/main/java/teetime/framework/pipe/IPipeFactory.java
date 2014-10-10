package teetime.framework.pipe;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.PipeFactory.PipeOrdering;
import teetime.framework.pipe.PipeFactory.ThreadCommunication;

public interface IPipeFactory {

	@Deprecated
	IPipe create(int capacity);

	<T> IPipe create(OutputPort<? extends T> sourcePort, InputPort<T> targetPort);

	<T> IPipe create(OutputPort<? extends T> sourcePort, InputPort<T> targetPort, int capacity);

	ThreadCommunication getThreadCommunication();

	PipeOrdering getOrdering();

	boolean isGrowable();

}
