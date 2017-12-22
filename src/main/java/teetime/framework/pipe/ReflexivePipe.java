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

/**
 * Represents an unsynchronized pipe with a capacity of 1. In contrast to the {@link UnsynchedPipe}, this pipe does not execute its successor stage if an element has
 * been added. This pipe has only rare application scenarios. Its main purpose is to connect an output port and an input port of the same stage.
 *
 * @author Christian Wulf (chw)
 *
 * @param <T>
 *            the type of the elements which this pipe should transfer.
 */
public class ReflexivePipe<T> extends AbstractUnsynchedPipe<T> {

	private Object element;

	public ReflexivePipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		super(sourcePort, targetPort);
	}

	@Override
	public void add(final Object element) {
		this.element = element;
	}

	@Override
	public boolean addNonBlocking(final Object element) {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return this.element == null;
	}

	@Override
	public int size() {
		return (this.element == null) ? 0 : 1;
	}

	@Override
	public Object removeLast() {
		final Object temp = this.element;
		this.element = null; // NOPMD
		return temp;
	}

	@Override
	public int capacity() {
		return 1;
	}

}
