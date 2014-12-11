package teetime.stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

/**
 * @author Nils Christian Ehmke
 */
public final class MultipleInstanceOfFilter<I> extends AbstractConsumerStage<I> {

	private final Map<Class<? extends I>, OutputPort<? super I>> outputPortsMap = new HashMap<Class<? extends I>, OutputPort<? super I>>();

	@SuppressWarnings("unchecked")
	public <T extends I> OutputPort<T> getOutputPortForType(final Class<T> clazz) {
		if (!this.outputPortsMap.containsKey(clazz)) {
			this.outputPortsMap.put(clazz, super.createOutputPort());
		}
		return (OutputPort<T>) this.outputPortsMap.get(clazz);
	}

	@Override
	protected void execute(final I element) {
		for (Entry<Class<? extends I>, OutputPort<? super I>> outputPortMapEntry : outputPortsMap.entrySet()) {
			if (outputPortMapEntry.getKey().isInstance(element)) {
				outputPortMapEntry.getValue().send(element);
			}
		}
	}

}
