package teetime.stage;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import teetime.framework.ConsumerStage;
import teetime.framework.OutputPort;
import teetime.util.StopWatch;

public class Cache<T> extends ConsumerStage<T> {

	private final OutputPort<T> outputPort = this.createOutputPort();

	private final List<T> cachedObjects = new LinkedList<T>();

	@Override
	protected void execute(final T element) {
		this.cachedObjects.add(element);
	}

	@Override
	public void onTerminating() {
		this.logger.debug("Emitting " + this.cachedObjects.size() + " cached elements...");
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		for (T cachedElement : this.cachedObjects) {
			this.send(this.outputPort, cachedElement);
		}
		stopWatch.end();
		this.logger.debug("Emitting took " + TimeUnit.NANOSECONDS.toMillis(stopWatch.getDurationInNs()) + " ms");
		super.onTerminating();
	}

	public OutputPort<T> getOutputPort() {
		return this.outputPort;
	}

}
