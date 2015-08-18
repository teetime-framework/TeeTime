package teetime.util.framework.concurrent.queue.takestrategy;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class SCBundleTakeStrategy<E> implements TakeStrategy<E> {

	public volatile int storeFence = 0; // NOCS

	private final AtomicReference<Thread> t = new AtomicReference<Thread>(null);
	private final int commitThreshold;

	private volatile long timeoutInNs;

	private int numUncommittedElements;

	public SCBundleTakeStrategy(final int commitThreshold, final long timeoutInNs, final int numUncommittedElements) {
		if (commitThreshold < 1) {
			throw new IllegalArgumentException("commitThreshold is non-positive. Only positive values are permitted.");
		}
		this.commitThreshold = commitThreshold;
		if (timeoutInNs < 1) {
			throw new IllegalArgumentException("timeoutInNs is non-positive. Only positive values are permitted.");
		}
		this.timeoutInNs = timeoutInNs;
	}

	@Override
	public void signal() {
		numUncommittedElements++;
		if (numUncommittedElements == commitThreshold) {
			numUncommittedElements = 0;
			storeFence = 1; // store barrier

			LockSupport.unpark(t.get()); // t.get() load barrier
		}
	}

	@Override
	public E waitPoll(final Queue<E> q) throws InterruptedException {
		E element = q.poll();
		if (element != null) {
			return element;
		}

		t.set(Thread.currentThread());

		do {
			LockSupport.parkNanos(timeoutInNs);
			if (Thread.currentThread().isInterrupted()) {
				throw new InterruptedException("Interrupted while waiting for the queue to become non-empty.");
			}

			t.lazySet(null);

			element = q.poll();
		} while (element == null);

		return element;
	}

	public int getCommitThreshold() {
		return commitThreshold;
	}

	public long getTimeoutInMs() {
		return timeoutInNs;
	}

	/**
	 * Thread-safe setter
	 *
	 * @param timeoutInMs
	 */
	public void setTimeoutInMs(final long timeoutInMs) {
		this.timeoutInNs = timeoutInMs;
	}
}
