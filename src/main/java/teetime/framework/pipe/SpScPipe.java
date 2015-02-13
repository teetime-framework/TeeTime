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
package teetime.framework.pipe;

import java.util.Queue;

import org.jctools.queues.QueueFactory;
import org.jctools.queues.spec.ConcurrentQueueSpec;
import org.jctools.queues.spec.Ordering;
import org.jctools.queues.spec.Preference;

import teetime.framework.AbstractInterThreadPipe;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;

public final class SpScPipe extends AbstractInterThreadPipe {

	// private static final Logger LOGGER = LoggerFactory.getLogger(SpScPipe.class);

	private final Queue<Object> queue;
	// statistics
	private int numWaits;

	<T> SpScPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		super(sourcePort, targetPort);
		this.queue = QueueFactory.newQueue(new ConcurrentQueueSpec(1, 1, capacity, Ordering.FIFO, Preference.THROUGHPUT));
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
			this.numWaits++;
			Thread.yield();
		}
		// this.reportNewElement();
		return true;
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
	public Object readLast() {
		return this.queue.peek();
	}

	// BETTER find a solution w/o any thread-safe code in this stage
	public synchronized int getNumWaits() {
		return this.numWaits;
	}

}
