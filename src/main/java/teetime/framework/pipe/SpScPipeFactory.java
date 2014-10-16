package teetime.framework.pipe;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;

public class SpScPipeFactory implements IPipeFactory {

	@Override
	public <T> IPipe create(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		return create(sourcePort, targetPort, 4);
	}

	@Override
	public <T> IPipe create(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		return new SpScPipe(sourcePort, targetPort, capacity);
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
