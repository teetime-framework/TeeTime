package teetime.examples.throughput.methodcall;

public class OutputPort<T> {

	private IPipe<T> pipe;

	public void send(final T element) {
		this.pipe.add(element);
	}

	public IPipe<T> getPipe() {
		return this.pipe;
	}

	public void setPipe(final IPipe<T> pipe) {
		this.pipe = pipe;
	}
}
