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

import teetime.framework.AbstractStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.signal.TerminatingSignal;

class IfStage extends AbstractStage { // NOPMD

	private final InputPort<QuicksortTaskContext> newTaskInputPort = createInputPort(QuicksortTaskContext.class);
	private final InputPort<QuicksortTaskContext> subTaskInputPort = createInputPort(QuicksortTaskContext.class);

	private final OutputPort<QuicksortTaskContext> trueOutputPort = createOutputPort(QuicksortTaskContext.class);
	private final OutputPort<int[]> falseOutputPort = createOutputPort(int[].class);

	// private final Set<QuicksortTaskContext> currentTasks = new HashSet<>();
	private int numCurrentTasks;

	@Override
	protected void execute() {
		QuicksortTaskContext subTask = subTaskInputPort.receive();
		if (subTask != null) {
			send(subTask);
			return;
		}

		QuicksortTaskContext newTask = newTaskInputPort.receive();
		if (newTask != null) {
			// currentTasks.add(newTask);
			numCurrentTasks++;
			send(newTask);
			return;
		}
	}

	private void send(final QuicksortTaskContext context) {
		if (context.getTop() >= 0) {
			trueOutputPort.send(context);
		} else {
			falseOutputPort.send(context.getElements());

			// currentTasks.remove(context);
			numCurrentTasks--;

			// if (newTaskInputPort.isClosed() && currentTasks.isEmpty()) {
			if (newTaskInputPort.isClosed() && numCurrentTasks == 0) {
				trueOutputPort.sendSignal(new TerminatingSignal());
			}
		}
	}

	public InputPort<QuicksortTaskContext> getNewTaskInputPort() {
		return newTaskInputPort;
	}

	public InputPort<QuicksortTaskContext> getSubTaskInputPort() {
		return subTaskInputPort;
	}

	public OutputPort<QuicksortTaskContext> getTrueOutputPort() {
		return trueOutputPort;
	}

	public OutputPort<int[]> getFalseOutputPort() {
		return falseOutputPort;
	}

}
