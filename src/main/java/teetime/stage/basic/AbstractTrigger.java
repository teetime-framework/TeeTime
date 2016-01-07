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
package teetime.stage.basic;

import teetime.framework.AbstractStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;

abstract class AbstractTrigger<I, T, O> extends AbstractStage {

	private final InputPort<I> inputPort = createInputPort();
	private final InputPort<T> triggerInputPort = createInputPort();
	private final OutputPort<O> outputPort = createOutputPort();

	protected AbstractTrigger() {
		super();
	}

	@SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
	// disabled while in development
	@Override
	protected void execute() {
		// TODO implement
	}

}
