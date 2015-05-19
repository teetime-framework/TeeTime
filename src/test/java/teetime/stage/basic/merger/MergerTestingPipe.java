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
package teetime.stage.basic.merger;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.IPipe;
import teetime.framework.signal.ISignal;
import teetime.framework.signal.StartingSignal;
import teetime.framework.signal.TerminatingSignal;

class MergerTestingPipe implements IPipe {

	private boolean startSent = false;
	private boolean terminateSent = false;

	@Override
	public void sendSignal(final ISignal signal) {
		if (signal instanceof StartingSignal) {
			this.startSent = true;
		} else if (signal instanceof TerminatingSignal) {
			this.terminateSent = true;
		}
	}

	public boolean startSent() {
		return this.startSent;
	}

	public boolean terminateSent() {
		return this.terminateSent;
	}

	public void reset() {
		this.startSent = false;
		this.terminateSent = false;
	}

	@Override
	public boolean add(final Object element) {
		return false;
	}

	@Override
	public boolean addNonBlocking(final Object element) {
		return add(element);
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public Object removeLast() {
		return null;
	}

	@Override
	public InputPort<?> getTargetPort() {
		return null;
	}

	@Override
	public <T> void connectPorts(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {

	}

	@Override
	public void reportNewElement() {

	}

	@Override
	public boolean isClosed() {
		return false;
	}

	@Override
	public boolean hasMore() {
		return false;
	}

	@Override
	public void waitForStartSignal() throws InterruptedException {}

	@Override
	public void waitForInitializingSignal() throws InterruptedException {}

	@Override
	public void close() {}

}
