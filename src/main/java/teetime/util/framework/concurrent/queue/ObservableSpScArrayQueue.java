/**
 * Copyright © 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.util.framework.concurrent.queue;

import org.jctools.queues.SpscArrayQueue;

public final class ObservableSpScArrayQueue<E> extends SpscArrayQueue<E> {

	private transient long lastProducerIndex, lastConsumerIndex;

	public ObservableSpScArrayQueue(final int capacity) {
		super(capacity);
	}

	/**
	 * @return the number of pushes to this queue since application start
	 */
	public long getNumPushesSinceAppStart() {
		return currentProducerIndex();
	}

	/**
	 * @return the number of pulls from this queue since application start
	 */
	public long getNumPullsSinceAppStart() {
		return currentConsumerIndex();
	}

	/**
	 * @return the number of pushes to this queue since last method call
	 */
	public long getNumPushes() {
		final long currentProducerIndex = getNumPushesSinceAppStart();
		long diff = currentProducerIndex - lastProducerIndex;
		lastProducerIndex = currentProducerIndex;
		return diff;
	}

	/**
	 * @return the number of pulls to this queue since last method call
	 */
	public long getNumPulls() {
		final long currentConsumerIndex = getNumPullsSinceAppStart();
		long diff = currentConsumerIndex - lastConsumerIndex;
		lastConsumerIndex = currentConsumerIndex;
		return diff;
	}

}
