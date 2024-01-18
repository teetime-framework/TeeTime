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

import teetime.framework.AbstractStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.StageFacade;
import teetime.framework.StageState;

class PrimitiveStageUnderTest implements StageUnderTest {

	private final AbstractStage stage;

	public PrimitiveStageUnderTest(final AbstractStage stage) {
		this.stage = stage;

		if (stage.getCurrentState() != StageState.CREATED) {
			String message = "This stage has already been tested in this test method. Move this test into a new test method.";
			throw new InvalidTestCaseSetupException(message);
		}

	}

	@Override
	public List<InputPort<?>> getInputPorts() {
		return StageFacade.INSTANCE.getInputPorts(stage);
	}

	@Override
	public List<OutputPort<?>> getOutputPorts() {
		return StageFacade.INSTANCE.getOutputPorts(stage);
	}

	@Override
	public void declareActive() {
		stage.declareActive();
	}

}
