package teetime.stage;

import teetime.framework.ConsumerStage;
import teetime.framework.OutputPort;

/**
 * @author Jan Waller, Nils Christian Ehmke, Christian Wulf
 * 
 */
public class InstanceOfFilter<I, O> extends ConsumerStage<I> {

	private final OutputPort<O> outputPort = this.createOutputPort();

	private Class<O> type;

	public InstanceOfFilter(final Class<O> type) {
		this.type = type;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void execute(final I element) {
		if (this.type.isInstance(element)) {
			this.send(this.outputPort, (O) element);
		} else { // swallow up the element
			if (this.logger.isDebugEnabled()) {
				this.logger.info("element is not an instance of " + this.type.getName() + ", but of " + element.getClass());
			}
		}
	}

	public Class<O> getType() {
		return this.type;
	}

	public void setType(final Class<O> type) {
		this.type = type;
	}

	public OutputPort<O> getOutputPort() {
		return this.outputPort;
	}

}
