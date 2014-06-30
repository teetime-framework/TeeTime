package teetime.variant.methodcallWithPorts.stage;

import teetime.variant.methodcallWithPorts.framework.core.ConsumerStage;

/**
 * @author Jan Waller, Nils Christian Ehmke, Christian Wulf
 * 
 * @since 1.10
 */
public class InstanceOfFilter<I, O> extends ConsumerStage<I, O> {

	private Class<O> type;

	/**
	 * @since 1.10
	 */
	public InstanceOfFilter(final Class<O> type) {
		this.type = type;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void execute5(final I element) {
		if (this.type.isInstance(element)) {
			this.send((O) element);
		} else { // swallow up the element
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("element is not an instance of " + this.type.getName() + ", but of " + element.getClass());
			}
		}
	}

	public Class<O> getType() {
		return this.type;
	}

	public void setType(final Class<O> type) {
		this.type = type;
	}

}
