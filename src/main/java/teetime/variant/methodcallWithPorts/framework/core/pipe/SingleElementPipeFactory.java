package teetime.variant.methodcallWithPorts.framework.core.pipe;

import teetime.variant.methodcallWithPorts.framework.core.pipe.PipeFactory.PipeOrdering;
import teetime.variant.methodcallWithPorts.framework.core.pipe.PipeFactory.ThreadCommunication;

public class SingleElementPipeFactory implements IPipeFactory {

	/**
	 * Hint: The capacity for this pipe implementation is ignored
	 */
	@Override
	public <T> IPipe<T> create(final int capacity) {
		return new SingleElementPipe<T>();
	}

	public ThreadCommunication getThreadCommunication() {
		return ThreadCommunication.INTRA;
	}

	public PipeOrdering getOrdering() {
		return PipeOrdering.ARBITRARY;
	}

	public boolean isGrowable() {
		return false;
	}

}
