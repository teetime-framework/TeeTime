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
package teetime.framework.scheduling.globaltaskpool;

import org.jctools.queues.MpmcArrayQueue;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.AbstractSynchedPipe;
import teetime.framework.pipe.IMonitorablePipe;

class BoundedMpMcSynchedPipe<T> extends AbstractSynchedPipe<T> implements IMonitorablePipe {

	private final MpmcArrayQueue<Object> queue;

	private transient long lastProducerIndex, lastConsumerIndex;

	public BoundedMpMcSynchedPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int requestedCapacity) {
		super(sourcePort, targetPort);
		this.queue = new MpmcArrayQueue<Object>(requestedCapacity);
	}

	@Override
	public void add(final Object element) {
		while (!this.queue.offer(element)) {
			getScheduler().onElementNotAdded(this);
		}
		getScheduler().onElementAdded(this);
	}

	@Override
	public boolean addNonBlocking(final Object element) {
		return this.queue.offer(element);
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
	public Object removeLast() {
		return this.queue.poll();
	}

	// TODO: Add interface for this to allow different pipes
	// private void createTask() {
	// tasksCreated++;
	// // TODO: Extract add to task queue in function. Remove also.
	// GlobalTaskQueueScheduling.getTaskQueue().add(cachedTargetStage); // FIXME use a listener; do not depend on a specific scheduling algo!
	// }

	@Override
	public int capacity() {
		return Integer.MAX_VALUE; // unbounded
	}

	@Override
	public long getNumPushesSinceAppStart() {
		return queue.currentProducerIndex();
	}

	@Override
	public long getNumPullsSinceAppStart() {
		return queue.currentConsumerIndex();
	}

	@Override
	public long getPushThroughput() {
		throw new UnsupportedOperationException("we use get/setLastProducerIndex instead");
	}

	@Override
	public long getPullThroughput() {
		final long currentConsumerIndex = getNumPullsSinceAppStart();
		long diff = currentConsumerIndex - lastConsumerIndex;
		lastConsumerIndex = currentConsumerIndex;
		return diff;
	}

	// FIXME only for testing purposes until global task pool scheduling works
	public long getLastProducerIndex() {
		return lastProducerIndex;
	}

	// FIXME only for testing purposes until global task pool scheduling works
	public void setLastProducerIndex(final long lastProducerIndex) {
		this.lastProducerIndex = lastProducerIndex;
	}

	@Override
	public int getNumWaits() {
		return 0;
	}

}
