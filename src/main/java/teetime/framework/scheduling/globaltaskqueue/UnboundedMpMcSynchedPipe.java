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
package teetime.framework.scheduling.globaltaskqueue;

import java.util.Queue;

import org.jctools.queues.MpmcArrayQueue;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.AbstractSynchedPipe;

class UnboundedMpMcSynchedPipe<T> extends AbstractSynchedPipe<T> {

	private final Queue<Object> queue;

	// private volatile int numElements = 0;
	// private volatile int tasksCreated = 0;

	public UnboundedMpMcSynchedPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		super(sourcePort, targetPort);
		// TODO: Make this really unbounded or implement capacity check earlier.
		this.queue = new MpmcArrayQueue<Object>(100000);
		// this.queue = new ConcurrentLinkedQueue<>();
	}

	@Override
	public boolean add(final Object element) {
		this.queue.add(element);
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

}
