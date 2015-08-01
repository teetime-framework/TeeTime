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
package teetime.stage.basic.merger.dynamic;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import teetime.framework.Configuration;
import teetime.framework.RuntimeServiceFacade;
import teetime.framework.Execution;
import teetime.framework.exceptionHandling.TerminatingExceptionListenerFactory;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;
import teetime.stage.basic.merger.strategy.BusyWaitingRoundRobinStrategy;
import teetime.util.framework.port.PortAction;

@Ignore
public class DynamicMergerTest {

	@Test
	public void shouldWorkWithoutActionTriggers() throws Exception {
		List<Integer> inputNumbers = Arrays.asList(0, 1, 2, 3, 4, 5);

		@SuppressWarnings("unchecked")
		PortAction<DynamicMerger<Integer>>[] inputActions = new PortAction[6];
		for (int i = 0; i < inputActions.length; i++) {
			inputActions[i] = new DoNothingPortAction<Integer>();
		}

		DynamicMergerTestConfig<Integer> config = new DynamicMergerTestConfig<Integer>(inputNumbers, Arrays.asList(inputActions));
		Execution<DynamicMergerTestConfig<Integer>> analysis = new Execution<DynamicMergerTestConfig<Integer>>(config);

		analysis.executeBlocking();

		assertThat(config.getOutputElements(), contains(0, 1, 2, 3, 4, 5));
	}

	@Test
	public void shouldWorkWithCreateActionTriggers() throws Exception {
		List<Integer> inputNumbers = Arrays.asList(0);

		@SuppressWarnings("unchecked")
		PortAction<DynamicMerger<Integer>>[] inputActions = new PortAction[6];
		for (int i = 0; i < inputActions.length; i++) {
			inputActions[i] = createPortCreateAction(i + 1);
		}

		DynamicMergerTestConfig<Integer> config = new DynamicMergerTestConfig<Integer>(inputNumbers, Arrays.asList(inputActions));
		Execution<DynamicMergerTestConfig<Integer>> analysis = new Execution<DynamicMergerTestConfig<Integer>>(config);

		analysis.executeBlocking();

		assertThat(config.getOutputElements(), contains(0, 1, 2, 3, 4, 5, 6));
	}

	@Test
	public void shouldWorkWithRemoveActionTriggers() throws Exception {
		List<Integer> inputNumbers = Arrays.asList(0, 1, 2);

		@SuppressWarnings("unchecked")
		PortAction<DynamicMerger<Integer>>[] inputActions = new PortAction[6];
		inputActions[0] = createPortCreateAction(3);
		inputActions[1] = new RemovePortAction<Integer>(null);
		inputActions[2] = createPortCreateAction(4);
		inputActions[3] = createPortCreateAction(5);
		inputActions[4] = new RemovePortAction<Integer>(null);
		inputActions[5] = new RemovePortAction<Integer>(null);

		DynamicMergerTestConfig<Integer> config = new DynamicMergerTestConfig<Integer>(inputNumbers, Arrays.asList(inputActions));
		Execution<DynamicMergerTestConfig<Integer>> analysis = new Execution<DynamicMergerTestConfig<Integer>>(config);

		analysis.executeBlocking();

		assertThat(config.getOutputElements(), contains(0, 1, 2, 4, 5));
	}

	private PortAction<DynamicMerger<Integer>> createPortCreateAction(final Integer number) {
		final InitialElementProducer<Integer> initialElementProducer = new InitialElementProducer<Integer>(number);

		PortAction<DynamicMerger<Integer>> portAction = new CreatePortAction<Integer>(initialElementProducer.getOutputPort()) {
			@Override
			public void execute(final DynamicMerger<Integer> dynamicDistributor) {
				super.execute(dynamicDistributor);
				RuntimeServiceFacade.INSTANCE.startWithinNewThread(dynamicDistributor, initialElementProducer);
			}
		};
		return portAction;
	}

	private static class DynamicMergerTestConfig<T> extends Configuration {

		private final CollectorSink<T> collectorSink;

		public DynamicMergerTestConfig(final List<T> elements, final List<PortAction<DynamicMerger<T>>> inputActions) {
			super(new TerminatingExceptionListenerFactory());
			InitialElementProducer<T> initialElementProducer = new InitialElementProducer<T>(elements);
			DynamicMerger<T> merger = new DynamicMerger<T>(new BusyWaitingRoundRobinStrategy());
			collectorSink = new CollectorSink<T>();

			connectPorts(initialElementProducer.getOutputPort(), merger.getNewInputPort());
			connectPorts(merger.getOutputPort(), collectorSink.getInputPort());

			addThreadableStage(merger);

			for (PortAction<DynamicMerger<T>> a : inputActions) {
				boolean added = merger.addPortActionRequest(a);
				assertTrue(added);
			}
		}

		public List<T> getOutputElements() {
			return collectorSink.getElements();
		}
	}
}
