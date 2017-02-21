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
package teetime.stage.util;

import com.carrotsearch.hppc.ObjectIntHashMap;
import com.carrotsearch.hppc.ObjectIntMap;
import com.carrotsearch.hppc.procedures.ObjectIntProcedure;

/**
 * An implementation of a map which can be used to count the occurrence of different keys.
 *
 * @since 1.1
 *
 * @author Nelson Tavares de Sousa, Christian Wulf
 *
 * @param <T>
 *            Key type to be count
 */
public final class CountingMap<T> {

	private final ObjectIntMap<T> map = new ObjectIntHashMap<T>();

	/**
	 * Increments the value of key by one.
	 *
	 * @param key
	 *            The key which sould be incremented
	 */
	public void increment(final T key) {
		map.addTo(key, 1);
	}

	/**
	 * Adds i to the value of key.
	 *
	 * @param key
	 *            the key which is used to add i.
	 * @param value
	 *            the value to be added.
	 */
	public void add(final T key, final int value) {
		map.addTo(key, value);
	}

	public void add(final CountingMap<T> otherMap) {
		final ObjectIntProcedure<? super T> procedure = new ObjectIntProcedure<T>() {
			@Override
			public void apply(final T key, final int value) {
				map.addTo(key, value);
			}
		};
		otherMap.map.forEach(procedure);
	}

	public int get(final T key) {
		return map.get(key);
	}

	public int size() {
		return map.size();
	}

	@Override
	public String toString() {
		return this.map.toString();
	}

}
