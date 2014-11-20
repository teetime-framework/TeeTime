package teetime.framework.pipe;

import teetime.framework.IStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;

public abstract class AbstractPipe implements IPipe {

	/**
	 * Performance cache: Avoids the following method chain
	 *
	 * <pre>
	 * this.getPipe().getTargetPort().getOwningStage()
	 * </pre>
	 */
	protected IStage cachedTargetStage;

	private InputPort<?> targetPort;

	protected <T> AbstractPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		this.targetPort = targetPort;
		if (null != targetPort) { // BETTER remove this check if migration is completed
			this.cachedTargetStage = targetPort.getOwningStage();
		}
		if (null != sourcePort) { // BETTER remove this check if migration is completed
			sourcePort.setPipe(this);
		}
		if (null != targetPort) { // BETTER remove this check if migration is completed
			targetPort.setPipe(this);
		}
	}

	@Override
	public InputPort<?> getTargetPort() {
		return this.targetPort;
	}

	@Override
	public <T> void connectPorts(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		sourcePort.setPipe(this);
		targetPort.setPipe(this);
		this.targetPort = targetPort;
		this.cachedTargetStage = targetPort.getOwningStage();
	}

}
