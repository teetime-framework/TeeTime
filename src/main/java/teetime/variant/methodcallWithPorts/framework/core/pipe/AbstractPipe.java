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

	@Override
	public InputPort<?> getTargetPort() {
		return this.targetPort;
	}

	@Override
	public void setTargetPort(final InputPort<?> targetPort) {
		this.targetPort = targetPort;
		this.cachedTargetStage = targetPort.getOwningStage();
	}

	@Override
	public <T> void connectPorts(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		sourcePort.setPipe(this);
		targetPort.setPipe(this);
	}

}
