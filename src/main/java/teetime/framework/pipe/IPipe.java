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
 * Represents a pipe that connects an output port with an input port.
 *
 * @author Christian Wulf (chw)
 *
 * @param <T>
 *            the type of the elements which this pipe should transfer.
 */
public interface IPipe<T> {

	/**
	 * Adds an element to the pipe. This method does not return anything because it should guarantee element delivery (as opposed to
	 * {@link #addNonBlocking(Object)}).
	 * If it cannot guarantee element delivery in some special situation, it then must throw an exception.
	 *
	 * @param element
	 *            to be added
	 */
	void add(Object element);

	/**
	 * Adds an element to the pipe.
	 *
	 * @param element
	 *            Element which will be added
	 * @return <code>true</code> if the element could be added, false otherwise
	 */
	boolean addNonBlocking(Object element);

	/**
	 * Checks whether the pipe is empty or not.
	 *
	 * @return <code>true</code> if the pipe is empty, false otherwise.
	 */
	boolean isEmpty();

	/**
	 * @return the current number of elements held by this pipe instance
	 */
	int size();

	/**
	 * @return the maximum number of elements possible to hold by this pipe instance
	 */
	int capacity();

	/**
	 * Retrieves and removes the last element from the pipe.
	 *
	 * @return the last element from the pipe, or <code>null</code> if the pipe is currently empty.
	 */
	Object removeLast();

	/**
	 * @return the output port that is connected to the pipe.
	 */
	OutputPort<? extends T> getSourcePort();

	/**
	 * @return the input port that is connected to the pipe.
	 */
	InputPort<T> getTargetPort();

	/**
	 * A stage can pass on a signal by executing this method. The signal will be sent to the receiving stage.
	 *
	 * @param signal
	 *            The signal which needs to be passed on.
	 */
	void sendSignal(ISignal signal);

	/**
	 * Stages report new elements with this method.
	 *
	 * @deprecated since 3.0. Is removed without replacement.
	 */
	@Deprecated
	void reportNewElement();

	/**
	 * @return <code>true</code> if the pipe is closed, that is, if the pipe is empty <b>and</b> if the source stage will not send any elements anymore (because the
	 *         stage has finished its whole work);
	 *         returns <code>false</code> in all other cases.
	 */
	boolean isClosed(); // FIXME remove dead method?

	/**
	 * @return <code>true</code> if the pipe is not empty, that is, if the pipe contains at least one element.
	 */
	boolean hasMore();

	/**
	 * May only be invoked by the input port and the owning (target) stage.
	 */
	void close();// FIXME remove if migration to TERM element has finished

	// "signal" handling

	void waitForStartSignal() throws InterruptedException;

	void setScheduler(final PipeScheduler scheduler);

}
