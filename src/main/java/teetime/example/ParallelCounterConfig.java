package teetime.example;

import java.util.List;

import teetime.framework.Configuration;
import teetime.stage.CollectorSink;
import teetime.stage.Counter;
import teetime.stage.InitialElementProducer;
import teetime.stage.Ramp;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.merger.Merger;

public class ParallelCounterConfig extends Configuration {

	public ParallelCounterConfig(final int numParallelPipelines, final List<Long> outputElements) {
		InitialElementProducer<Long> init = new InitialElementProducer<>(512L);
		Ramp ramp = new Ramp();
		Distributor<Long> distributor = new Distributor<>();
		// pipelines in between
		Merger<Long> merger = new Merger<>();
		CollectorSink<Long> sink = new CollectorSink<>(outputElements);

		from(init).to(ramp).end(distributor);

		for (int i = 0; i < numParallelPipelines; i++) {
			Counter<Long> firstCounter = new Counter<>();
			Counter<Long> secondCounter = new Counter<>();
			Counter<Long> thirdCounter = new Counter<>();

			connectPorts(distributor.getNewOutputPort(), firstCounter.getInputPort());
			connectPorts(firstCounter.getOutputPort(), secondCounter.getInputPort());
			connectPorts(secondCounter.getOutputPort(), thirdCounter.getInputPort());
			connectPorts(thirdCounter.getOutputPort(), merger.getNewInputPort());
		}

		connectPorts(merger.getOutputPort(), sink.getInputPort());
	}
}
