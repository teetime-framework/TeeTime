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
package org.jctools.queues;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

public final class ObservableSpScArrayQueue<E> implements Queue<E> {

	private final SpscArrayQueue<E> queue;

	private transient long lastProducerIndex, lastConsumerIndex;

	public ObservableSpScArrayQueue(final int requestedCapacity) {
		this.queue = new SpscArrayQueue<E>(requestedCapacity);
	}

	public long getNumPushes() {
		return queue.lvProducerIndex();
	}

	public long getNumPulls() {
		return queue.lvConsumerIndex();
	}

	public long getProducerFrequency() {
		final long currentProducerIndex = queue.lvProducerIndex();
		long diff = currentProducerIndex - lastProducerIndex;
		lastProducerIndex = currentProducerIndex;
		return diff;
	}

	public long getConsumerFrequency() {
		final long currentConsumerIndex = queue.lvConsumerIndex();
		long diff = currentConsumerIndex - lastConsumerIndex;
		lastConsumerIndex = currentConsumerIndex;
		return diff;
	}

	@Override
	public int hashCode() {
		return queue.hashCode();
	}

	@Override
	public boolean add(final E e) {
		return queue.add(e);
	}

	@Override
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	@Override
	public boolean contains(final Object o) {
		return queue.contains(o);
	}

	@Override
	public E remove() {
		return queue.remove();
	}

	@Override
	public Object[] toArray() {
		return queue.toArray();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof ObservableSpScArrayQueue) {
			return queue.equals(((ObservableSpScArrayQueue<?>) obj).queue);
		}
		return false;
	}

	@Override
	public E element() {
		return queue.element();
	}

	@Override
	public boolean offer(final E e) {
		return queue.offer(e);
	}

	@Override
	public <T> T[] toArray(final T[] a) {
		return queue.toArray(a);
	}

	@Override
	public boolean addAll(final Collection<? extends E> c) {
		return queue.addAll(c);
	}

	@Override
	public E poll() {
		return queue.poll();
	}

	@Override
	public E peek() {
		return queue.peek();
	}

	@Override
	public int size() {
		return queue.size();
	}

	@Override
	public Iterator<E> iterator() {
		return queue.iterator();
	}

	@Override
	public void clear() {
		queue.clear();
	}

	@Override
	public boolean remove(final Object o) {
		return queue.remove(o);
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		return queue.containsAll(c);
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		return queue.removeAll(c);
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		return queue.retainAll(c);
	}

	@Override
	public String toString() {
		return queue.toString();
	}

}
