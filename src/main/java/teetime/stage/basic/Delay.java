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

import java.util.LinkedList;
import java.util.List;

import teetime.framework.AbstractStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;

public final class Delay<T> extends AbstractStage {

	private final InputPort<T> inputPort = this.createInputPort();
	private final InputPort<Long> timestampTriggerInputPort = this.createInputPort();
	private final OutputPort<T> outputPort = this.createOutputPort();

	private final List<T> bufferedElements = new LinkedList<T>();

	@Override
	protected void execute() {
		T element = inputPort.receive();
		if (null != element) {
			bufferedElements.add(element);
		}

		Long timestampTrigger = this.timestampTriggerInputPort.receive();
		if (null == timestampTrigger) {
			return; // BETTER use returnNoElement(). so far, RunnableProducerStages cannot handle the NOT_ENOUGH__INPUT_EXCEPTION
		}

		sendAllBufferedEllements();
	}

	private void sendAllBufferedEllements() {
		while (!bufferedElements.isEmpty()) {
			T element = bufferedElements.remove(0);
			outputPort.send(element);
		}
	}

	@Override
	public void onTerminating() throws Exception {
		while (null == timestampTriggerInputPort.receive()) {
			// wait for the next trigger
		}

		sendAllBufferedEllements();

		T element;
		while (null != (element = inputPort.receive())) {
			outputPort.send(element);
		}

		super.onTerminating();
	}

	public InputPort<T> getInputPort() {
		return this.inputPort;
	}

	public InputPort<Long> getTimestampTriggerInputPort() {
		return this.timestampTriggerInputPort;
	}

	public OutputPort<T> getOutputPort() {
		return this.outputPort;
	}

}
