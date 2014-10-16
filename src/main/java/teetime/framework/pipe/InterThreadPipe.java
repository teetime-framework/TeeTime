package teetime.framework.pipe;

import java.util.Queue;

import org.jctools.queues.QueueFactory;
import org.jctools.queues.spec.ConcurrentQueueSpec;
import org.jctools.queues.spec.Ordering;
import org.jctools.queues.spec.Preference;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.signal.ISignal;

public abstract class InterThreadPipe extends AbstractPipe {

	private final Queue<ISignal> signalQueue = QueueFactory.newQueue(new ConcurrentQueueSpec(1, 1, 0, Ordering.FIFO, Preference.THROUGHPUT));;

	<T> InterThreadPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		super(sourcePort, targetPort);
	}

	@Override
	public void setSignal(final ISignal signal) {
		this.signalQueue.offer(signal);
	}

	/**
	 * Retrieves and removes the head of the signal queue
	 *
	 * @return Head of signal queue, <code>null</code> if signal queue is empty.
	 */
	public ISignal getSignal() {
		return this.signalQueue.poll();
	}

	@Override
	public void reportNewElement() {
		// do nothing
	}
}
