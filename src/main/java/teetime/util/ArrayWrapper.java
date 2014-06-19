package teetime.util;

public final class ArrayWrapper<T> {

	private final T[] elements;

	// private int lastFreeIndex;

	@SuppressWarnings("unchecked")
	public ArrayWrapper(final int initialCapacity) {
		super();
		this.elements = (T[]) new Object[initialCapacity];
	}

	public final T get(final int index) {
		return this.elements[index];
	}

	public final void put(final int index, final T element) {
		this.elements[index] = element;
	}

	public final int getCapacity() {
		return this.elements.length;
	}

}
