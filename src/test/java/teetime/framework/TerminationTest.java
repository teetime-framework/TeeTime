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
package teetime.framework;

import org.junit.Test;

import teetime.stage.InitialElementProducer;
import teetime.stage.basic.Sink;

public class TerminationTest {

	@Test(timeout = 1000)
	public void correctAbort() {
		TerminationConfig configuration = new TerminationConfig(10);
		Execution<TerminationConfig> execution = new Execution<TerminationConfig>(configuration);
		execution.executeBlocking();
	}

	@Test(timeout = 1000, expected = ExecutionException.class)
	public void doesNotGetStuckInAdd() {
		TerminationConfig configuration = new TerminationConfig(1);
		Execution<TerminationConfig> execution = new Execution<TerminationConfig>(configuration);
		execution.executeBlocking();
	}

	private class TerminationConfig extends Configuration {
		final InitialElementProducer<Integer> init = new InitialElementProducer<Integer>(1, 2, 3, 4, 5);
		final Propagator firstProp = new Propagator();
		final DoesNotRetrieveElements absorbStage = new DoesNotRetrieveElements();
		final Propagator finalProp = new Propagator();

		public TerminationConfig(final int capacity) {
			if (capacity == 1) {
				connectPorts(init.getOutputPort(), firstProp.getInputPort());
				connectPorts(firstProp.getOutputPort(), absorbStage.getInputPort(), capacity);
				connectPorts(absorbStage.getOutputPort(), finalProp.getInputPort());
				absorbStage.declareActive();
			} else {
				Sink<Integer> sink = new Sink<Integer>();
				connectPorts(init.getOutputPort(), sink.getInputPort(), capacity);
				sink.declareActive();
			}
		}

	}

	private class DoesNotRetrieveElements extends AbstractStage {

		private final InputPort<Integer> inputPort = createInputPort();
		private final OutputPort<Integer> outputPort = createOutputPort();

		@Override
		protected void execute() throws InterruptedException {
			throw new IllegalStateException();
		}

		public InputPort<Integer> getInputPort() {
			return inputPort;
		}

		public OutputPort<? extends Integer> getOutputPort() {
			return outputPort;
		}
	}

	private class Propagator extends AbstractConsumerStage<Integer> {

		private final OutputPort<Integer> output = createOutputPort();

		@Override
		protected void execute(final Integer element) {
			output.send(element);
		}

		public OutputPort<? extends Integer> getOutputPort() {
			return output;
		}
	}

}
