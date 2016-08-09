/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
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
package teetime.util.framework.concurrent.queue;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import teetime.util.framework.concurrent.queue.putstrategy.PutStrategy;
import teetime.util.framework.concurrent.queue.takestrategy.TakeStrategy;

public final class PCBlockingQueue<E> implements BlockingQueue<E> {

	private final Queue<E> q;
	private final PutStrategy<E> putStrategy;
	private final TakeStrategy<E> takeStrategy;

	// public PCBlockingQueue(final Queue<E> q, final Class<TakeStrategy<E>> takeStrategyClass, final Class<PutStrategy<E>> putStrategyClass) {
	public PCBlockingQueue(final Queue<E> q, final PutStrategy<E> putStrategy, final TakeStrategy<E> takeStrategy) {
		this.q = q;
		this.putStrategy = putStrategy;
		this.takeStrategy = takeStrategy;
	}

	@Override
	public void put(final E e) throws InterruptedException {
		putStrategy.backoffOffer(q, e); // internally calls "offer(e)"
	}

	@Override
	public E take() throws InterruptedException {
		return takeStrategy.waitPoll(q);
	}

	@Override
	public boolean offer(final E e) {
		boolean offered = q.offer(e);
		if (offered) {
			takeStrategy.signal();
		}
		return offered;
	}

	@Override
	public boolean offer(final E e, final long timeout, final TimeUnit unit) throws InterruptedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public E poll() {
		E e = q.poll();
		if (e != null) {
			putStrategy.signal();
		}
		return e;
	}

	@Override
	public E poll(final long timeout, final TimeUnit unit) throws InterruptedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int remainingCapacity() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int drainTo(final Collection<? super E> c) {
		int count = 0;
		E e;
		while ((e = poll()) != null) {
			c.add(e);
			count++;
		}
		return count;
	}

	@Override
	public int drainTo(final Collection<? super E> c, final int maxElements) {
		int count = 0;
		E e;
		while (((e = poll()) != null) && count < maxElements) {
			c.add(e);
			count++;
		}
		return count;
	}

	@Override
	public boolean add(final E e) {
		return q.add(e);
	}

	@Override
	public int size() {
		return q.size();
	}

	@Override
	public boolean isEmpty() {
		return q.isEmpty();
	}

	@Override
	public boolean contains(final Object o) {
		return q.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return q.iterator();
	}

	@Override
	public boolean remove(final Object o) {
		return q.remove(o);
	}

	@Override
	public E remove() {
		return q.remove();
	}

	@Override
	public <T> T[] toArray(final T[] a) {
		return q.toArray(a);
	}

	@Override
	public Object[] toArray() {
		return q.toArray();
	}

	@Override
	public E element() {
		return q.element();
	}

	@Override
	public E peek() {
		return q.peek();
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		return q.containsAll(c);
	}

	@Override
	public boolean addAll(final Collection<? extends E> c) {
		return q.addAll(c);
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		return q.removeAll(c);
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		return q.retainAll(c);
	}

	@Override
	public void clear() {
		q.clear();
	}

	@Override
	public boolean equals(final Object o) {
		return q.equals(o);
	}

	@Override
	public int hashCode() {
		return q.hashCode();
	}

}
