package teetime.variant.methodcallWithPorts.framework.core.pipe;

import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.StageWithPort;

public abstract class AbstractPipe<T> implements IPipe<T> {

	private InputPort<T> targetPort;

	/**
	 * Performance cache: Avoids the following method chain
	 *
	 * <pre>
	 * this.getPipe().getTargetPort().getOwningStage()
	 * </pre>
	 */
	protected StageWithPort cachedTargetStage;

	@Override
	public InputPort<T> getTargetPort() {
		return this.targetPort;
	}

	@Override
	public void setTargetPort(final InputPort<T> targetPort) {
		this.targetPort = targetPort;
		this.cachedTargetStage = targetPort.getOwningStage();
	}

}
