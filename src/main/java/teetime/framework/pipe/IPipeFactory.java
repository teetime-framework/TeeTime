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

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;

/**
 * Represents the interface, which is must be defined in every PipeFactory
 */
public interface IPipeFactory {

	/**
	 * Connects two stages with a pipe of default capacity.
	 *
	 * @param sourcePort
	 *            OutputPort of the stage, which produces data.
	 * @param targetPort
	 *            Input port of the receiving stage.
	 * @return The connecting pipe.
	 */
	<T> IPipe create(OutputPort<? extends T> sourcePort, InputPort<T> targetPort);

	/**
	 * Connects two stages with a pipe.
	 *
	 * @param sourcePort
	 *            OutputPort of the stage, which produces data.
	 * @param targetPort
	 *            Input port of the receiving stage.
	 * @param capacity
	 *            Number of elements the pipe can carry.
	 * @return The connecting pipe.
	 */
	<T> IPipe create(OutputPort<? extends T> sourcePort, InputPort<T> targetPort, int capacity);

	/**
	 * @return Type of ThreadCommunication, which is used by the created pipes.
	 */
	ThreadCommunication getThreadCommunication();

	/**
	 * @return Ordering type, which is used by the created pipes.
	 */
	PipeOrdering getOrdering();

	/**
	 * @return Wether or not the created pipes are growable
	 */
	boolean isGrowable();

}
