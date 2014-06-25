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

package teetime.variant.methodcallWithPorts.stage.basic.merger;

import java.util.ArrayList;
import java.util.List;

import teetime.variant.explicitScheduling.framework.core.Description;
import teetime.variant.methodcallWithPorts.framework.core.ConsumerStage;
import teetime.variant.methodcallWithPorts.framework.core.InputPort;

/**
 * 
 * @author Christian Wulf
 * 
 * @since 1.10
 * 
 * @param <T>
 *            the type of the input ports and the output port
 */
@Description("This stage merges data from the input ports, by taking elements according to the chosen merge strategy and by putting them to the output port.")
public class Merger<T> extends ConsumerStage<T, T> {

	// TODO do not inherit from AbstractStage since it provides the default input port that is unnecessary for the merger

	private final List<InputPort<T>> inputPortList = new ArrayList<InputPort<T>>();

	private IMergerStrategy<T> strategy = new RoundRobinStrategy<T>();

	public IMergerStrategy<T> getStrategy() {
		return this.strategy;
	}

	public void setStrategy(final IMergerStrategy<T> strategy) {
		this.strategy = strategy;
	}

	@Override
	protected void execute5(final T element) {
		final T token = this.strategy.getNextInput(this);
		if (token == null) {
			return;
		}

		this.send(token);
	}

	@Override
	public InputPort<T> getInputPort() {
		return this.getNewInputPort();
	}

	private InputPort<T> getNewInputPort() {
		InputPort<T> inputPort = new InputPort<T>();
		this.inputPortList.add(inputPort);
		return inputPort;
	}

	public List<InputPort<T>> getInputPortList() {
		return this.inputPortList;
	}

}
