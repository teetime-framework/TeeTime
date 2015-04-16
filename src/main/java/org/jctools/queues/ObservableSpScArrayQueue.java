/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime.sourceforge.net)
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
package org.jctools.queues;

public final class ObservableSpScArrayQueue<E> extends SpscArrayQueue<E> {

	private transient long lastProducerIndex, lastConsumerIndex;

	public ObservableSpScArrayQueue(final int capacity) {
		super(capacity);
	}

	public long getNumPushes() {
		return currentProducerIndex();
	}

	public long getNumPulls() {
		return currentConsumerIndex();
	}

	public long getProducerFrequency() {
		final long currentProducerIndex = getNumPushes();
		long diff = currentProducerIndex - lastProducerIndex;
		lastProducerIndex = currentProducerIndex;
		return diff;
	}

	public long getConsumerFrequency() {
		final long currentConsumerIndex = getNumPulls();
		long diff = currentConsumerIndex - lastConsumerIndex;
		lastConsumerIndex = currentConsumerIndex;
		return diff;
	}

}
