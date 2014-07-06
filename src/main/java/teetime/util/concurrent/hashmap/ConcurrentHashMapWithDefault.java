package teetime.util.concurrent.hashmap;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapWithDefault<K, V> extends ConcurrentHashMap<K, V> {

	private static final long serialVersionUID = 199185976241037967L;

	private final ValueFactory<V> valueFactory;

	private int maxElements;

	public ConcurrentHashMapWithDefault(final ValueFactory<V> valueFactory) {
		this.valueFactory = valueFactory;
	}

	public V getOrCreate(final K key) {
		V value = this.get(key);
		if (value == null) {
			synchronized (this) {
				value = this.get(key);
				if (value == null) { // NOCS (DCL)
					value = this.valueFactory.create();
					this.put(key, value);
					this.maxElements++;
				}
			}
		}
		return value;
	}

	public int getMaxElements() {
		return this.maxElements;
	}
}
