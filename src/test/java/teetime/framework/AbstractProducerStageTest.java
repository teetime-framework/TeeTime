package teetime.framework;

import org.junit.Test;

import teetime.stage.basic.Sink;
import teetime.stage.basic.merger.Merger;

public class AbstractProducerStageTest {

	private static class FiniteProducer extends AbstractProducerStage<Object> {
		@Override
		protected void execute() {
			outputPort.send(Boolean.TRUE);
			terminateStage();
		}

		@Override
		public TerminationStrategy getTerminationStrategy() {
			return TerminationStrategy.BY_SELF_DECISION;
		}
	}

	private static class InfiniteProducer extends AbstractProducerStage<Object> {
		@Override
		protected void execute() {
			outputPort.send(Boolean.FALSE);
		}

		@Override
		public TerminationStrategy getTerminationStrategy() {
			return TerminationStrategy.BY_INTERRUPT;
		}
	}

	private static class MixedProducerConfig extends Configuration {
		public MixedProducerConfig() {
			FiniteProducer finiteProducer = new FiniteProducer();
			InfiniteProducer infiniteProducer = new InfiniteProducer();
			Merger<Object> merger = new Merger<>();

			connectPorts(finiteProducer.getOutputPort(), merger.createInputPort());
			connectPorts(infiniteProducer.getOutputPort(), merger.createInputPort());

			merger.declareActive();
		}
	}

	private static class TwoIndependentPipelinesConfig extends Configuration {
		public TwoIndependentPipelinesConfig() {
			FiniteProducer finiteProducer = new FiniteProducer();
			InfiniteProducer infiniteProducer = new InfiniteProducer();
			Sink<Object> sink0 = new Sink<>();
			Sink<Object> sink1 = new Sink<>();

			connectPorts(finiteProducer.getOutputPort(), sink0.getInputPort());
			connectPorts(infiniteProducer.getOutputPort(), sink1.getInputPort());
		}
	}

	/*
	 * Use a t/o since the execution may not block infinitely;
	 * expected execution time is 500 ms, so the t/o should be sufficiently high
	 */
	@Test(timeout = 1000)
	public void shouldTerminateFiniteAndInfiniteProducer() {
		MixedProducerConfig config = new MixedProducerConfig();
		new Execution<>(config).executeBlocking();
	}

	/*
	 * Use a t/o since the execution may not block infinitely;
	 * expected execution time is 500 ms, so the t/o should be sufficiently high
	 */
	@Test(timeout = 1000)
	public void shouldTerminateTwoIndependentPipelines() {
		TwoIndependentPipelinesConfig config = new TwoIndependentPipelinesConfig();
		new Execution<>(config).executeBlocking();
	}
}
