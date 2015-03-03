package teetime.stage.basic;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

public abstract class AbstractTransform<I, O> extends AbstractConsumerStage<I> {

	private final OutputPort<O> outputPort = createOutputPort();

	protected AbstractTransform() {
		super();
	}

	public OutputPort<O> getOutputPort() {
		return outputPort;
	}
}
