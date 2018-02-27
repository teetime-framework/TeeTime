/**
 * Copyright © 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.framework.pipe;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;

import org.jctools.queues.QueueFactory;
import org.jctools.queues.spec.ConcurrentQueueSpec;
import org.jctools.queues.spec.Ordering;
import org.jctools.queues.spec.Preference;

import teetime.framework.AbstractPipe;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.signal.ISignal;
import teetime.framework.signal.StartingSignal;
import teetime.framework.signal.ValidatingSignal;
import teetime.util.framework.concurrent.queue.PCBlockingQueue;
import teetime.util.framework.concurrent.queue.putstrategy.PutStrategy;
import teetime.util.framework.concurrent.queue.putstrategy.YieldPutStrategy;
import teetime.util.framework.concurrent.queue.takestrategy.SCParkTakeStrategy;
import teetime.util.framework.concurrent.queue.takestrategy.TakeStrategy;

/**
 *
 * @author Christian Wulf
 *
 * @param <T>
 *            the type of the elements which this pipe should transfer.
 */
public abstract class AbstractSynchedPipe<T> extends AbstractPipe<T> {

	private final BlockingQueue<ISignal> signalQueue;

	private volatile boolean closed;

	protected AbstractSynchedPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		super(sourcePort, targetPort);
		final Queue<ISignal> localSignalQueue = QueueFactory.newQueue(new ConcurrentQueueSpec(1, 1, 0, Ordering.FIFO, Preference.THROUGHPUT));
		final PutStrategy<ISignal> putStrategy = new YieldPutStrategy<ISignal>();
		final TakeStrategy<ISignal> takeStrategy = new SCParkTakeStrategy<ISignal>();
		signalQueue = new PCBlockingQueue<ISignal>(localSignalQueue, putStrategy, takeStrategy);
	}

	@Override
	public void sendSignal(final ISignal signal) {
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

	/**
	 * @deprecated since 3.0. Is removed without replacement.
	 */
	@Deprecated
	@Override
	public void reportNewElement() { // NOPMD
		// default implementation
	}

	@Override
	public final void waitForStartSignal() throws InterruptedException {
		final ISignal signal = signalQueue.take();
		if (signal instanceof ValidatingSignal) {
			this.waitForStartSignal(); // recursive call
			return;
		}
		if (!(signal instanceof StartingSignal)) {
			throw new IllegalStateException(
					"2001 - Expected StartingSignal, but was " + signal.getClass().getSimpleName() + " in " + getTargetPort().getOwningStage().getId());
		}
		cachedTargetStage.onSignal(signal, getTargetPort());
	}

	@Override
	public final boolean isClosed() {
		return closed;
	}

	@Override
	public final void close() {
		closed = true;
	}
}
