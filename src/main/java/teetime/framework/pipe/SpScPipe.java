/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime.sourceforge.net)
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
import teetime.util.concurrent.queue.ObservableSpScArrayQueue;

public final class SpScPipe extends AbstractInterThreadPipe implements IMonitorablePipe {

	// private static final Logger LOGGER = LoggerFactory.getLogger(SpScPipe.class);

	private final ObservableSpScArrayQueue<Object> queue;
	// statistics
	private int numWaits;

	<T> SpScPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		super(sourcePort, targetPort);
		this.queue = new ObservableSpScArrayQueue<Object>(capacity);
	}

	@Deprecated
	public static <T> SpScPipe connect(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		final SpScPipe pipe = new SpScPipe(sourcePort, targetPort, capacity);
		pipe.connectPorts(sourcePort, targetPort);
		return pipe;
	}

	@Override
	public boolean add(final Object element) {
		// BETTER introduce a QueueIsFullStrategy
		while (!this.queue.offer(element)) {
			// Thread.yield();
			if (this.cachedTargetStage.getCurrentState() == StageState.TERMINATED) {
				return false;
			}
			this.numWaits++;
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// FIXME Handle it correctly
				e.printStackTrace();
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

	// BETTER find a solution w/o any thread-safe code in this stage
	public synchronized int getNumWaits() {
		return this.numWaits;
	}

	@Override
	public long getPushThroughput() {
		return queue.getProducerFrequency();
	}

	@Override
	public long getPullThroughput() {
		return queue.getConsumerFrequency();
	}

	@Override
	public long getNumPushes() {
		return queue.getNumPushes();
	}

	@Override
	public long getNumPulls() {
		return queue.getNumPulls();
	}

}
