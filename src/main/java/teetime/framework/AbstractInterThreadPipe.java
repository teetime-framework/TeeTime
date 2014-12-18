package teetime.framework;

import java.lang.Thread.State;
import java.util.Queue;

import org.jctools.queues.QueueFactory;
import org.jctools.queues.spec.ConcurrentQueueSpec;
import org.jctools.queues.spec.Ordering;
import org.jctools.queues.spec.Preference;

import teetime.framework.signal.ISignal;

public abstract class AbstractInterThreadPipe extends AbstractPipe {

	private final Queue<ISignal> signalQueue = QueueFactory.newQueue(new ConcurrentQueueSpec(1, 1, 0, Ordering.FIFO, Preference.THROUGHPUT));

	protected <T> AbstractInterThreadPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		super(sourcePort, targetPort);
	}

	@Override
	public void sendSignal(final ISignal signal) {
		this.signalQueue.offer(signal);
		System.out.println("send signal: " + signal + " to " + cachedTargetStage);

		Thread owningThread = cachedTargetStage.getOwningThread();
		if (null != owningThread && (owningThread.getState() == State.WAITING || owningThread.getState() == State.TIMED_WAITING)) {
			owningThread.interrupt();
			System.out.println("interrupted " + owningThread);
		}
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
	public void reportNewElement() { // NOPMD
		// do nothing
	}
}
