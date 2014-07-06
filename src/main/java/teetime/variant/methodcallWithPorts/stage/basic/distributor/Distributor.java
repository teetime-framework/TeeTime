/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
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
 ***************************************************************************/

package teetime.variant.methodcallWithPorts.stage.basic.distributor;

import java.util.ArrayList;
import java.util.List;

import teetime.variant.methodcallWithPorts.framework.core.AbstractStage;
import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;
import teetime.variant.methodcallWithPorts.framework.core.Signal;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 * 
 * @param T
 *            the type of the input port and the output ports
 */
public class Distributor<T> extends AbstractStage<T, T> {

	// TODO do not inherit from AbstractStage since it provides the default output port that is unnecessary for the distributor ConsumerStage<T, T> {

	// BETTER use an array since a list always creates a new iterator when looping
	private final List<OutputPort<T>> outputPortList = new ArrayList<OutputPort<T>>();

	private IDistributorStrategy<T> strategy = new RoundRobinStrategy<T>();

	@Override
	public void executeWithPorts() {
		T element = this.getInputPort().receive();

		this.setReschedulable(this.getInputPort().getPipe().size() > 0);

		this.execute5(element);
	}

	@Override
	protected void execute5(final T element) {
		this.strategy.distribute(this.outputPortList, element);
	}

	@Override
	public void onIsPipelineHead() {
		// for (OutputPort<T> op : this.outputPortList) {
		// op.getPipe().close();
		// System.out.println("End signal sent, size: " + op.getPipe().size());
		// }
	}

	@Override
	public void onSignal(final Signal signal, final InputPort<?> inputPort) {
		this.logger.info("Got signal: " + signal + " from input port: " + inputPort);

		switch (signal) {
		case FINISHED:
			this.onFinished();
			break;
		default:
			this.logger.warn("Aborted sending signal " + signal + ". Reason: Unknown signal.");
			break;
		}

		for (OutputPort<T> op : this.outputPortList) {
			op.sendSignal(signal);
		}
	}

	@Override
	public OutputPort<T> getOutputPort() {
		return this.getNewOutputPort();
	}

	public OutputPort<T> getNewOutputPort() {
		final OutputPort<T> newOutputPort = new OutputPort<T>();
		this.outputPortList.add(newOutputPort);
		return newOutputPort;
	}

	public List<OutputPort<T>> getOutputPortList() {
		return this.outputPortList;
	}

	public IDistributorStrategy<T> getStrategy() {
		return this.strategy;
	}

	public void setStrategy(final IDistributorStrategy<T> strategy) {
		this.strategy = strategy;
	}

}
