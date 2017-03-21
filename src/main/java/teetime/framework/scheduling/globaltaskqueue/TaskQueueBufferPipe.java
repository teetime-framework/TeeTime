package teetime.framework.scheduling.globaltaskqueue;

import java.util.Queue;

import org.jctools.queues.QueueFactory;
import org.jctools.queues.spec.*;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.IPipe;
import teetime.framework.scheduling.PipeScheduler;
import teetime.framework.signal.ISignal;

/**
 * Created by nilsziermann on 04.01.17.
 */
class TaskQueueBufferPipe<T> implements IPipe<T> {

	private final Queue<Object> queue;

	private final InputPort<T> targetPort;
	private final OutputPort<? extends T> sourcePort;
	private final IPipe<? extends T> replacedPipe;

	private PipeScheduler scheduler;

	public TaskQueueBufferPipe(final InputPort<T> targetPort, final OutputPort<? extends T> sourcePort, final IPipe<? extends T> replacedPipe) {
		this.targetPort = targetPort;
		if (this.targetPort != null) {
			this.targetPort.setPipe(this);
		}

		this.sourcePort = sourcePort;
		if (this.sourcePort != null) {
			this.sourcePort.setPipe(this);
		}

		this.replacedPipe = replacedPipe;

		ConcurrentQueueSpec specification = new ConcurrentQueueSpec(1, 1, 0, Ordering.FIFO, Preference.THROUGHPUT);
		this.queue = QueueFactory.newQueue(specification);
	}

	@Override
	public boolean add(final Object element) {
		boolean added = queue.offer(element);
		// scheduler.onElementAdded(this);
		return added;
	}

	@Override
	public boolean addNonBlocking(final Object element) {
		return add(element);
	}

	@Override
	public boolean isEmpty() {
		return queue.size() == 0;
	}

	@Override
	public int size() {
		return queue.size();
	}

	@Override
	public int capacity() {
		return Integer.MAX_VALUE;
	}

	@Override
	public Object removeLast() {
		return queue.remove();
	}

	@Override
	public OutputPort<? extends T> getSourcePort() {
		return sourcePort;
	}

	@Override
	public InputPort<T> getTargetPort() {
		return targetPort;
	}

	@Override
	public void sendSignal(final ISignal signal) {

	}

	@Override
	public void reportNewElement() {

	}

	@Override
	public boolean isClosed() {
		return false;
	}

	@Override
	public boolean hasMore() {
		return !isEmpty();
	}

	@Override
	public void waitForStartSignal() throws InterruptedException {

	}

	@Override
	public void close() {

	}

	public IPipe<? extends T> getReplacedPipe() {
		return replacedPipe;
	}

	@Override
	public void setScheduler(final PipeScheduler scheduler) {
		this.scheduler = scheduler;
	}
}
