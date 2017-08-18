package teetime.example;

import java.util.List;

import teetime.framework.Configuration;
import teetime.stage.CollectorSink;
import teetime.stage.Counter;
import teetime.stage.InitialElementProducer;
import teetime.stage.Ramp;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.distributor.strategy.BlockingBusyWaitingRoundRobinStrategy;
import teetime.stage.basic.merger.Merger;
import teetime.stage.basic.merger.strategy.BusyWaitingRoundRobinStrategy;

public class ParallelCounterConfig extends Configuration {

	public ParallelCounterConfig(final int numElements, final int numParallelPipelines, final List<Integer> outputElements) {
		InitialElementProducer<Integer> init = new InitialElementProducer<>(numElements);
		Ramp ramp = new Ramp();
		Distributor<Integer> distributor = new Distributor<>(new BlockingBusyWaitingRoundRobinStrategy());
		// pipelines in between
		Merger<Integer> merger = new Merger<>(new BusyWaitingRoundRobinStrategy());
		CollectorSink<Integer> sink = new CollectorSink<>(outputElements);

		from(init).to(ramp).end(distributor);

		for (int i = 0; i < numParallelPipelines; i++) {
			Counter<Integer> firstCounter = new Counter<>();
			Counter<Integer> secondCounter = new Counter<>();
			Counter<Integer> thirdCounter = new Counter<>();

			connectPorts(distributor.getNewOutputPort(), firstCounter.getInputPort());
			connectPorts(firstCounter.getOutputPort(), secondCounter.getInputPort());
			connectPorts(secondCounter.getOutputPort(), thirdCounter.getInputPort());
			connectPorts(thirdCounter.getOutputPort(), merger.getNewInputPort());
		}

		connectPorts(merger.getOutputPort(), sink.getInputPort());
	}
}
