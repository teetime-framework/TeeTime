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
package teetime.util.framework.concurrent.queue.takestrategy;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public final class SCParkTakeStrategy<E> implements TakeStrategy<E> {

	public volatile int storeFence = 0;

	private final AtomicReference<Thread> t = new AtomicReference<Thread>(null);

	@Override
	// Make sure the offer is visible before unpark
	public void signal()
	{
		storeFence = 1; // store barrier

		LockSupport.unpark(t.get()); // t.get() load barrier
	}

	@Override
	public E waitPoll(final Queue<E> q) throws InterruptedException
	{
		E e = q.poll();
		if (e != null)
		{
			return e;
		}

		t.set(Thread.currentThread());

		while ((e = q.poll()) == null) {
			LockSupport.park();
			if (Thread.currentThread().isInterrupted()) {
				throw new InterruptedException("Interrupted while waiting for the queue to become non-empty.");
			}
		}

		t.lazySet(null);

		return e;
	}
}
