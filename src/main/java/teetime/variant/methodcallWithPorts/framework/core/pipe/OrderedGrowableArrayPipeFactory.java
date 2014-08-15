package teetime.variant.methodcallWithPorts.framework.core.pipe;

import teetime.variant.methodcallWithPorts.framework.core.pipe.PipeFactory.PipeOrdering;
import teetime.variant.methodcallWithPorts.framework.core.pipe.PipeFactory.ThreadCommunication;

public class OrderedGrowableArrayPipeFactory implements IPipeFactory {

	/**
	 * Hint: The capacity for this pipe implementation is ignored
	 */
	@Override
	public <T> IPipe<T> create(final int capacity) {
		return new OrderedGrowableArrayPipe<T>();
	}

	@Override
	public ThreadCommunication getThreadCommunication() {
		return ThreadCommunication.INTRA;
	}

	@Override
	public PipeOrdering getOrdering() {
		return PipeOrdering.QUEUE_BASED;
	}

	@Override
	public boolean isGrowable() {
		return true;
	}

}
