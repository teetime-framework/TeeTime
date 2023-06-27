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

import org.jctools.queues.QueueFactory;
import org.jctools.queues.spec.ConcurrentQueueSpec;
import org.jctools.queues.spec.Ordering;
import org.jctools.queues.spec.Preference;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;

public class UnboundedSynchedPipe<T> extends AbstractSynchedPipe<T> {

	private final Queue<T> queue;

	public UnboundedSynchedPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		super(sourcePort, targetPort);
		ConcurrentQueueSpec specification = new ConcurrentQueueSpec(1, 1, 0, Ordering.FIFO, Preference.THROUGHPUT);
		this.queue = QueueFactory.newQueue(specification);
	}

	@Override
	public void add(final T element) {
		this.queue.add(element);
		getScheduler().onElementAdded(this);
	}

	@Override
	public boolean addNonBlocking(final T element) {
		boolean offered = this.queue.offer(element);
		if (offered) {
			getScheduler().onElementAdded(this);
		} else {
			getScheduler().onElementNotAdded(this);
		}
		return offered;
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
	public int capacity() {
		return -1;
	}

}
