/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
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
package teetime.util.framework.concurrent;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;

public class SignalingCounter {

	private final IntObjectMap<Object> conditions = new IntObjectHashMap<Object>();
	private int counter;

	// synchronized methods synchronize the map and the counter
	// synchronized(cond) synchronizes the individual numbers for which are being waited for

	public synchronized void inc() {
		counter++;
		conditionalNotifyAll(counter);
	}

	public synchronized void dec() {
		counter--;
		conditionalNotifyAll(counter);
	}

	private synchronized void conditionalNotifyAll(final int number) {
		if (conditions.containsKey(number)) {
			Object cond = conditions.get(number);
			synchronized (cond) {
				cond.notifyAll(); // If you came here because of FindBugs, go on, everything's ok here. You saw nothing!
			}
		}
	}

	public void waitFor(final int number) throws InterruptedException {
		synchronized (this) {
			if (!conditions.containsKey(number)) {
				conditions.put(number, new Object());
			}
		}

		final Object cond = conditions.get(number);

		synchronized (cond) { // counter must be wrapped by synchronized to get the latest value
			while (counter != number) {
				cond.wait();
			}
		}
	}

	public synchronized void inc(final SignalingCounter otherCounter) {
		counter += otherCounter.counter;
		conditionalNotifyAll(counter);
	}

	@Override
	public String toString() {
		return "counter: " + counter + ", " + super.toString();
	}
}
