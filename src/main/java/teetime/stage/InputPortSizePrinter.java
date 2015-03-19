package teetime.stage;

import java.util.concurrent.TimeUnit;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;
import teetime.framework.pipe.IPipe;
import teetime.util.StopWatch;

public class InputPortSizePrinter<T> extends AbstractConsumerStage<T> {

	private final OutputPort<T> outputPort = createOutputPort();
	private final StopWatch stopWatch;

	private final long thresholdInNs = TimeUnit.SECONDS.toNanos(1);

	public InputPortSizePrinter() {
		stopWatch = new StopWatch();
		stopWatch.start();
	}

	@Override
	protected void execute(final T element) {
		stopWatch.end();
		if (stopWatch.getDurationInNs() >= thresholdInNs) {
			if (logger.isDebugEnabled()) {
				final IPipe pipe = inputPort.getPipe();
				logger.debug("pipe size: " + pipe.size());
			}
			stopWatch.start();
		}

		outputPort.send(element);
	}

	public OutputPort<T> getOutputPort() {
		return outputPort;
	}

}
