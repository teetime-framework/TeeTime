package teetime.framework.pipe;

import java.util.Queue;

import org.jctools.queues.QueueFactory;
import org.jctools.queues.spec.ConcurrentQueueSpec;

import teetime.framework.AbstractIntraThreadPipe;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;

/**
 * Represents a less efficient implementation of an intra-thread pipe.
 *
 * @author Christian Wulf
 *
 * @param <T>
 */
public final class SpScIntraThreadPipe<T> extends AbstractIntraThreadPipe {

	private final Queue<Object> queue;

	public SpScIntraThreadPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		super(sourcePort, targetPort);
		queue = QueueFactory.newQueue(ConcurrentQueueSpec.createBoundedSpsc(1));
	}

	@Override
	public boolean add(final Object element) {
		return queue.offer(element);
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
