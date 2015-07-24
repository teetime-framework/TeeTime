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
package teetime.stage.basic.distributor.dynamic;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.DynamicActuator;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.SpScPipeFactory;
import teetime.framework.signal.InitializingSignal;
import teetime.framework.signal.StartingSignal;
import teetime.util.framework.port.PortAction;
import teetime.util.stage.OneTimeCondition;

public class CreatePortActionDistributor<T> implements PortAction<DynamicDistributor<T>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CreatePortActionDistributor.class);

	private static final SpScPipeFactory INTER_THREAD_PIPE_FACTORY = new SpScPipeFactory();
	private static final DynamicActuator DYNAMIC_ACTUATOR = new DynamicActuator();

	private final List<PortActionListener<T>> listeners = new ArrayList<PortActionListener<T>>();
	private final OneTimeCondition condition = new OneTimeCondition();

	private final InputPort<T> inputPort;

	public CreatePortActionDistributor(final InputPort<T> inputPort) {
		this.inputPort = inputPort;
	}

	@Override
	public void execute(final DynamicDistributor<T> dynamicDistributor) {
		LOGGER.debug("execute");
		OutputPort<T> newOutputPort = dynamicDistributor.getNewOutputPort();
		processOutputPort(newOutputPort);
		onOutputPortCreated(dynamicDistributor, newOutputPort);
		condition.signalAll();
	}

	private void processOutputPort(final OutputPort<T> newOutputPort) {
		INTER_THREAD_PIPE_FACTORY.create(newOutputPort, inputPort);

		DYNAMIC_ACTUATOR.startWithinNewThread(inputPort.getOwningStage());

		newOutputPort.sendSignal(new InitializingSignal());
		newOutputPort.sendSignal(new StartingSignal());

		// FIXME pass the new thread to the analysis so that it can terminate the thread at the end
	}

	private void onOutputPortCreated(final DynamicDistributor<T> dynamicDistributor, final OutputPort<T> newOutputPort) {
		for (PortActionListener<T> listener : listeners) {
			listener.onOutputPortCreated(dynamicDistributor, newOutputPort);
		}
	}

	InputPort<T> getInputPort() { // for testing purposes only
		return inputPort;
	}

	public void addPortActionListener(final PortActionListener<T> listener) {
		listeners.add(listener);
	}

	public void waitForCompletion() throws InterruptedException {
		condition.await();
	}
}
