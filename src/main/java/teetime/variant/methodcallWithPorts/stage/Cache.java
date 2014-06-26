package teetime.variant.methodcallWithPorts.stage;

import java.util.LinkedList;
import java.util.List;

import teetime.variant.methodcallWithPorts.framework.core.ConsumerStage;

public class Cache<T> extends ConsumerStage<T, T> {

	private final List<T> cachedObjects = new LinkedList<T>();

	@Override
	protected void execute5(final T element) {
		this.cachedObjects.add(element);
	}

	@Override
	public void onIsPipelineHead() {
		this.logger.debug("Emitting cached elements...");
		for (T cachedElement : this.cachedObjects) {
			this.send(cachedElement);
		}
		super.onIsPipelineHead();
	}

}
