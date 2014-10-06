package teetime.framework.pipe;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.PipeFactory.PipeOrdering;
import teetime.framework.pipe.PipeFactory.ThreadCommunication;

public class OrderedGrowableArrayPipeFactory implements IPipeFactory {

	@Override
	public IPipe create(final int capacity) {
		return create(null, null, capacity);
	}

	@Override
	public <T> IPipe create(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		return create(sourcePort, targetPort, 4);
	}

	@Override
	public <T> IPipe create(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		return new OrderedGrowableArrayPipe(sourcePort, targetPort, capacity);
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
