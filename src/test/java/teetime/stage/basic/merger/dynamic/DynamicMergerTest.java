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
package teetime.stage.basic.merger.dynamic;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import teetime.framework.*;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;
import teetime.stage.basic.merger.strategy.BusyWaitingRoundRobinStrategy;
import teetime.util.framework.port.PortAction;

public class DynamicMergerTest {

	@Test
	public void shouldWorkWithoutActionTriggers() throws Exception {
		List<Integer> inputNumbers = Arrays.asList(0, 1, 2, 3, 4, 5);

		DynamicMergerTestConfig config = new DynamicMergerTestConfig(inputNumbers);
		for (int i = 0; i < 6; i++) {
			assertTrue(config.addPortActionRequest(new DoNothingPortAction<Integer>()));
		}

		Execution<DynamicMergerTestConfig> analysis = new Execution<DynamicMergerTestConfig>(config);

		analysis.executeBlocking();

		assertThat(config.getOutputElements(), contains(0, 1, 2, 3, 4, 5));
	}

	@Test
	public void shouldWorkWithCreateActionTriggers() throws Exception {
		List<Integer> inputNumbers = Arrays.asList(0);

		DynamicMergerTestConfig config = new DynamicMergerTestConfig(inputNumbers);
		for (int i = 0; i < 6; i++) {
			assertTrue(config.addCreatePortAction(i + 1));
		}

		Execution<DynamicMergerTestConfig> analysis = new Execution<DynamicMergerTestConfig>(config);

		analysis.executeBlocking();

		assertThat(config.getOutputElements(), contains(0, 1, 2, 3, 4, 5, 6));
	}

	@Test
	@Ignore // we cannot ensure anymore to consume at least one element before executing a port action
	public void shouldWorkWithRemoveActionTriggers() {
		List<Integer> inputNumbers = Arrays.asList(0, 1, 2);

		DynamicMergerTestConfig config = new DynamicMergerTestConfig(inputNumbers);
		assertTrue(config.addCreatePortAction(3)); // processed after reading 0
		assertTrue(config.addRemovePortAction()); // processed after reading 1
		assertTrue(config.addCreatePortAction(4)); // processed after reading 2
		assertTrue(config.addCreatePortAction(5)); // processed after reading 4
		assertTrue(config.addRemovePortAction()); // processed after reading 5
		assertTrue(config.addRemovePortAction());

		Execution<DynamicMergerTestConfig> analysis = new Execution<DynamicMergerTestConfig>(config);

		analysis.executeBlocking();

		assertThat(config.getOutputElements(), contains(0, 1, 2, 4, 5));
	}

	private static class DynamicMergerTestConfig extends Configuration {

		private static final int DEFAULT_CAPACITY = 16;

		private final CollectorSink<Integer> collectorSink;
		private final DynamicMerger<Integer> merger;

		public DynamicMergerTestConfig(final List<Integer> inputNumbers) {
			InitialElementProducer<Integer> initialElementProducer = new InitialElementProducer<Integer>(inputNumbers);
			merger = new DynamicMerger<Integer>(new BusyWaitingRoundRobinStrategy());
			collectorSink = new CollectorSink<Integer>();

			connectPorts(initialElementProducer.getOutputPort(), merger.getNewInputPort());
			connectPorts(merger.getOutputPort(), collectorSink.getInputPort());

			merger.declareActive();
		}

		public boolean addPortActionRequest(final PortAction<DynamicMerger<Integer>> portAction) {
			return merger.addPortActionRequest(portAction);
		}

		public List<Integer> getOutputElements() {
			return collectorSink.getElements();
		}

		boolean addCreatePortAction(final Integer number) {
			final InitialElementProducer<Integer> initialElementProducer = new InitialElementProducer<Integer>(number);

			PortAction<DynamicMerger<Integer>> portAction = new CreatePortActionMerger<Integer>(initialElementProducer.getOutputPort(), DEFAULT_CAPACITY) {
				@Override
				public void execute(final DynamicMerger<Integer> dynamicMerger) {
					super.execute(dynamicMerger);
					RuntimeServiceFacade.INSTANCE.startWithinNewThread(merger, initialElementProducer);
				}
			};

			return addPortActionRequest(portAction);
		}

		boolean addRemovePortAction() {
			RemovePortAction<Integer> portAction = new RemovePortAction<Integer>(null);

			return addPortActionRequest(portAction);
		}
	}
}
