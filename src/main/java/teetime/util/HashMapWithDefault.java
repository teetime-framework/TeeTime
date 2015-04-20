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
package teetime.util;

import java.util.HashMap;

import teetime.util.concurrent.hashmap.ValueFactory;

/**
 * @author Christian Wulf
 *
 * @since 1.10
 */
public final class HashMapWithDefault<K, V> extends HashMap<K, V> {

	private static final long serialVersionUID = -7958038532219740472L;

	private final ValueFactory<V> valueFactory;

	/**
	 * @since 1.10
	 */
	public HashMapWithDefault(final ValueFactory<V> valueFactory) {
		this.valueFactory = valueFactory;
	}

	/**
	 * @return the corresponding value if the key exists. Otherwise, it creates,
	 *         inserts, and returns a new default value.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public V get(final Object key) {
		V value = super.get(key);
		if (value == null) {
			value = this.valueFactory.create();
			super.put((K) key, value);
		}
		return value;
	}
}
