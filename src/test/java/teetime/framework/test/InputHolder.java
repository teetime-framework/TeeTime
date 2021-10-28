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
package teetime.framework.test;

import java.util.List;

import teetime.framework.InputPort;

public class InputHolder<I> {

	private final StageTester stageTester;
	private final List<Object> inputElements;

	@SuppressWarnings("unchecked")
	InputHolder(final StageTester stageTester, final List<I> inputElements) {
		this.stageTester = stageTester;
		this.inputElements = (List<Object>) inputElements;
	}

	@SuppressWarnings("unchecked")
	public StageTester to(final InputPort<? super I> inputPort) { // NOPMD deliberately chosen name
		List<InputPort<?>> inputPorts = this.stageTester.getStageUnderTest().getInputPorts();
		if (!inputPorts.contains(inputPort)) {
			throw new InvalidTestCaseSetupException("The given input port does not belong to the stage which should be tested.");
		}
		InputPort<Object> castedPort = (InputPort<Object>) inputPort;
		this.stageTester.getInputElementsByPort().put(castedPort, inputElements); // overwrite

		return stageTester;
	}

}
