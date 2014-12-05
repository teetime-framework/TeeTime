package teetime.stage;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

public class EveryXthStage<T> extends AbstractConsumerStage<T> {

	private final OutputPort<T> outputPort = createOutputPort();
	private final int threshold;

	private int counter;

	public EveryXthStage(final int threshold) {
		this.threshold = threshold;
	}

	@Override
	protected void execute(final T element) {
		counter++;
		if (counter % threshold == 0) {
			outputPort.send(element);
		}
	}

	public OutputPort<T> getOutputPort() {
		return outputPort;
	}

}
