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

import teetime.framework.pipe.DummyPipe;
import teetime.framework.signal.ISignal;
import teetime.framework.signal.TerminatingSignal;

/**
 *
 * @author Christian Wulf
 *
 * @param <T>
 *            the type of elements to be sent
 *
 * @since 1.0
 */
public class OutputPort<T> extends AbstractPort<T> {

	OutputPort(final Class<T> type, final AbstractStage owningStage, final String portName) {
		super(type, owningStage, portName);
		setPipe(DummyPipe.INSTANCE);
	}

	/**
	 * For testing purposes only.
	 */
	public OutputPort() {
		super(null, null, null);
	}

	/**
	 *
	 * Guarantees the delivery of the given <code>element</code>.
	 *
	 * @param element
	 *            to be sent; May not be <code>null</code>.
	 *
	 */
	public void send(final T element) {
		this.pipe.add(element);
	}

	/**
	 *
	 * @param element
	 *            to be sent; May not be <code>null</code>.
	 *
	 * @return <code>true</code> iff the <code>element</code> was sent; <code>false</code> otherwise.
	 *
	 * @since 1.1
	 */
	public boolean sendNonBlocking(final T element) {
		return this.pipe.addNonBlocking(element);
	}

	/**
	 *
	 * @param signal
	 *            to be sent; May not be <code>null</code>.
	 */
	public void sendSignal(final ISignal signal) {
		if (signal instanceof TerminatingSignal) {
			pipe.close();
		}
		pipe.sendSignal(signal);
	}

}
