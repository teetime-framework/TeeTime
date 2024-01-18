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
package teetime.util.framework.list;

public final class CommittableResizableArrayQueue<T> implements CommittableQueue<T> {

	// private final int MIN_CAPACITY;

	private final ArrayPool<T> arrayPool;
	private T[] elements;

	private int lastFreeIndex;
	private int lastFreeIndexUncommitted;

	@SuppressWarnings("unchecked")
	public CommittableResizableArrayQueue(final Object emptyObject, final int initialCapacity) {
		super();
		this.arrayPool = new ArrayPool<>();
		// this.MIN_CAPACITY = initialCapacity + 1;
		this.elements = this.arrayPool.acquire(initialCapacity + 1);

		this.elements[0] = (T) emptyObject; // optimization: avoids the use of an index out-of-bounds check
		this.clear();
	}

	@Override
	public final T get(final int index) {
		return this.elements[index + 1];
	}

	@Override
	public void addToTailUncommitted(final T element) {
		if (this.lastFreeIndexUncommitted == this.capacity()) {
			this.grow();
		}
		this.put(this.lastFreeIndexUncommitted++, element);
	}

	@Override
	public T removeFromHeadUncommitted() {
		return this.get(--this.lastFreeIndexUncommitted);
	}

	@Override
	// TODO set elements to null to help the gc
	public void commit() {
		this.lastFreeIndex = this.lastFreeIndexUncommitted;
	}

	@Override
	public void rollback() {
		this.lastFreeIndexUncommitted = this.lastFreeIndex;
	}

	@Override
	public int size() {
		return this.lastFreeIndex;
	}

	@Override
	public boolean isEmpty() {
		return this.size() == 0;
	}

	@Override
	public void clear() {
		this.lastFreeIndex = this.lastFreeIndexUncommitted = 0;
	}

	@Override
	public T getTail() {
		return this.get(this.lastFreeIndex - 1);
	}

	private void grow() {
		T[] newElements = this.arrayPool.acquire(this.elements.length * 2);
		this.replaceCurrentArrayBy(newElements);
	}

	private final void replaceCurrentArrayBy(final T[] newElements) { // NOPMD
		this.copyArray(this.elements, newElements);
		this.arrayPool.release(this.elements);
		this.elements = newElements;
	}

	private final void copyArray(final T[] elements, final T[] newElements) { // NOPMD storing arrays is ok
		System.arraycopy(elements, 0, newElements, 0, this.lastFreeIndexUncommitted + 1);
	}

	private final void put(final int index, final T element) {
		this.elements[index + 1] = element;
	}

	private final int capacity() {
		return this.elements.length - 1;
	}

	@Override
	public T removeFromHead() {
		T element = this.removeFromHeadUncommitted();
		this.commit();
		return element;
	}
}
