package teetime.examples.throughput.methodcall;

public class OutputPort<T> {

	public IPipe<T> pipe;

	public void send(final T element) {
		this.pipe.add(element);
	}
}
