package teetime.stage.basic.merger.dynamic;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import teetime.framework.Configuration;
import teetime.framework.DynamicActuator;
import teetime.framework.Execution;
import teetime.framework.exceptionHandling.TerminatingExceptionListenerFactory;
import teetime.framework.signal.InitializingSignal;
import teetime.framework.signal.StartingSignal;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;
import teetime.util.framework.port.PortAction;

public class ControlledMergerTest {

	private static final DynamicActuator DYNAMIC_ACTUATOR = new DynamicActuator();

	@Test
	public void shouldWorkWithoutActionTriggers() throws Exception {
		List<Integer> inputNumbers = Arrays.asList(0, 1, 2, 3, 4);

		@SuppressWarnings("unchecked")
		PortAction<DynamicMerger<Integer>>[] inputActions = new PortAction[5];
		for (int i = 0; i < inputActions.length; i++) {
			inputActions[i] = new DoNothingPortAction<Integer>();
		}

		ControlledMergerTestConfig<Integer> config = new ControlledMergerTestConfig<Integer>(inputNumbers, Arrays.asList(inputActions));
		Execution<ControlledMergerTestConfig<Integer>> analysis = new Execution<ControlledMergerTestConfig<Integer>>(config,
				new TerminatingExceptionListenerFactory());

		analysis.executeBlocking();

		assertThat(config.getOutputElements(), contains(0, 1, 2, 3, 4));
	}

	@Test
	public void shouldWorkWithCreateActionTriggers() throws Exception {
		List<Integer> inputNumbers = Arrays.asList(0);

		@SuppressWarnings("unchecked")
		PortAction<DynamicMerger<Integer>>[] inputActions = new PortAction[5];
		for (int i = 0; i < inputActions.length; i++) {
			inputActions[i] = createPortCreateAction(i + 1);
		}

		ControlledMergerTestConfig<Integer> config = new ControlledMergerTestConfig<Integer>(inputNumbers, Arrays.asList(inputActions));
		Execution<ControlledMergerTestConfig<Integer>> analysis = new Execution<ControlledMergerTestConfig<Integer>>(config,
				new TerminatingExceptionListenerFactory());

		analysis.executeBlocking();

		assertThat(config.getOutputElements(), containsInAnyOrder(0, 1, 2, 3, 4, 5, 6));
	}

	@Test
	public void shouldWorkWithRemoveActionTriggers() throws Exception {
		List<Integer> inputNumbers = Arrays.asList(0, 1, 2, 3, 4, 5);

		@SuppressWarnings("unchecked")
		PortAction<DynamicMerger<Integer>>[] inputActions = new PortAction[6];
		// inputActions[0] = createPortCreateAction();
		// inputActions[1] = new RemovePortAction<Integer>(null);
		// inputActions[2] = createPortCreateAction();
		// inputActions[3] = createPortCreateAction();
		// inputActions[4] = new RemovePortAction<Integer>(null);
		// inputActions[5] = new RemovePortAction<Integer>(null);
		//
		// ControlledMergerTestConfig<Integer> config = new ControlledMergerTestConfig<Integer>(inputNumbers, Arrays.asList(inputActions));
		// Execution<ControlledMergerTestConfig<Integer>> analysis = new Execution<ControlledMergerTestConfig<Integer>>(config,
		// new TerminatingExceptionListenerFactory());
		//
		// analysis.executeBlocking();
		//
		// assertThat(config.getOutputElements(), contains(0, 1, 2, 4, 5));
		// assertValuesForIndex(inputActions, Collections.<Integer> emptyList(), 0);
		// assertValuesForIndex(inputActions, Arrays.asList(3), 2);
		// assertValuesForIndex(inputActions, Collections.<Integer> emptyList(), 3);
	}

	private PortAction<DynamicMerger<Integer>> createPortCreateAction(final Integer number) {
		final InitialElementProducer<Integer> initialElementProducer = new InitialElementProducer<Integer>(number);
		DYNAMIC_ACTUATOR.startWithinNewThread(initialElementProducer);

		PortAction<DynamicMerger<Integer>> portAction = new CreatePortAction<Integer>(initialElementProducer.getOutputPort()) {
			@Override
			public void execute(final DynamicMerger<Integer> dynamicDistributor) {
				super.execute(dynamicDistributor);
				initialElementProducer.getOutputPort().sendSignal(new InitializingSignal());
				initialElementProducer.getOutputPort().sendSignal(new StartingSignal());
			}
		};
		return portAction;
	}

	private static class ControlledMergerTestConfig<T> extends Configuration {

		private final CollectorSink<T> collectorSink;

		public ControlledMergerTestConfig(final List<T> elements, final List<PortAction<DynamicMerger<T>>> inputActions) {
			InitialElementProducer<T> initialElementProducer = new InitialElementProducer<T>(elements);
			DynamicMerger<T> merger = new ControlledDynamicMerger<T>();
			collectorSink = new CollectorSink<T>();

			connectPorts(initialElementProducer.getOutputPort(), merger.getNewInputPort());
			connectPorts(merger.getOutputPort(), collectorSink.getInputPort());

			addThreadableStage(merger);

			for (PortAction<DynamicMerger<T>> a : inputActions) {
				merger.addPortActionRequest(a);
			}
		}

		public List<T> getOutputElements() {
			return collectorSink.getElements();
		}
	}
}
