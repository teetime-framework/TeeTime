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
package teetime.util.concurrent.workstealing;

import java.util.Arrays;

/**
 * 
 * @author Christian Wulf
 * 
 * @see "Dynamic Circular WorkStealing Deque"
 * 
 * @since 1.10
 * 
 * @param <T>
 */
public final class CircularModIntArray<T> {

	private final int logSize;
	private final T[] segment;
	private final int size;

	private int currentIndex;

	/**
	 * 
	 * @param logSize
	 *            The initial size of this array in log2, i.e., the number of bits to use
	 */
	@SuppressWarnings("unchecked")
	public CircularModIntArray(final int logSize) {
		this.logSize = logSize;
		this.segment = (T[]) new Object[1 << this.logSize];
		this.size = this.segment.length;
	}

	public int getCapacity() {
		return this.segment.length;
	}

	public T get(final int i) {
		return this.segment[i % this.size]; // risk of overflow
	}

	public T getNext() {
		int index = this.currentIndex;
		this.currentIndex = (this.currentIndex + 1) % this.size;
		return this.segment[index];
	}

	public void put(final int i, final T o) {
		this.segment[i % this.size] = o; // risk of overflow
	}

	public CircularModIntArray<T> grow(final int b, final int t) {
		final CircularModIntArray<T> a = new CircularModIntArray<T>(this.logSize + 1);
		for (int i = t; i < b; i++) {
			a.put(i, this.get(i));
		}
		return a;
	}

	public CircularModIntArray<T> shrink(final int b, final int t) {
		final CircularModIntArray<T> a = new CircularModIntArray<T>(this.logSize - 1);
		for (int i = t; i < b; i++) {
			a.put(i, this.get(i));
		}
		return a;
	}

	@Override
	public String toString() {
		return Arrays.toString(this.segment);
	}
}
