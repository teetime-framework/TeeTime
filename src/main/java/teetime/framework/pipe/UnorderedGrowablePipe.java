package teetime.framework.pipe;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;

public final class UnorderedGrowablePipe extends IntraThreadPipe {

	private Object[] elements;
	// private final ArrayWrapper2<T> elements = new ArrayWrapper2<T>(2);
	private int lastFreeIndex;

	<T> UnorderedGrowablePipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		super(sourcePort, targetPort);
		this.elements = new Object[capacity];
	}

	@Deprecated
	public static <T> void connect(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		IPipe pipe = new UnorderedGrowablePipe(null, null, 4);
		pipe.connectPorts(sourcePort, targetPort);
	}

	@Override
	public boolean add(final Object element) {
		if (this.lastFreeIndex == this.elements.length) {
			// if (this.lastFreeIndex == this.elements.getCapacity()) {
			this.elements = this.grow();
		}
		this.elements[this.lastFreeIndex++] = element;
		// this.elements.put(this.lastFreeIndex++, element);
		return true;
	}

	@Override
	public Object removeLast() {
		// if (this.lastFreeIndex == 0) {
		// return null;
		// }
		Object element = this.elements[--this.lastFreeIndex];
		this.elements[this.lastFreeIndex] = null;
		// T element = this.elements.get(--this.lastFreeIndex);
		return element;
	}

	@Override
	public boolean isEmpty() {
		return this.lastFreeIndex == 0;
	}

	@Override
	public Object readLast() {
		return this.elements[this.lastFreeIndex - 1];
		// return this.elements.get(this.lastFreeIndex - 1);
	}

	@Override
	public int size() {
		return this.lastFreeIndex;
	}

	private Object[] grow() {
		int newSize = this.elements.length * 2;
		// System.out.println("growing to " + newSize);
		return this.newArray(newSize);
	}

	// we do not support shrink since it causes too much overhead due to the capacity checks
	// private T[] shrink() {
	// int newSize = this.elements.length / 2;
	// return this.newArray(newSize);
	// }

	private Object[] newArray(final int newSize) {
		Object[] newElements = new Object[newSize];

		System.arraycopy(this.elements, 0, newElements, 0, this.elements.length);

		return newElements;
	}

}
