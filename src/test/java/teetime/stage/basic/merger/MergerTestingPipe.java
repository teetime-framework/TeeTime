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
package teetime.stage.basic.merger;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.IPipe;
import teetime.framework.scheduling.PipeScheduler;
import teetime.framework.signal.ISignal;
import teetime.framework.signal.StartingSignal;
import teetime.framework.signal.TerminatingSignal;

public class MergerTestingPipe implements IPipe<Object> {

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
	public void add(final Object element) {
		throw new IllegalStateException();
	}

	@Override
	public boolean addNonBlocking(final Object element) {
		// return add(element);
		throw new IllegalStateException();
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
	public int capacity() {
		return 0;
	}

	@Override
	public Object removeLast() {
		return null;
	}

	@Override
	public OutputPort<?> getSourcePort() {
		return null;
	}

	@Override
	public InputPort<Object> getTargetPort() {
		return null;
	}

	@Override
	public void reportNewElement() { // NOPMD

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
	public void waitForStartSignal() throws InterruptedException {
		throw new IllegalStateException();
	}

	@Override
	public void close() {
		throw new IllegalStateException();
	}

	@Override
	public void setScheduler(final PipeScheduler scheduler) {
		throw new IllegalStateException();
	}

}
