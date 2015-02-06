/**
 * Copyright (C) 2015 TeeTime (http://teetime.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.framework;

import java.lang.Thread.State;
import java.util.Queue;

import org.jctools.queues.QueueFactory;
import org.jctools.queues.spec.ConcurrentQueueSpec;
import org.jctools.queues.spec.Ordering;
import org.jctools.queues.spec.Preference;
import org.slf4j.LoggerFactory;

import teetime.framework.signal.ISignal;

public abstract class AbstractInterThreadPipe extends AbstractPipe {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AbstractInterThreadPipe.class);

	private final Queue<ISignal> signalQueue = QueueFactory.newQueue(new ConcurrentQueueSpec(1, 1, 0, Ordering.FIFO, Preference.THROUGHPUT));

	protected <T> AbstractInterThreadPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		super(sourcePort, targetPort);
	}

	@Override
	public void sendSignal(final ISignal signal) {
		this.signalQueue.offer(signal);

		Thread owningThread = cachedTargetStage.getOwningThread();
		if (owningThread == null && LOGGER.isWarnEnabled()) {
			LOGGER.warn("owningThread of " + cachedTargetStage + " is null.");
		}
		if (null != owningThread && isThreadWaiting(owningThread)) { // FIXME remove the null check for performance
			owningThread.interrupt();
		}
	}

	protected final boolean isThreadWaiting(final Thread thread) {
		final State state = thread.getState(); // store state in variable for performance reasons
		return state == State.WAITING || state == State.TIMED_WAITING;
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
