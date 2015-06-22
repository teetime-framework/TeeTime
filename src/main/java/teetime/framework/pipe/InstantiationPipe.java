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
package teetime.framework.pipe;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.signal.ISignal;

public class InstantiationPipe implements IPipe {

	private final InputPort<?> target;
	private final int capacity;

	public <T> InstantiationPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		this.target = targetPort;
		this.capacity = capacity;
		sourcePort.setPipe(this);
	}

	public int getCapacity() {
		return capacity;
	}

	@Override
	public InputPort<?> getTargetPort() {
		return this.target;
	}

	@Override
	public boolean add(final Object element) {
		throw new IllegalStateException("This must not be called while executing the configuration");
	}

	@Override
	public boolean addNonBlocking(final Object element) {
		throw new IllegalStateException("This must not be called while executing the configuration");
	}

	@Override
	public boolean isEmpty() {
		throw new IllegalStateException("This must not be called while executing the configuration");
	}

	@Override
	public int size() {
		throw new IllegalStateException("This must not be called while executing the configuration");
	}

	@Override
	public Object removeLast() {
		throw new IllegalStateException("This must not be called while executing the configuration");
	}

	@Override
	public void sendSignal(final ISignal signal) {
		throw new IllegalStateException("This must not be called while executing the configuration");
	}

	@Override
	public void reportNewElement() {
		throw new IllegalStateException("This must not be called while executing the configuration");
	}

	@Override
	public boolean isClosed() {
		throw new IllegalStateException("This must not be called while executing the configuration");
	}

	@Override
	public boolean hasMore() {
		throw new IllegalStateException("This must not be called while executing the configuration");
	}

	@Override
	public void waitForStartSignal() throws InterruptedException {
		throw new IllegalStateException("This must not be called while executing the configuration");
	}

	@Override
	public void waitForInitializingSignal() throws InterruptedException {
		throw new IllegalStateException("This must not be called while executing the configuration");
	}

	@Override
	public void close() {
		throw new IllegalStateException("This must not be called while executing the configuration");
	}

}
