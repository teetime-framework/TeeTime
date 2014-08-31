package teetime.variant.methodcallWithPorts.framework.core.pipe;

import java.util.Queue;

import org.jctools.queues.QueueFactory;
import org.jctools.queues.spec.ConcurrentQueueSpec;
import org.jctools.queues.spec.Ordering;
import org.jctools.queues.spec.Preference;

import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;

public final class SpScPipe<T> extends InterThreadPipe<T> {

	private final Queue<T> queue;
	// statistics
	private int numWaits;

	SpScPipe(final int capacity) {
		ConcurrentQueueSpec concurrentQueueSpec = new ConcurrentQueueSpec(1, 1, capacity, Ordering.FIFO, Preference.THROUGHPUT);
		this.queue = QueueFactory.newQueue(concurrentQueueSpec);
	}

	@Deprecated
	public static <T> SpScPipe<T> connect(final OutputPort<T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		SpScPipe<T> pipe = new SpScPipe<T>(capacity);
		pipe.connectPorts(sourcePort, targetPort);
		return pipe;
	}

	@Override
	public boolean add(final T element) {
		// BETTER introduce a QueueIsFullStrategy
		while (!this.queue.offer(element)) {
			this.numWaits++;
			Thread.yield();
		}

		return true;
	}

	@Override
	public T removeLast() {
		return this.queue.poll();
	}

	@Override
	public boolean isEmpty() {
		return this.queue.isEmpty();
	}

	@Override
	public int size() {
		return this.queue.size();
	}

	@Override
	public T readLast() {
		return this.queue.peek();
	}

	// BETTER find a solution w/o any thread-safe code in this stage
	public synchronized int getNumWaits() {
		return this.numWaits;
	}

}
