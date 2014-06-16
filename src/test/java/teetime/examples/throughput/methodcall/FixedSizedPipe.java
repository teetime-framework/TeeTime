package teetime.examples.throughput.methodcall;

public class FixedSizedPipe<T> implements IPipe<T> {

	private final T[] elements = (T[]) new Object[4];
	private int lastFreeIndex;

	public static <T> void connect(final OutputPort<T> sourcePort, final InputPort<T> targetPort) {
		IPipe<T> pipe = new FixedSizedPipe<T>();
		sourcePort.pipe = pipe;
		targetPort.pipe = pipe;
	}

	@Override
	public void add(final T element) {
		this.elements[this.lastFreeIndex++] = element;
	}

	@Override
	public T removeLast() {
		return this.elements[--this.lastFreeIndex];
	}

	@Override
	public boolean isEmpty() {
		return this.lastFreeIndex == 0;
	}

	@Override
	public T readLast() {
		return this.elements[this.lastFreeIndex];
	}

}
