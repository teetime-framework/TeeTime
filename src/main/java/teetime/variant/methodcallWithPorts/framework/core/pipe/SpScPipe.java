package teetime.variant.methodcallWithPorts.framework.core.pipe;

import java.util.concurrent.atomic.AtomicReference;

import teetime.util.concurrent.spsc.FFBufferOrdered3;
import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;
import teetime.variant.methodcallWithPorts.framework.core.Signal;

public class SpScPipe<T> extends AbstractPipe<T> {

	private final FFBufferOrdered3<T> queue;
	private int maxSize;
	private final AtomicReference<Signal> signal = new AtomicReference<Signal>();

	private SpScPipe(final int capacity) {
		this.queue = new FFBufferOrdered3<T>(capacity);
	}

	public static <T> SpScPipe<T> connect(final OutputPort<T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		SpScPipe<T> pipe = new SpScPipe<T>(capacity);
		targetPort.setPipe(pipe);
		sourcePort.setPipe(pipe);
		sourcePort.setCachedTargetStage(targetPort.getOwningStage());
		return pipe;
	}

	@Override
	public void add(final T element) {
		this.queue.offer(element);
		this.maxSize = Math.max(this.queue.size(), this.maxSize);
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

	public int getMaxSize() {
		return this.maxSize;
	}

	@Override
	public void setSignal(final Signal signal) {
		this.signal.lazySet(signal); // lazySet is legal due to our single-writer requirement
	}

	public Signal getSignal() {
		return this.signal.get();
	}

}
