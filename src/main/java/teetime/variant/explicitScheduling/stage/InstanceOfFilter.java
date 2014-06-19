package teetime.variant.explicitScheduling.stage;

import teetime.variant.explicitScheduling.framework.core.AbstractFilter;
import teetime.variant.explicitScheduling.framework.core.Context;
import teetime.variant.explicitScheduling.framework.core.IInputPort;
import teetime.variant.explicitScheduling.framework.core.IOutputPort;

/**
 * @author Jan Waller, Nils Christian Ehmke, Christian Wulf
 * 
 * @since 1.10
 */
public class InstanceOfFilter<I, O> extends AbstractFilter<InstanceOfFilter<I, O>> {

	public final IInputPort<InstanceOfFilter<I, O>, I> inputPort = this.createInputPort();

	public final IOutputPort<InstanceOfFilter<I, O>, O> matchingOutputPort = this.createOutputPort();
	public final IOutputPort<InstanceOfFilter<I, O>, I> mismatchingOutputPort = this.createOutputPort();

	private Class<O> type;

	/**
	 * @since 1.10
	 */
	public InstanceOfFilter(final Class<O> type) {
		this.type = type;
	}

	@Override
	protected boolean execute(final Context<InstanceOfFilter<I, O>> context) {
		final I inputObject = context.tryTake(this.inputPort);
		if (inputObject == null) {
			return false;
		}

		if (this.type.isInstance(inputObject)) {
			context.put(this.matchingOutputPort, this.type.cast(inputObject));
		} else {
			context.put(this.mismatchingOutputPort, inputObject);
		}

		return true;
	}

	public Class<O> getType() {
		return this.type;
	}

	public void setType(final Class<O> type) {
		this.type = type;
	}

}
