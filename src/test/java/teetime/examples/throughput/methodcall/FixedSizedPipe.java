package teetime.examples.throughput.methodcall;

public class FixedSizedPipe<T> implements IPipe<T> {

	// private static final class ArrayWrapper2<T> {
	//
	// private final T[] elements;
	//
	// // private int lastFreeIndex;
	//
	// @SuppressWarnings("unchecked")
	// public ArrayWrapper2(final int initialCapacity) {
	// super();
	// this.elements = (T[]) new Object[initialCapacity];
	// }
	//
	// public final T get(final int index) {
	// return this.elements[index];
	// }
	//
	// public final void put(final int index, final T element) {
	// this.elements[index] = element;
	// }
	//
	// public final int getCapacity() {
	// return this.elements.length;
	// }
	//
	// }

	@SuppressWarnings("unchecked")
	private final T[] elements = (T[]) new Object[4];
	// private final ArrayWrapper2<T> elements = new ArrayWrapper2<T>(2);
	private int lastFreeIndex;

	public static <T> void connect(final OutputPort<T> sourcePort, final InputPort<T> targetPort) {
		IPipe<T> pipe = new FixedSizedPipe<T>();
		sourcePort.pipe = pipe;
		targetPort.pipe = pipe;
	}

	@Override
	public void add(final T element) {
		if (this.lastFreeIndex == this.elements.length) {
			// if (this.lastFreeIndex == this.elements.getCapacity()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// grow
		}
		this.elements[this.lastFreeIndex++] = element;
		// this.elements.put(this.lastFreeIndex++, element);
	}

	@Override
	public T removeLast() {
		return this.elements[--this.lastFreeIndex];
		// return this.elements.get(--this.lastFreeIndex);
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

}
