package teetime.stage;

import java.util.HashMap;
import java.util.Map;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

/**
 * This counts how many of different elements are sent to this stage. Nothing is forwarded.
 * On termination a Map of T's and counter value is sent to its outputport.
 *
 * @author Nelson Tavares de Sousa
 *
 * @param <T>
 */
public class DistributedMapCounter<T> extends AbstractConsumerStage<T> {

	private final Map<T, Integer> counter = new HashMap<T, Integer>();
	private final OutputPort<Map<T, Integer>> port = createOutputPort();

	public DistributedMapCounter() {

	}

	@Override
	protected void execute(final T element) {
		if (counter.containsKey(element)) {
			Integer i = counter.get(element);
			i++;
			counter.put(element, i);
		} else {
			counter.put(element, 0);
		}

	}

	@Override
	public void onTerminating() throws Exception {
		port.send(counter);
		super.onTerminating();
	}

}
