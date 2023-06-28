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
package teetime.framework.pipe;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.scheduling.PipeScheduler;
import teetime.framework.signal.ISignal;

/**
 * A pipe implementation used to connect unconnected output ports.
 *
 * @author Christian Wulf
 *
 */
@SuppressWarnings("PMD")
public final class DummyPipe<T> implements IPipe<T> {

	public static final IPipe<?> INSTANCE = new DummyPipe<>();

	private DummyPipe() {
		// singleton
	}

	@Override
	public void add(final Object element) {
		// do nothing
	}

	@Override
	public boolean addNonBlocking(final Object element) {
		return true;
	}

	@Override
	public T removeLast() {
		return null;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public OutputPort<? extends T> getSourcePort() {
		return null;
	}

	@Override
	public InputPort<T> getTargetPort() {
		return null;
	}

	@Override
	public void sendSignal(final ISignal signal) {}

	@Override
	public void reportNewElement() {
		// do nothing
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

	}

	@Override
	public void close() {

	}

	@Override
	public int capacity() {
		return 0;
	}

	@Override
	public void setScheduler(final PipeScheduler scheduler) {
		throw new IllegalStateException("This method must not be called.");
	}

}
