package teetime.stage;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;
import teetime.stage.util.CountingMap;

/**
 * This counts how many of different elements are sent to this stage. Nothing is forwarded.
 * On termination a Map of T's and counter value is sent to its outputport.
 *
 * @author Nelson Tavares de Sousa
 *
 * @param <T>
 */
public class DistributedMapCounter<T> extends AbstractConsumerStage<T> {

	private final CountingMap<T> counter = new CountingMap<T>();
	private final OutputPort<CountingMap<T>> port = createOutputPort();

	public DistributedMapCounter() {

	}

	@Override
	protected void execute(final T element) {
		counter.increment(element);

	}

	@Override
	public void onTerminating() throws Exception {
		port.send(counter);
		super.onTerminating();
	}

}
