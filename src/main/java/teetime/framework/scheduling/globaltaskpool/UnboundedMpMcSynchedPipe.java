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
import org.slf4j.LoggerFactory;

import teetime.framework.AbstractStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.AbstractSynchedPipe;
import teetime.framework.pipe.IMonitorablePipe;

class UnboundedMpMcSynchedPipe<T> extends AbstractSynchedPipe<T> implements IMonitorablePipe {

	private final MpmcArrayQueue<Object> queue;

	private transient long lastProducerIndex, lastConsumerIndex;

	public UnboundedMpMcSynchedPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		super(sourcePort, targetPort);
		// TODO: Make this really unbounded or implement capacity check earlier.
		this.queue = new MpmcArrayQueue<Object>(100000);
		// this.queue = new ConcurrentLinkedQueue<>();
	}

	@Override
	public boolean add(final Object element) {
		// try {
		// this.queue.add(element);
		// } catch (IllegalStateException e) {
		// String message = String.format("in pipe %s --> %s", getSourcePort().getOwningStage().getId(), getTargetPort().getOwningStage().getId());
		// throw new IllegalStateException(message, e);
		// }

		// FIXME only for debugging purposes
		boolean yield = false;
		while (!this.queue.offer(element)) {
			// GlobalTaskPoolScheduling globalTaskPoolScheduling = (GlobalTaskPoolScheduling) getScheduler();
			// PrioritizedTaskPool taskPool = globalTaskPoolScheduling.getPrioritizedTaskPool();
			// AbstractStage targetStage = getCachedTargetStage();
			// if (!StageFacade.INSTANCE.shouldBeTerminated(targetStage) && targetStage.getCurrentState() != StageState.TERMINATED) {
			// TeeTimeTaskQueueThreadChw currentThread = (TeeTimeTaskQueueThreadChw) Thread.currentThread();
			// // while (!taskPool.scheduleStage(targetStage)) {
			//// currentThread.processNextStage(taskPool);
			// // }
			// }

			GlobalTaskPoolScheduling globalTaskPoolScheduling = (GlobalTaskPoolScheduling) getScheduler();
			AbstractStage owningStage = getSourcePort().getOwningStage();
			LoggerFactory.getLogger(owningStage.getClass()).debug("Yielding {} cause of the full pipe {}", owningStage, this);
			yield = true;
			globalTaskPoolScheduling.yieldStage(owningStage);
		}
		if (yield) {
			// LoggerFactory.getLogger(getSourcePort().getOwningStage().getClass()).debug("Continue {} cause of the non-full pipe {}",
			// getSourcePort().getOwningStage(),
			// this);
		}

		getScheduler().onElementAdded(this);
		reportNewElement();
		return true;
	}

	@Override
	public boolean addNonBlocking(final Object element) {
		return add(element);
	}

	@Override
	public void reportNewElement() {
		// Create task for new element
		// numElements++;
		// for (; numElements >= 1000; numElements -= 1000) {
		// createTask();
		// }
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
		final long currentProducerIndex = getNumPushesSinceAppStart();
		long diff = currentProducerIndex - lastProducerIndex;
		lastProducerIndex = currentProducerIndex;
		return diff;
	}

	@Override
	public long getPullThroughput() {
		final long currentConsumerIndex = getNumPullsSinceAppStart();
		long diff = currentConsumerIndex - lastConsumerIndex;
		lastConsumerIndex = currentConsumerIndex;
		return diff;
	}

	@Override
	public int getNumWaits() {
		return 0;
	}

}
