package teetime.framework.pipe;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;

public class SingleElementPipeFactory implements IPipeFactory {

	@Override
	public <T> IPipe create(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		return create(sourcePort, targetPort, 1);
	}

	/**
	 * Hint: The capacity for this pipe implementation is ignored
	 */
	@Override
	public <T> IPipe create(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		return new SingleElementPipe(sourcePort, targetPort);
	}

	@Override
	public ThreadCommunication getThreadCommunication() {
		return ThreadCommunication.INTRA;
	}

	@Override
	public PipeOrdering getOrdering() {
		return PipeOrdering.ARBITRARY;
	}

	@Override
	public boolean isGrowable() {
		return false;
	}

}
