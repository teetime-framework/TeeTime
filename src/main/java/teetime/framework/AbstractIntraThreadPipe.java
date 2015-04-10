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
package teetime.framework;

import teetime.framework.signal.ISignal;

public abstract class AbstractIntraThreadPipe extends AbstractPipe {

	private boolean isClosed;

	protected <T> AbstractIntraThreadPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		super(sourcePort, targetPort);
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
		return isClosed;
	}

	@Override
	public void close() {
		isClosed = true;
	}

	@SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
	@Override
	public void waitForStartSignal() throws InterruptedException {
		// do nothing
	}
}
