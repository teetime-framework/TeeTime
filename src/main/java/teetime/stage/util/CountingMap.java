package teetime.stage.util;

import java.util.HashMap;

public class CountingMap<T> extends HashMap<T, Integer> {

	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -8036971796701648200L;

	public void increment(final T key) {
		if (super.containsKey(key)) {
			Integer i = super.get(key);
			i++;
			super.put(key, i);
		} else {
			super.put(key, 0);
		}
	}

}
