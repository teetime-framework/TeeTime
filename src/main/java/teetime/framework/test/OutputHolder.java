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

import teetime.framework.OutputPort;

public class OutputHolder<O> {

	private final StageTester stageTester;
	private final List<Object> outputElements;

	@SuppressWarnings("unchecked")
	OutputHolder(final StageTester stageTester, final List<O> outputElements) {
		this.stageTester = stageTester;
		this.outputElements = (List<Object>) outputElements;
	}

	@SuppressWarnings("unchecked")
	public StageTester from(final OutputPort<O> outputPort) {
		List<OutputPort<?>> outputPorts = this.stageTester.getStageUnderTest().getOutputPorts();
		if (!outputPorts.contains(outputPort)) {
			throw new InvalidTestCaseSetupException("The given output port does not belong to the stage which should be tested.");
		}
		OutputPort<Object> castedPort = (OutputPort<Object>) outputPort;
		this.stageTester.getOutputElementsByPort().put(castedPort, outputElements); // overwrite

		return stageTester;
	}

}
