package teetime.framework.pipe;

import java.util.concurrent.ConcurrentLinkedQueue;

import teetime.framework.AbstractIntraThreadPipe;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;

public final class ConcurrentBlockingIntraThreadPipe<T> extends AbstractIntraThreadPipe {

	private final ConcurrentLinkedQueue<Object> queue;

	ConcurrentBlockingIntraThreadPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		super(sourcePort, targetPort);
		queue = new ConcurrentLinkedQueue<Object>();
	}

	@Override
	public boolean add(final Object element) {
		return queue.add(element);
	}

	@Override
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	@Override
	public int size() {
		return queue.size();
	}

	@Override
	public Object removeLast() {
		return queue.poll();
	}

}
