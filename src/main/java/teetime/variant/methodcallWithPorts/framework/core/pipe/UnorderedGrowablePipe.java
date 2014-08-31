package teetime.variant.methodcallWithPorts.framework.core.pipe;

import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;

public final class UnorderedGrowablePipe<T> extends IntraThreadPipe<T> {

	private final int MIN_CAPACITY;

	private T[] elements;
	// private final ArrayWrapper2<T> elements = new ArrayWrapper2<T>(2);
	private int lastFreeIndex;

	@SuppressWarnings("unchecked")
	UnorderedGrowablePipe() {
		this.MIN_CAPACITY = 4;
		this.elements = (T[]) new Object[this.MIN_CAPACITY];
	}

	@Deprecated
	public static <T> void connect(final OutputPort<T> sourcePort, final InputPort<T> targetPort) {
		IPipe<T> pipe = new UnorderedGrowablePipe<T>();
		pipe.connectPorts(sourcePort, targetPort);
	}

	@Override
	public boolean add(final T element) {
		if (this.lastFreeIndex == this.elements.length) {
			// if (this.lastFreeIndex == this.elements.getCapacity()) {
			this.elements = this.grow();
		}
		this.elements[this.lastFreeIndex++] = element;
		// this.elements.put(this.lastFreeIndex++, element);
		return true;
	}

	@Override
	public T removeLast() {
		// if (this.lastFreeIndex == 0) {
		// return null;
		// }
		T element = this.elements[--this.lastFreeIndex];
		this.elements[this.lastFreeIndex] = null;
		// T element = this.elements.get(--this.lastFreeIndex);
		return element;
	}

	@Override
	public boolean isEmpty() {
		return this.lastFreeIndex == 0;
	}

	@Override
	public T readLast() {
		return this.elements[this.lastFreeIndex - 1];
		// return this.elements.get(this.lastFreeIndex - 1);
	}

	@Override
	public int size() {
		return this.lastFreeIndex;
	}

	private T[] grow() {
		int newSize = this.elements.length * 2;
		// System.out.println("growing to " + newSize);
		return this.newArray(newSize);
	}

	// we do not support shrink since it causes too much overhead due to the capacity checks
	// private T[] shrink() {
	// int newSize = this.elements.length / 2;
	// return this.newArray(newSize);
	// }

	private T[] newArray(final int newSize) {
		@SuppressWarnings("unchecked")
		T[] newElements = (T[]) new Object[newSize];

		System.arraycopy(this.elements, 0, newElements, 0, this.elements.length);

		return newElements;
	}

}
