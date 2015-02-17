package teetime.stage;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;
import teetime.stage.util.CountingMap;

/**
 * Receives different CountingMap instances and merges them into a single one.
 * The result is sent upon termination.
 *
 * @author Nelson Tavares de Sousa
 *
 * @param <T>
 *            Key type of the map to be sent
 */
public class CountingmapMerger<T> extends AbstractConsumerStage<CountingMap<T>> {

	private final CountingMap<T> result = new CountingMap<T>();
	private final OutputPort<Map<T, Integer>> port = createOutputPort();

	private final int numberOfInputPorts;

	public CountingmapMerger(final int numberOfInputPorts) {
		for (int i = 1; i < numberOfInputPorts; i++) {
			createInputPort();
		}
		this.numberOfInputPorts = numberOfInputPorts;
	}

	@Override
	protected void execute(final CountingMap<T> element) {
		Set<Map.Entry<T, Integer>> entries = element.entrySet();
		for (Entry<T, Integer> entry : entries) {
			Integer resultValue = result.get(entry.getKey());
			if (resultValue == null) {
				result.put(entry.getKey(), entry.getValue());
			}
			else {
				Integer temp = result.get(entry.getKey());
				temp += entry.getValue();
				result.put(entry.getKey(), temp);
			}
		}
	}

	@Override
	public void onTerminating() throws Exception {
		port.send(result);
		super.onTerminating();
	}

}
