/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://christianwulf.github.io/teetime)
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
package teetime.framework.pipe;

import teetime.framework.AbstractInterThreadPipe;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.StageState;
import teetime.framework.exceptionHandling.TerminateException;
import teetime.util.framework.concurrent.queue.ObservableSpScArrayQueue;

final class SpScPipe<T> extends AbstractInterThreadPipe<T> implements IMonitorablePipe {

	// private static final Logger LOGGER = LoggerFactory.getLogger(SpScPipe.class);

	private final ObservableSpScArrayQueue<Object> queue;
	// statistics
	private int numWaits;

	SpScPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		super(sourcePort, targetPort, capacity);
		this.queue = new ObservableSpScArrayQueue<Object>(capacity);
	}

	// BETTER introduce a QueueIsFullStrategy
	@Override
	public boolean add(final Object element) {
		while (!this.queue.offer(element)) {
			// Thread.yield();
			if (this.cachedTargetStage.getCurrentState() == StageState.TERMINATED ||
					Thread.currentThread().isInterrupted()) {
				throw TerminateException.INSTANCE;
			}
			this.numWaits++;
			try {
				// LOGGER.trace("queue is full " + numWaits + " " + getTargetPort().getOwningStage().getCurrentState() + " "
				// + getTargetPort().getOwningStage().getOwningThread().getState() + " "
				// + queue.getNumPullsSinceAppStart() + " "
				// + getSourcePort().getOwningStage().getOwningThread().getName() + " -> " + getTargetPort().getOwningStage().getOwningThread().getName());
				Thread.sleep(1);
			} catch (InterruptedException e) {
				throw TerminateException.INSTANCE;
			}
		}
		// this.reportNewElement();
		return true;
	}

	@Override
	public boolean addNonBlocking(final Object element) {
		return this.queue.offer(element);
	}

	@Override
	public Object removeLast() {
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
	public int getNumWaits() {
		return this.numWaits;
	}

	@Override
	public long getPushThroughput() {
		return queue.getNumPushes();
	}

	@Override
	public long getPullThroughput() {
		return queue.getNumPulls();
	}

	@Override
	public long getNumPushes() {
		return queue.getNumPushesSinceAppStart();
	}

	@Override
	public long getNumPulls() {
		return queue.getNumPullsSinceAppStart();
	}

}
