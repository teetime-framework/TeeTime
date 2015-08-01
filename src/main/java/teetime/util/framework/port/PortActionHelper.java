/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://christianwulf.github.io/teetime)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.util.framework.port;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;

import org.jctools.queues.QueueFactory;
import org.jctools.queues.spec.ConcurrentQueueSpec;
import org.jctools.queues.spec.Ordering;
import org.jctools.queues.spec.Preference;

import teetime.framework.Stage;
import teetime.util.framework.concurrent.queue.PCBlockingQueue;
import teetime.util.framework.concurrent.queue.putstrategy.PutStrategy;
import teetime.util.framework.concurrent.queue.putstrategy.YieldPutStrategy;
import teetime.util.framework.concurrent.queue.takestrategy.SCParkTakeStrategy;
import teetime.util.framework.concurrent.queue.takestrategy.TakeStrategy;

public final class PortActionHelper {

	private PortActionHelper() {
		// utility class
	}

	public static <T> BlockingQueue<T> createPortActionQueue() {
		final Queue<T> localQueue = QueueFactory.newQueue(new ConcurrentQueueSpec(1, 1, 0, Ordering.FIFO, Preference.THROUGHPUT));
		final PutStrategy<T> putStrategy = new YieldPutStrategy<T>();
		final TakeStrategy<T> takeStrategy = new SCParkTakeStrategy<T>();
		PCBlockingQueue<T> portActions = new PCBlockingQueue<T>(localQueue, putStrategy, takeStrategy);
		return portActions;
	}

	public static <T extends Stage> void checkForPendingPortActionRequest(final T stage, final BlockingQueue<PortAction<T>> portActions) {
		PortAction<T> dynamicPortAction = portActions.poll();
		if (null != dynamicPortAction) {
			dynamicPortAction.execute(stage);
		}
	}

	// public static <T extends Stage> void checkBlockingForPendingPortActionRequest(final T stage, final BlockingQueue<PortAction<T>> portActions)
	// throws InterruptedException {
	// PortAction<T> dynamicPortAction = portActions.take();
	// dynamicPortAction.execute(stage);
	// }

}
