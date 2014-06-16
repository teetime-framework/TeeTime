package teetime.examples.throughput.methodcall;

public class SingleElementPipe<T> implements IPipe<T> {

	private T element;

	public static <T> void connect(final OutputPort<T> sourcePort, final InputPort<T> targetPort) {
		IPipe<T> pipe = new SingleElementPipe<T>();
		sourcePort.pipe = pipe;
		targetPort.pipe = pipe;
	}

	@Override
	public void add(final T element) {
		this.element = element;
	}

	@Override
	public T removeLast() {
		T temp = this.element;
		this.element = null;
		return temp;
	}

	@Override
	public boolean isEmpty() {
		return this.element == null;
	}

	@Override
	public T readLast() {
		return this.element;
	}

	@Override
	public int size() {
		return (this.element == null) ? 0 : 1;
	}

}
