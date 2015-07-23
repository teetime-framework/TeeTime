/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://christianwulf.github.io/teetime)
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
	 * @param <T>
	 *            type of elements which traverse this pipe
	 *
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
	 * @param <T>
	 *            type of elements which traverse this pipe
	 * @return The connecting pipe.
	 */
	<T> IPipe create(OutputPort<? extends T> sourcePort, InputPort<T> targetPort, int capacity);

	/**
	 * @return Whether or not the created pipes are growable
	 */
	boolean isGrowable();

}
