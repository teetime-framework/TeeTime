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
package teetime.stage.quicksort;

import teetime.framework.CompositeStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;

/**
 * A Pipe-and-Filter implementation of the iterative Quicksort algorithm.
 *
 * @author Christian Wulf
 *
 * @see <a href="https://www.geeksforgeeks.org/iterative-quick-sort">https://www.geeksforgeeks.org/iterative-quick-sort</a>
 */
public class QuicksortStage extends CompositeStage {

	private final InputPort<int[]> inputPort;
	private final OutputPort<int[]> outputPort;

	public QuicksortStage() {
		InitStage initStage = new InitStage();
		IfStage ifStage = new IfStage();
		SetupRangeStage setupRangeStage = new SetupRangeStage();
		PartitionStage partitionStage = new PartitionStage();
		PushLeftSideStage pushLeftSideStage = new PushLeftSideStage();
		PushRightSideStage pushRightSideStage = new PushRightSideStage();

		connectPorts(initStage.getOutputPort(), ifStage.getNewTaskInputPort());

		connectPorts(ifStage.getTrueOutputPort(), setupRangeStage.getInputPort());
		connectPorts(setupRangeStage.getOutputPort(), partitionStage.getInputPort());
		connectPorts(partitionStage.getOutputPort(), pushLeftSideStage.getInputPort());
		connectPorts(pushLeftSideStage.getOutputPort(), pushRightSideStage.getInputPort());
		connectPorts(pushRightSideStage.getOutputPort(), ifStage.getSubTaskInputPort());
		// -> feedback loop to the ifStage

		// map outer ports to inner ports
		inputPort = createInputPort(initStage.getInputPort());
		outputPort = createOutputPort(ifStage.getFalseOutputPort());
	}

	public InputPort<int[]> getInputPort() {
		return inputPort;
	}

	public OutputPort<int[]> getOutputPort() {
		return outputPort;
	}
}
