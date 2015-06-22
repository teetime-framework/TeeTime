package teetime.stage.basic.distributor.dynamic;

import java.util.Queue;

import org.jctools.queues.QueueFactory;
import org.jctools.queues.spec.ConcurrentQueueSpec;
import org.jctools.queues.spec.Ordering;
import org.jctools.queues.spec.Preference;

import teetime.framework.DynamicOutputPort;
import teetime.stage.basic.distributor.Distributor;
import teetime.util.concurrent.queue.PCBlockingQueue;
import teetime.util.concurrent.queue.putstrategy.PutStrategy;
import teetime.util.concurrent.queue.putstrategy.YieldPutStrategy;
import teetime.util.concurrent.queue.takestrategy.SCParkTakeStrategy;
import teetime.util.concurrent.queue.takestrategy.TakeStrategy;

public class DynamicDistributor<T> extends Distributor<T> {

	protected final PCBlockingQueue<PortAction<T>> portActions;

	public DynamicDistributor() {
		final Queue<PortAction<T>> localQueue = QueueFactory.newQueue(new ConcurrentQueueSpec(1, 1, 0, Ordering.FIFO, Preference.THROUGHPUT));
		final PutStrategy<PortAction<T>> putStrategy = new YieldPutStrategy<PortAction<T>>();
		final TakeStrategy<PortAction<T>> takeStrategy = new SCParkTakeStrategy<PortAction<T>>();
		portActions = new PCBlockingQueue<PortAction<T>>(localQueue, putStrategy, takeStrategy);
	}

	@Override
	protected void execute(final T element) {
		try {
			checkForPendingPortActionRequest();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.execute(element);
	}

	private void checkForPendingPortActionRequest() throws InterruptedException {
		PortAction<T> dynamicPortAction = getPortAction();
		if (null != dynamicPortAction) { // check if getPortAction() uses polling
			dynamicPortAction.execute(this);
		}
	}

	protected PortAction<T> getPortAction() throws InterruptedException {
		return portActions.poll();
	}

	@Override
	public void removeDynamicPort(final DynamicOutputPort<?> dynamicOutputPort) {
		super.removeDynamicPort(dynamicOutputPort);
	}

	public boolean addPortActionRequest(final PortAction<T> newPortActionRequest) {
		return portActions.offer(newPortActionRequest);
	}
}
