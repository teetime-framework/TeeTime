package teetime.variant.methodcallWithPorts.framework.core.pipe;

import teetime.util.list.CommittableResizableArrayQueue;
import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;

public final class CommittablePipe extends IntraThreadPipe {

	private final CommittableResizableArrayQueue<Object> elements = new CommittableResizableArrayQueue<Object>(null, 4);

	@Deprecated
	public static <T> void connect(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		IPipe pipe = new CommittablePipe();
		pipe.connectPorts(sourcePort, targetPort);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see teetime.examples.throughput.methodcall.IPipe#add(T)
	 */
	@Override
	public boolean add(final Object element) {
		this.elements.addToTailUncommitted(element);
		this.elements.commit();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see teetime.examples.throughput.methodcall.IPipe#removeLast()
	 */
	@Override
	public Object removeLast() {
		Object element = this.elements.removeFromHeadUncommitted();
		this.elements.commit();
		return element;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see teetime.examples.throughput.methodcall.IPipe#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.elements.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see teetime.examples.throughput.methodcall.IPipe#readLast()
	 */
	@Override
	public Object readLast() {
		return this.elements.getTail();
	}

	public CommittableResizableArrayQueue<?> getElements() {
		return this.elements;
	}

	@Override
	public int size() {
		return this.elements.size();
	}

}
