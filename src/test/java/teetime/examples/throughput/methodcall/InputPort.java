package teetime.examples.throughput.methodcall;

public class InputPort<T> {

	public IPipe<T> pipe;

	public T receive() {
		T element = this.pipe.removeLast();
		return element;
	}

	public T read() {
		T element = this.pipe.readLast();
		return element;
	}
}
