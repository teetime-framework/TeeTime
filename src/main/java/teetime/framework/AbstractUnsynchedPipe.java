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
package teetime.framework;

import teetime.framework.signal.ISignal;

/**
 * Represents an unsynchronized pipe which can be used to connect stages within the same thread.
 *
 * @author Christian Wulf (chw)
 *
 * @param <T>
 *            the type of the elements which this pipe should transfer.
 */
public abstract class AbstractUnsynchedPipe<T> extends AbstractPipe<T> {

	private boolean closed;

	protected AbstractUnsynchedPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		super(sourcePort, targetPort, capacity);
	}

	@Override
	public boolean addNonBlocking(final Object element) {
		return add(element);
	}

	@Override
	public final void sendSignal(final ISignal signal) {
		// getTargetPort is always non-null since the framework adds dummy ports if necessary
		this.cachedTargetStage.onSignal(signal, this.getTargetPort());
	}

	@Override
	public final void reportNewElement() {
		this.cachedTargetStage.executeStage();
	}

	@Override
	public boolean isClosed() {
		return closed;
	}

	@Override
	public void close() {
		closed = true;
	}

	@SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
	@Override
	public void waitForStartSignal() throws InterruptedException {
		// do nothing
	}

}
