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
package teetime.util.list;

import java.util.HashMap;
import java.util.Map;

public final class ArrayPool<T> {

	// BETTER use a map with int as key due to performance
	private final Map<Integer, T[]> cache = new HashMap<Integer, T[]>();

	@SuppressWarnings("unchecked")
	public T[] acquire(final int capacity) {
		T[] array = this.cache.get(capacity);
		if (array == null) {
			array = (T[]) new Object[capacity];
		}
		return array;
	}

	public void release(final T[] array) {
		this.cache.put(array.length, array);
	}

}
