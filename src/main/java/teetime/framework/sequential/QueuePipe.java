/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
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
 ***************************************************************************/
package teetime.framework.sequential;

import java.util.ArrayList;
import java.util.List;

import teetime.framework.core.AbstractPipe;
import teetime.framework.core.IInputPort;
import teetime.framework.core.IOutputPort;
import teetime.framework.core.ISink;
import teetime.framework.core.ISource;
import teetime.util.concurrent.workstealing.CircularWorkStealingDeque;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
public class QueuePipe<T> extends AbstractPipe<T> {

	private final List<T> queue = new ArrayList<T>(10);

	// private final List<T> queue = new ReservableArrayList<T>(10);

	// private final ObjectPooledLinkedList<T> queue = new ObjectPooledLinkedList<T>();

	static public <S0 extends ISource, S1 extends ISink<S1>, T> void connect(final IOutputPort<S0, ? extends T> sourcePort, final IInputPort<S1, T> targetPort) {
		final QueuePipe<T> pipe = new QueuePipe<T>();
		pipe.setSourcePort(sourcePort);
		pipe.setTargetPort(targetPort);
	}

	@Override
	public void putInternal(final T element) {
		this.queue.add(element);
		// this.queue.push(element);
	}

	@Override
	public void putMultiple(final List<T> elements) {
		this.queue.addAll(elements);
		throw new IllegalStateException();
	}

	@Override
	public T tryTakeInternal() {
		// return this.queue.poll();
		return this.queue.remove(this.queue.size() - 1);
		// return this.queue.pop();
	}

	@Override
	public T take() {
		final T element = this.tryTake();
		if (element == null) {
			throw CircularWorkStealingDeque.DEQUE_IS_EMPTY_EXCEPTION;
		}
		return element;
	}

	@Override
	public T read() {
		// return this.queue.peek();
		return this.queue.get(this.queue.size() - 1);
		// return this.queue.read();
	}

	@Override
	public List<?> tryTakeMultiple(final int numElementsToTake) {
		throw new IllegalStateException("Taking more than one element is not possible. You tried to take " + numElementsToTake + " items.");
	}

	@Override
	public boolean isEmpty() {
		return this.queue.isEmpty();
		// return this.queue.size() == 0;
	}

}
