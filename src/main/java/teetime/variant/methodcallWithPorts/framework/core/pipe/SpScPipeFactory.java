package teetime.variant.methodcallWithPorts.framework.core.pipe;

import teetime.variant.methodcallWithPorts.framework.core.pipe.PipeFactory.PipeOrdering;
import teetime.variant.methodcallWithPorts.framework.core.pipe.PipeFactory.ThreadCommunication;

public class SpScPipeFactory implements IPipeFactory {

	@Override
	public IPipe create(final int capacity) {
		return new SpScPipe(capacity);
	}

	@Override
	public ThreadCommunication getThreadCommunication() {
		return ThreadCommunication.INTER;
	}

	@Override
	public PipeOrdering getOrdering() {
		return PipeOrdering.QUEUE_BASED;
	}

	@Override
	public boolean isGrowable() {
		return false;
	}
}
