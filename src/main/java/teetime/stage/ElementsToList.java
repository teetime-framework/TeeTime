package teetime.stage;

import java.util.LinkedList;
import java.util.List;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

public final class ElementsToList<I> extends AbstractConsumerStage<I> {

	private final int size;

	private final OutputPort<List<I>> outputPort = this.createOutputPort();

	private final List<I> cachedObjects = new LinkedList<I>();

	public ElementsToList(final int size) {
		this.size = size;
	}

	@Override
	protected void execute(final I element) {
		if (cachedObjects.size() < size) {
			this.logger.debug("Received element #" + this.cachedObjects.size());
			this.cachedObjects.add(element);
		} else {
			this.logger.debug("Sending cached element List to output port...");
			this.outputPort.send(cachedObjects);
		}
	}

	public OutputPort<List<I>> getOutputPort() {
		return this.outputPort;
	}
}
