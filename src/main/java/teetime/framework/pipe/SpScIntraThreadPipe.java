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

import java.util.Queue;

import org.jctools.queues.QueueFactory;
import org.jctools.queues.spec.ConcurrentQueueSpec;

import teetime.framework.AbstractIntraThreadPipe;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;

/**
 * Represents a less efficient implementation of an intra-thread pipe.
 *
 * @author Christian Wulf
 *
 * @param <T>
 */
final class SpScIntraThreadPipe<T> extends AbstractIntraThreadPipe {

	private final Queue<Object> queue;

	SpScIntraThreadPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		super(sourcePort, targetPort);
		queue = QueueFactory.newQueue(ConcurrentQueueSpec.createBoundedSpsc(1));
	}

	@Override
	public boolean add(final Object element) {
		return queue.offer(element);
	}

	@Override
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	@Override
	public int size() {
		return queue.size();
	}

	@Override
	public Object removeLast() {
		return queue.poll();
	}

}
