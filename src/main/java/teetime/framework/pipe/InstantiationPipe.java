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

import teetime.framework.AbstractIntraThreadPipe;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;

public class InstantiationPipe<T> extends AbstractIntraThreadPipe {

	private final InputPort<T> target;
	private final int capacity;

	public InstantiationPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		super(sourcePort, targetPort);
		this.target = targetPort;
		this.capacity = capacity;
		sourcePort.setPipe(this);
	}

	public int getCapacity() {
		return capacity;
	}

	public InputPort<T> getTarget() {
		return target;
	}

	@Override
	public boolean add(final Object element) {
		throw new IllegalStateException("Should not be called");
	}

	@Override
	public boolean isEmpty() {
		throw new IllegalStateException("Should not be called");
	}

	@Override
	public int size() {
		throw new IllegalStateException("Should not be called");
	}

	@Override
	public Object removeLast() {
		throw new IllegalStateException("Should not be called");
	}

}
