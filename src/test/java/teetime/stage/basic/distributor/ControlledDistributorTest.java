package teetime.stage.basic.distributor;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import teetime.framework.Analysis;
import teetime.framework.AnalysisConfiguration;
import teetime.framework.Stage;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;
import teetime.stage.basic.distributor.dynamic.ControlledDynamicDistributor;
import teetime.stage.basic.distributor.dynamic.CreatePortAction;
import teetime.stage.basic.distributor.dynamic.DoNothingPortAction;
import teetime.stage.basic.distributor.dynamic.DynamicDistributor;
import teetime.stage.basic.distributor.dynamic.PortAction;

public class ControlledDistributorTest {

	// private ControlledDistributor<Integer> controlledDistributor;

	@Before
	public void setUp() throws Exception {
		// controlledDistributor = new ControlledDistributor<Integer>();
	}

	@Test
	public void shouldWorkWithoutActionTriggers() throws Exception {
		PortAction<Integer> createAction = new DoNothingPortAction<Integer>();

		List<Integer> inputNumbers = Arrays.asList(0, 1, 2, 3, 4);
		@SuppressWarnings("unchecked")
		List<PortAction<Integer>> inputActions = Arrays.asList(createAction, createAction, createAction, createAction, createAction);

		ControlledDistributorTestConfig<Integer> config = new ControlledDistributorTestConfig<Integer>(inputNumbers, inputActions);
		Analysis<ControlledDistributorTestConfig<Integer>> analysis = new Analysis<ControlledDistributorTestConfig<Integer>>(config);

		analysis.executeBlocking();

		assertThat(config.getOutputElements(), contains(0, 1, 2, 3, 4));
	}

	@Test
	public void shouldWorkWithActionTriggers() throws Exception {
		List<Integer> inputNumbers = Arrays.asList(0, 1, 2, 3, 4);

		@SuppressWarnings("unchecked")
		PortAction<Integer>[] inputActions = new PortAction[5];
		for (int i = 0; i < inputActions.length; i++) {
			CollectorSink<Integer> newStage = new CollectorSink<Integer>();

			// Runnable runnable = dynamicActuator.wrap(newStage);
			// Thread thread = new Thread(runnable);
			// thread.start();

			PortAction<Integer> createAction = new CreatePortAction<Integer>(newStage.getInputPort());
			inputActions[i] = createAction;
		}

		ControlledDistributorTestConfig<Integer> config = new ControlledDistributorTestConfig<Integer>(inputNumbers, Arrays.asList(inputActions));
		Analysis<ControlledDistributorTestConfig<Integer>> analysis = new Analysis<ControlledDistributorTestConfig<Integer>>(config);

		analysis.executeBlocking();

		for (PortAction<Integer> ia : inputActions) {
			Stage stage = ((CreatePortAction<Integer>) ia).getInputPort().getOwningStage();
			@SuppressWarnings("unchecked")
			CollectorSink<Integer> collectorSink = (CollectorSink<Integer>) stage;
			System.out.println("collectorSink: " + collectorSink.getElements());
		}

		assertThat(config.getOutputElements(), contains(0));
		assertValuesForIndex(inputActions, Arrays.asList(1), 0);
		assertValuesForIndex(inputActions, Arrays.asList(2), 1);
		assertValuesForIndex(inputActions, Arrays.asList(3), 2);
		assertValuesForIndex(inputActions, Arrays.asList(4), 3);
		assertValuesForIndex(inputActions, Collections.<Integer> emptyList(), 4);
	}

	private void assertValuesForIndex(final PortAction<Integer>[] inputActions,
			final List<Integer> values, final int index) {
		PortAction<Integer> ia = inputActions[index];
		Stage stage = ((CreatePortAction<Integer>) ia).getInputPort().getOwningStage();
		@SuppressWarnings("unchecked")
		CollectorSink<Integer> collectorSink = (CollectorSink<Integer>) stage;
		assertThat(collectorSink.getElements(), is(values));
	}

	private static class ControlledDistributorTestConfig<T> extends AnalysisConfiguration {

		private final CollectorSink<T> collectorSink;

		public ControlledDistributorTestConfig(final List<T> elements, final List<PortAction<T>> portActions) {
			InitialElementProducer<T> initialElementProducer = new InitialElementProducer<T>(elements);
			// InitialElementProducer<PortAction<T>> initialActionProducer = new InitialElementProducer<PortAction<T>>(actions);
			DynamicDistributor<T> distributor = new ControlledDynamicDistributor<T>();
			collectorSink = new CollectorSink<T>();

			connectPorts(initialElementProducer.getOutputPort(), distributor.getInputPort());
			// connectPorts(initialActionProducer.getOutputPort(), controlledDistributor.getDynamicPortActionInputPort());
			connectPorts(distributor.getNewOutputPort(), collectorSink.getInputPort());

			addThreadableStage(initialElementProducer);
			// addThreadableStage(initialActionProducer); // simulates the AdaptationThread
			addThreadableStage(distributor);
			addThreadableStage(collectorSink);

			for (PortAction<T> a : portActions) {
				distributor.addPortActionRequest(a);
			}
		}

		public List<T> getOutputElements() {
			return collectorSink.getElements();
		}
	}
}
