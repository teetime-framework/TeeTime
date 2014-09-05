package teetime.variant.methodcallWithPorts.framework.core.pipe;

import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;
import teetime.variant.methodcallWithPorts.framework.core.StageWithPort;

public abstract class AbstractPipe implements IPipe {

	private InputPort<?> targetPort;

	/**
	 * Performance cache: Avoids the following method chain
	 *
	 * <pre>
	 * this.getPipe().getTargetPort().getOwningStage()
	 * </pre>
	 */
	protected StageWithPort cachedTargetStage;

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
