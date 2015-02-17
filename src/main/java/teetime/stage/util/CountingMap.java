package teetime.stage.util;

import java.util.HashMap;

/**
 * An implementation of HashMap which can be used to count the occurrence of different keys.
 * This conaitns all methods of HashMap, but is enhanched with the {@link #add(T, Integer)} and {@link #increment(T)} methods.
 *
 * @author Nelson Tavares de Sousa
 *
 * @param <T>
 *            Key type to be count
 */
public class CountingMap<T> extends HashMap<T, Integer> {

	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -8036971796701648200L;

	/**
	 * Increments the value of key by one.
	 *
	 * @param key
	 */
	public void increment(final T key) {
		if (super.containsKey(key)) {
			Integer i = super.get(key);
			i++;
			super.put(key, i);
		} else {
			super.put(key, 1);
		}
	}

	/**
	 * Adds i to the value of key.
	 *
	 * @param key
	 *            Key which is used to add i.
	 * @param i
	 *            Integer value to be added.
	 */
	public void add(final T key, final Integer i) {
		if (super.containsKey(key)) {
			Integer j = super.get(key);
			j += i;
			super.put(key, j);
		} else {
			super.put(key, i);
		}
	}

}
