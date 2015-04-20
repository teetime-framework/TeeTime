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

public final class SingleElementPipe extends AbstractIntraThreadPipe {

	private Object element;

	<T> SingleElementPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		super(sourcePort, targetPort);
	}

	@Deprecated
	public static <T> void connect(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		final IPipe pipe = new SingleElementPipe(null, null);
		pipe.connectPorts(sourcePort, targetPort);
	}

	@Override
	public boolean add(final Object element) {
		this.element = element;
		this.reportNewElement();
		return true;
	}

	@Override
	public Object removeLast() {
		final Object temp = this.element;
		this.element = null;
		return temp;
	}

	@Override
	public boolean isEmpty() {
		return this.element == null;
	}

	@Override
	public int size() {
		return (this.element == null) ? 0 : 1;
	}

}
