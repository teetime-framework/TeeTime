package teetime.util.list;

import java.util.HashMap;
import java.util.Map;

public class ArrayPool<T> {

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
