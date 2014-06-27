package teetime.variant.methodcallWithPorts.stage;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import teetime.util.StopWatch;
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
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		for (T cachedElement : this.cachedObjects) {
			this.send(cachedElement);
		}
		stopWatch.end();
		System.out.println("dur: " + TimeUnit.NANOSECONDS.toMillis(stopWatch.getDurationInNs()) + " ms");
		super.onIsPipelineHead();
	}

}
