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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import teetime.framework.Configuration;
import teetime.framework.Execution;
import teetime.framework.OutputPort;
import teetime.framework.AbstractStage;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;
import teetime.util.framework.port.PortAction;

public class DynamicDistributorTest {

	@Test
	public void shouldWorkWithoutActionTriggers() throws Exception {
		PortAction<DynamicDistributor<Integer>> createAction = new DoNothingPortAction<Integer>();

		List<Integer> inputNumbers = Arrays.asList(0, 1, 2, 3, 4);
		@SuppressWarnings("unchecked")
		List<PortAction<DynamicDistributor<Integer>>> inputActions = Arrays.asList(createAction, createAction, createAction, createAction, createAction);

		DynamicDistributorTestConfig<Integer> config = new DynamicDistributorTestConfig<Integer>(inputNumbers, inputActions);
		Execution<DynamicDistributorTestConfig<Integer>> analysis = new Execution<DynamicDistributorTestConfig<Integer>>(config);

		analysis.executeBlocking();

		assertThat(config.getOutputElements(), contains(0, 1, 2, 3, 4));
	}

	@Test
	public void shouldWorkWithCreateActionTriggers() throws Exception {
		List<Integer> inputNumbers = Arrays.asList(0, 1, 2, 3, 4);

		@SuppressWarnings("unchecked")
		PortAction<DynamicDistributor<Integer>>[] inputActions = new PortAction[5];
		for (int i = 0; i < inputActions.length; i++) {
			PortAction<DynamicDistributor<Integer>> createAction = createPortCreateAction(new PortContainer<Integer>());
			inputActions[i] = createAction;
		}

		DynamicDistributorTestConfig<Integer> config = new DynamicDistributorTestConfig<Integer>(inputNumbers, Arrays.asList(inputActions));
		Execution<DynamicDistributorTestConfig<Integer>> analysis = new Execution<DynamicDistributorTestConfig<Integer>>(config);

		analysis.executeBlocking();

		assertThat(config.getOutputElements(), contains(0));
		assertValuesForIndex(inputActions[0], Arrays.asList(1));
		assertValuesForIndex(inputActions[1], Arrays.asList(2));
		assertValuesForIndex(inputActions[2], Arrays.asList(3));
		assertValuesForIndex(inputActions[3], Arrays.asList(4));
		assertValuesForIndex(inputActions[4], Collections.<Integer> emptyList());
	}

	@Test
	public void shouldWorkWithRemoveActionTriggers() throws Exception {
		List<Integer> inputNumbers = Arrays.asList(0, 1, 2, 3, 4, 5);

		@SuppressWarnings("unchecked")
		PortAction<DynamicDistributor<Integer>>[] inputActions = new PortAction[6];

		final PortContainer<Integer> portContainer0 = new PortContainer<Integer>();
		final PortContainer<Integer> portContainer1 = new PortContainer<Integer>();
		final PortContainer<Integer> portContainer2 = new PortContainer<Integer>();

		inputActions[0] = createPortCreateAction(portContainer0);
		inputActions[1] = new RemovePortActionDelegation<Integer>(portContainer0);
		inputActions[2] = createPortCreateAction(portContainer1);
		inputActions[3] = createPortCreateAction(portContainer2);
		inputActions[4] = new RemovePortActionDelegation<Integer>(portContainer1);
		inputActions[5] = new RemovePortActionDelegation<Integer>(portContainer2);

		DynamicDistributorTestConfig<Integer> config = new DynamicDistributorTestConfig<Integer>(inputNumbers, Arrays.asList(inputActions));
		Execution<DynamicDistributorTestConfig<Integer>> analysis = new Execution<DynamicDistributorTestConfig<Integer>>(config);

		analysis.executeBlocking();

		assertThat(config.getOutputElements(), contains(0, 1, 2, 4, 5));
		assertValuesForIndex(inputActions[0], Collections.<Integer> emptyList());
		assertValuesForIndex(inputActions[2], Arrays.asList(3));
		assertValuesForIndex(inputActions[3], Collections.<Integer> emptyList());
	}

	private CreatePortAction<Integer> createPortCreateAction(final PortContainer<Integer> portContainer) {
		CollectorSink<Integer> newStage = new CollectorSink<Integer>();
		CreatePortAction<Integer> portAction = new CreatePortAction<Integer>(newStage.getInputPort());
		portAction.addPortActionListener(new PortActionListener<Integer>() {
			@Override
			public void onOutputPortCreated(final DynamicDistributor<Integer> distributor, final OutputPort<Integer> port) {
				portContainer.setPort(port);
			}
		});
		return portAction;
	}

	private void assertValuesForIndex(final PortAction<DynamicDistributor<Integer>> ia, final List<Integer> values) {
		AbstractStage stage = ((CreatePortAction<Integer>) ia).getInputPort().getOwningStage();

		@SuppressWarnings("unchecked")
		CollectorSink<Integer> collectorSink = (CollectorSink<Integer>) stage;

		assertThat(collectorSink.getElements(), is(values));
	}

	private static class DynamicDistributorTestConfig<T> extends Configuration {

		private final CollectorSink<T> collectorSink;

		public DynamicDistributorTestConfig(final List<T> elements, final List<PortAction<DynamicDistributor<T>>> inputActions) {
			InitialElementProducer<T> initialElementProducer = new InitialElementProducer<T>(elements);
			DynamicDistributor<T> distributor = new DynamicDistributor<T>();
			collectorSink = new CollectorSink<T>();

			connectPorts(initialElementProducer.getOutputPort(), distributor.getInputPort());
			connectPorts(distributor.getNewOutputPort(), collectorSink.getInputPort());

			distributor.declareActive();
			collectorSink.declareActive();

			for (PortAction<DynamicDistributor<T>> a : inputActions) {
				distributor.addPortActionRequest(a);
			}
		}

		public List<T> getOutputElements() {
			return collectorSink.getElements();
		}
	}
}
