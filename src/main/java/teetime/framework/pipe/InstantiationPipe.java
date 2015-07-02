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
package teetime.framework.pipe;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.signal.ISignal;

public class InstantiationPipe implements IPipe {

	private static final String ERROR_MESSAGE = "This must not be called while executing the configuration";

	private final InputPort<?> targetPort;
	private final int capacity;

	public <T> InstantiationPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		this.targetPort = targetPort;
		this.capacity = capacity;
		sourcePort.setPipe(this);
		targetPort.setPipe(this);
	}

	public int getCapacity() {
		return capacity;
	}

	@Override
	public InputPort<?> getTargetPort() {
		return this.targetPort;
	}

	@Override
	public boolean add(final Object element) {
		throw new IllegalStateException(ERROR_MESSAGE);
	}

	@Override
	public boolean addNonBlocking(final Object element) {
		throw new IllegalStateException(ERROR_MESSAGE);
	}

	@Override
	public boolean isEmpty() {
		throw new IllegalStateException(ERROR_MESSAGE);
	}

	@Override
	public int size() {
		throw new IllegalStateException(ERROR_MESSAGE);
	}

	@Override
	public Object removeLast() {
		throw new IllegalStateException(ERROR_MESSAGE);
	}

	@Override
	public void sendSignal(final ISignal signal) {
		throw new IllegalStateException(ERROR_MESSAGE);
	}

	@Override
	public void reportNewElement() {
		throw new IllegalStateException(ERROR_MESSAGE);
	}

	@Override
	public boolean isClosed() {
		throw new IllegalStateException(ERROR_MESSAGE);
	}

	@Override
	public boolean hasMore() {
		throw new IllegalStateException(ERROR_MESSAGE);
	}

	@Override
	public void waitForStartSignal() throws InterruptedException {
		throw new IllegalStateException(ERROR_MESSAGE);
	}

	@Override
	public void waitForInitializingSignal() throws InterruptedException {
		throw new IllegalStateException(ERROR_MESSAGE);
	}

	@Override
	public void close() {
		throw new IllegalStateException(ERROR_MESSAGE);
	}

}
