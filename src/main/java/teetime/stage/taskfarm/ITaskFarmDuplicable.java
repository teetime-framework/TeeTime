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
package teetime.stage.taskfarm;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;

/**
 * Any {@link teetime.framework.AbstractStage AbstractStage} or {@link teetime.framework.CompositeStage CompositeStage} implementing this interface
 * can be used by a Task Farm as an enclosed stage. The enclosed stage may not have more than one input or output port each.
 *
 * @author Christian Claus Wiechmann
 *
 * @param <I>
 *            Input type of the task farm
 * @param <O>
 *            Output Type of the task farm
 */
public interface ITaskFarmDuplicable<I, O> {

	/**
	 * Creates a new instance of the enclosed stage.
	 *
	 * @return new instance
	 */
	public ITaskFarmDuplicable<I, O> duplicate();

	/**
	 * @return single input port of the enclosed stage
	 */
	public InputPort<I> getInputPort();

	/**
	 * @return single output port of the enclosed stage
	 */
	public OutputPort<O> getOutputPort();
}
