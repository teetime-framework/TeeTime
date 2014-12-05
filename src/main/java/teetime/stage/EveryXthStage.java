package teetime.stage;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

public final class EveryXthStage<T> extends AbstractConsumerStage<T> {

	private final OutputPort<Integer> outputPort = createOutputPort();

	private final int threshold;

	private int counter;

	public EveryXthStage(final int threshold) {
		this.threshold = threshold;
	}

	@Override
	protected void execute(final T element) {
		counter++;
		if (counter % threshold == 0) {
			outputPort.send(Integer.valueOf(counter));
		}
	}

	public OutputPort<Integer> getOutputPort() {
		return outputPort;
	}

}
