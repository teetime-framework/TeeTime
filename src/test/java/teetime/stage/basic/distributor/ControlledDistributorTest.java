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
import teetime.framework.DynamicActuator;
import teetime.framework.Stage;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;
import teetime.stage.basic.distributor.DynamicPortActionContainer.DynamicPortAction;

public class ControlledDistributorTest {

	// private ControlledDistributor<Integer> controlledDistributor;
	private final DynamicActuator dynamicActuator = new DynamicActuator();

	@Before
	public void setUp() throws Exception {
		// controlledDistributor = new ControlledDistributor<Integer>();
	}

	@Test
	public void shouldWorkWithoutActionTriggers() throws Exception {
		DynamicPortActionContainer<Integer> createAction = new DynamicPortActionContainer<Integer>(
				DynamicPortAction.REMOVE, null);

		List<Integer> inputNumbers = Arrays.asList(0, 1, 2, 3, 4);
		@SuppressWarnings("unchecked")
		List<DynamicPortActionContainer<Integer>> inputActions = Arrays.asList(createAction, createAction, createAction, createAction, createAction);

		ControlledDistributorTestConfig<Integer> config = new ControlledDistributorTestConfig<Integer>(inputNumbers, inputActions);
		Analysis<ControlledDistributorTestConfig<Integer>> analysis = new Analysis<ControlledDistributorTestConfig<Integer>>(config);

		analysis.executeBlocking();

		assertThat(config.getOutputElements(), contains(0, 1, 2, 3, 4));
	}

	@Test
	public void shouldWorkWithActionTriggers() throws Exception {
		List<Integer> inputNumbers = Arrays.asList(0, 1, 2, 3, 4);

		@SuppressWarnings("unchecked")
		DynamicPortActionContainer<Integer>[] inputActions = new DynamicPortActionContainer[5];
		for (int i = 0; i < inputActions.length; i++) {
			CollectorSink<Integer> newStage = new CollectorSink<Integer>();

			// Runnable runnable = dynamicActuator.wrap(newStage);
			// Thread thread = new Thread(runnable);
			// thread.start();

			DynamicPortActionContainer<Integer> createAction = new DynamicPortActionContainer<Integer>(
					DynamicPortAction.CREATE, newStage.getInputPort());
			inputActions[i] = createAction;
		}

		ControlledDistributorTestConfig<Integer> config = new ControlledDistributorTestConfig<Integer>(inputNumbers, Arrays.asList(inputActions));
		Analysis<ControlledDistributorTestConfig<Integer>> analysis = new Analysis<ControlledDistributorTestConfig<Integer>>(config);

		analysis.executeBlocking();

		for (DynamicPortActionContainer<Integer> ia : inputActions) {
			Stage stage = ia.getInputPort().getOwningStage();
			@SuppressWarnings("unchecked")
			CollectorSink<Integer> collectorSink = (CollectorSink<Integer>) stage;
			System.out.println("collectorSink: " + collectorSink.getElements());
		}

		assertThat(config.getOutputElements(), contains(0, 1));
		assertValuesForIndex(inputActions, Arrays.asList(2), 0);
		assertValuesForIndex(inputActions, Arrays.asList(3), 1);
		assertValuesForIndex(inputActions, Arrays.asList(4), 2);
		assertValuesForIndex(inputActions, Collections.<Integer> emptyList(), 3);
		assertValuesForIndex(inputActions, Collections.<Integer> emptyList(), 4);
	}

	private void assertValuesForIndex(final DynamicPortActionContainer<Integer>[] inputActions,
			final List<Integer> values, final int index) {
		DynamicPortActionContainer<Integer> ia = inputActions[index];
		Stage stage = ia.getInputPort().getOwningStage();
		@SuppressWarnings("unchecked")
		CollectorSink<Integer> collectorSink = (CollectorSink<Integer>) stage;
		assertThat(collectorSink.getElements(), is(values));
	}

	private static class ControlledDistributorTestConfig<T> extends AnalysisConfiguration {

		private final CollectorSink<T> collectorSink;

		public ControlledDistributorTestConfig(final List<T> elements, final List<DynamicPortActionContainer<T>> actions) {
			InitialElementProducer<T> initialElementProducer = new InitialElementProducer<T>(elements);
			// InitialElementProducer<DynamicPortActionContainer<T>> initialActionProducer = new InitialElementProducer<DynamicPortActionContainer<T>>(actions);
			ControlledDistributor<T> controlledDistributor = new ControlledDistributor<T>();
			Distributor<T> distributor = new Distributor<T>();
			collectorSink = new CollectorSink<T>();

			connectPorts(initialElementProducer.getOutputPort(), controlledDistributor.getInputPort());
			// connectPorts(initialActionProducer.getOutputPort(), controlledDistributor.getDynamicPortActionInputPort());
			connectPorts(controlledDistributor.getOutputPort(), distributor.getInputPort());
			connectPorts(distributor.getNewOutputPort(), collectorSink.getInputPort());

			addThreadableStage(initialElementProducer);
			// addThreadableStage(initialActionProducer); // simulates the AdaptationThread
			addThreadableStage(controlledDistributor);
			addThreadableStage(collectorSink);

			controlledDistributor.getActions().addAll(actions);
		}

		public List<T> getOutputElements() {
			return collectorSink.getElements();
		}
	}
}
