package teetime.example;

import java.util.List;

import teetime.framework.Configuration;
import teetime.stage.CollectorSink;
import teetime.stage.Counter;
import teetime.stage.ObjectProducer;
import teetime.util.ConstructorClosure;

public class ManyElementsGlobalTaskPoolConfig extends Configuration {

	public ManyElementsGlobalTaskPoolConfig(final int numInputObjects, final List<Integer> outputElements) {
		ObjectProducer<Integer> producer = new ObjectProducer<>(numInputObjects, new ConstructorClosure<Integer>() {
			private int counter;

			@Override
			public Integer create() {
				return counter++;
			}
		});
		Counter<Integer> counter = new Counter<>();
		CollectorSink<Integer> sink = new CollectorSink<>(outputElements);
		from(producer).to(counter).end(sink);
	}
}
