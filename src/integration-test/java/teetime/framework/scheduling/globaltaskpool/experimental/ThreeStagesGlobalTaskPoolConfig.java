package teetime.framework.scheduling.globaltaskpool.experimental;

import java.util.List;

import teetime.framework.Configuration;
import teetime.stage.CollectorSink;
import teetime.stage.Counter;
import teetime.stage.ObjectProducer;
import teetime.util.ConstructorClosure;

public class ThreeStagesGlobalTaskPoolConfig extends Configuration {

	public ThreeStagesGlobalTaskPoolConfig(final int numInputObjects, final List<Integer> outputElements) {
		ObjectProducer<Integer> producer = new ObjectProducer<>(numInputObjects, new ConstructorClosure<Integer>() {
			private int counter;

			@Override
			public Integer create() {
				return counter++;
			}
		});
		AssertFilter assertFilter = new AssertFilter();
		Counter<Integer> counter = new Counter<>();
		CollectorSink<Integer> sink = new CollectorSink<>(outputElements);
		from(producer).to(assertFilter).to(counter).end(sink);
	}
}
