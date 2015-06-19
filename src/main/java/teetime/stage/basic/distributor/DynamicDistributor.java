package teetime.stage.basic.distributor;

import java.util.Queue;

import org.jctools.queues.QueueFactory;
import org.jctools.queues.spec.ConcurrentQueueSpec;
import org.jctools.queues.spec.Ordering;
import org.jctools.queues.spec.Preference;

import teetime.framework.OutputPort;
import teetime.stage.basic.distributor.DynamicPortActionContainer.DynamicPortAction;
import teetime.util.concurrent.queue.PCBlockingQueue;
import teetime.util.concurrent.queue.putstrategy.PutStrategy;
import teetime.util.concurrent.queue.putstrategy.YieldPutStrategy;
import teetime.util.concurrent.queue.takestrategy.SCParkTakeStrategy;
import teetime.util.concurrent.queue.takestrategy.TakeStrategy;

public class DynamicDistributor<T> extends Distributor<T> {

	private final PCBlockingQueue<DynamicPortActionContainer<T>> actions;

	public DynamicDistributor() {
		final Queue<DynamicPortActionContainer<T>> localQueue = QueueFactory.newQueue(new ConcurrentQueueSpec(1, 1, 0, Ordering.FIFO, Preference.THROUGHPUT));
		final PutStrategy<DynamicPortActionContainer<T>> putStrategy = new YieldPutStrategy<DynamicPortActionContainer<T>>();
		final TakeStrategy<DynamicPortActionContainer<T>> takeStrategy = new SCParkTakeStrategy<DynamicPortActionContainer<T>>();
		actions = new PCBlockingQueue<DynamicPortActionContainer<T>>(localQueue, putStrategy, takeStrategy);
	}

	@Override
	protected void execute(final T element) {
		checkForPendingPortActionRequest();

		super.execute(element);
	}

	private void checkForPendingPortActionRequest() {
		DynamicPortActionContainer<T> dynamicPortAction = actions.poll();
		switch (dynamicPortAction.getDynamicPortAction()) {
		case CREATE:
			OutputPort<T> newOutputPort = createOutputPort();
			dynamicPortAction.execute(newOutputPort);
			break;
		case REMOVE:
			// TODO implement "remove port at runtime"
			break;
		default:
			if (logger.isWarnEnabled()) {
				logger.warn("Unhandled switch case of " + DynamicPortAction.class.getName() + ": " + dynamicPortAction.getDynamicPortAction());
			}
			break;
		}
	}
}
