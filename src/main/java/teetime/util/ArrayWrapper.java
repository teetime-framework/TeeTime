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
package teetime.util;

public final class ArrayWrapper<T> {

	private final T[] elements;

	// private int lastFreeIndex;

	@SuppressWarnings("unchecked")
	public ArrayWrapper(final int initialCapacity) {
		super();
		this.elements = (T[]) new Object[initialCapacity];
	}

	public T get(final int index) {
		return this.elements[index];
	}

	public void put(final int index, final T element) {
		this.elements[index] = element;
	}

	public int getCapacity() {
		return this.elements.length;
	}

}
