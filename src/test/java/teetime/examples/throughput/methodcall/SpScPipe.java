package teetime.examples.throughput.methodcall;

import teetime.util.concurrent.spsc.FFBufferOrdered3;

public class SpScPipe<T> implements IPipe<T> {

	private final FFBufferOrdered3<T> queue = new FFBufferOrdered3<T>(4);

	public static <T> void connect(final OutputPort<T> sourcePort, final InputPort<T> targetPort) {
		IPipe<T> pipe = new SpScPipe<T>();
		sourcePort.pipe = pipe;
		targetPort.pipe = pipe;
	}

	@Override
	public void add(final T element) {
		this.queue.offer(element);
	}

	@Override
	public T removeLast() {
		return this.queue.poll();
	}

	@Override
	public boolean isEmpty() {
		return this.queue.isEmpty();
	}

	@Override
	public int size() {
		return this.queue.size();
	}

	@Override
	public T readLast() {
		return this.queue.peek();
	}

}