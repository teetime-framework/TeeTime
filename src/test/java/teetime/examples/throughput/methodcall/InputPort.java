package teetime.examples.throughput.methodcall;

public class InputPort<T> {

	private IPipe<T> pipe;

	public T receive() {
		T element = this.pipe.removeLast();
		return element;
	}

	public T read() {
		T element = this.pipe.readLast();
		return element;
	}

	public IPipe<T> getPipe() {
		return this.pipe;
	}

	public void setPipe(final IPipe<T> pipe) {
		this.pipe = pipe;
	}

}
