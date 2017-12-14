package teetime.framework.scheduling.globaltaskpool.experimental;

import java.util.List;

import teetime.framework.Configuration;
import teetime.stage.CollectorSink;
import teetime.stage.ObjectProducer;
import teetime.util.ConstructorClosure;

public class ProducerConsumerGlobalTaskPoolConfig extends Configuration {

	public ProducerConsumerGlobalTaskPoolConfig(final int numInputObjects, final List<Integer> outputElements) {
		ObjectProducer<Integer> producer = new ObjectProducer<>(numInputObjects, new ConstructorClosure<Integer>() {
			private int counter;

			@Override
			public Integer create() {
				return counter++;
			}
		});
		CollectorSink<Integer> sink = new CollectorSink<>(outputElements);

		from(producer).end(sink);
	}

}
