package teetime.variant.methodcallWithPorts.framework.core.pipe;

import teetime.util.concurrent.spsc.FFBufferOrdered3;
import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;

public class SpScPipe<T> extends AbstractPipe<T> {

	private final FFBufferOrdered3<T> queue;

	private SpScPipe(final int initialCapacity) {
		this.queue = new FFBufferOrdered3<T>(initialCapacity);
	}

	public static <T> void connect(final OutputPort<T> sourcePort, final InputPort<T> targetPort, final int initialCapacity) {
		IPipe<T> pipe = new SpScPipe<T>(initialCapacity);
		targetPort.setPipe(pipe);
		if (sourcePort != null) {
			sourcePort.setPipe(pipe);
			sourcePort.setCachedTargetStage(targetPort.getOwningStage());
		}
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
