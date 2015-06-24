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
import teetime.framework.Stage;
import teetime.framework.exceptionHandling.TerminatingExceptionListenerFactory;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;
import teetime.util.framework.port.PortAction;

public class ControlledDistributorTest {

	@Test
	public void shouldWorkWithoutActionTriggers() throws Exception {
		PortAction<DynamicDistributor<Integer>> createAction = new DoNothingPortAction<Integer>();

		List<Integer> inputNumbers = Arrays.asList(0, 1, 2, 3, 4);
		@SuppressWarnings("unchecked")
		List<PortAction<DynamicDistributor<Integer>>> inputActions = Arrays.asList(createAction, createAction, createAction, createAction, createAction);

		ControlledDistributorTestConfig<Integer> config = new ControlledDistributorTestConfig<Integer>(inputNumbers, inputActions);
		Execution<ControlledDistributorTestConfig<Integer>> analysis = new Execution<ControlledDistributorTestConfig<Integer>>(config,
				new TerminatingExceptionListenerFactory());

		analysis.executeBlocking();

		assertThat(config.getOutputElements(), contains(0, 1, 2, 3, 4));
	}

	@Test
	public void shouldWorkWithCreateActionTriggers() throws Exception {
		List<Integer> inputNumbers = Arrays.asList(0, 1, 2, 3, 4);

		@SuppressWarnings("unchecked")
		PortAction<DynamicDistributor<Integer>>[] inputActions = new PortAction[5];
		for (int i = 0; i < inputActions.length; i++) {
			PortAction<DynamicDistributor<Integer>> createAction = createPortCreateAction();
			inputActions[i] = createAction;
		}

		ControlledDistributorTestConfig<Integer> config = new ControlledDistributorTestConfig<Integer>(inputNumbers, Arrays.asList(inputActions));
		Execution<ControlledDistributorTestConfig<Integer>> analysis = new Execution<ControlledDistributorTestConfig<Integer>>(config,
				new TerminatingExceptionListenerFactory());

		analysis.executeBlocking();

		assertThat(config.getOutputElements(), contains(0));
		assertValuesForIndex(inputActions, Arrays.asList(1), 0);
		assertValuesForIndex(inputActions, Arrays.asList(2), 1);
		assertValuesForIndex(inputActions, Arrays.asList(3), 2);
		assertValuesForIndex(inputActions, Arrays.asList(4), 3);
		assertValuesForIndex(inputActions, Collections.<Integer> emptyList(), 4);
	}

	@Test
	public void shouldWorkWithRemoveActionTriggers() throws Exception {
		List<Integer> inputNumbers = Arrays.asList(0, 1, 2, 3, 4, 5);

		@SuppressWarnings("unchecked")
		PortAction<DynamicDistributor<Integer>>[] inputActions = new PortAction[6];
		inputActions[0] = createPortCreateAction();
		inputActions[1] = new RemovePortAction<Integer>(null);
		inputActions[2] = createPortCreateAction();
		inputActions[3] = createPortCreateAction();
		inputActions[4] = new RemovePortAction<Integer>(null);
		inputActions[5] = new RemovePortAction<Integer>(null);

		ControlledDistributorTestConfig<Integer> config = new ControlledDistributorTestConfig<Integer>(inputNumbers, Arrays.asList(inputActions));
		Execution<ControlledDistributorTestConfig<Integer>> analysis = new Execution<ControlledDistributorTestConfig<Integer>>(config,
				new TerminatingExceptionListenerFactory());

		analysis.executeBlocking();

		assertThat(config.getOutputElements(), contains(0, 1, 2, 4, 5));
		assertValuesForIndex(inputActions, Collections.<Integer> emptyList(), 0);
		assertValuesForIndex(inputActions, Arrays.asList(3), 2);
		assertValuesForIndex(inputActions, Collections.<Integer> emptyList(), 3);
	}

	private PortAction<DynamicDistributor<Integer>> createPortCreateAction() {
		CollectorSink<Integer> newStage = new CollectorSink<Integer>();
		PortAction<DynamicDistributor<Integer>> portAction = new CreatePortAction<Integer>(newStage.getInputPort());
		return portAction;
	}

	private void assertValuesForIndex(final PortAction<DynamicDistributor<Integer>>[] inputActions,
			final List<Integer> values, final int index) {
		PortAction<DynamicDistributor<Integer>> ia = inputActions[index];
		Stage stage = ((CreatePortAction<Integer>) ia).getInputPort().getOwningStage();
		@SuppressWarnings("unchecked")
		CollectorSink<Integer> collectorSink = (CollectorSink<Integer>) stage;
		assertThat(collectorSink.getElements(), is(values));
	}

	private static class ControlledDistributorTestConfig<T> extends Configuration {

		private final CollectorSink<T> collectorSink;

		public ControlledDistributorTestConfig(final List<T> elements, final List<PortAction<DynamicDistributor<T>>> inputActions) {
			InitialElementProducer<T> initialElementProducer = new InitialElementProducer<T>(elements);
			DynamicDistributor<T> distributor = new ControlledDynamicDistributor<T>();
			collectorSink = new CollectorSink<T>();

			connectPorts(initialElementProducer.getOutputPort(), distributor.getInputPort());
			connectPorts(distributor.getNewOutputPort(), collectorSink.getInputPort());

			addThreadableStage(distributor);
			addThreadableStage(collectorSink);

			for (PortAction<DynamicDistributor<T>> a : inputActions) {
				distributor.addPortActionRequest(a);
			}
		}

		public List<T> getOutputElements() {
			return collectorSink.getElements();
		}
	}
}
