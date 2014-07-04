package teetime.variant.methodcallWithPorts.framework.core;

import teetime.variant.methodcallWithPorts.framework.core.pipe.IPipe;

public class OutputPort<T> {

	private IPipe<T> pipe;
	/**
	 * Performance cache: Avoids the following method chain
	 * 
	 * <pre>
	 * this.getPipe().getTargetPort().getOwningStage()
	 * </pre>
	 */
	private StageWithPort<?, ?> cachedTargetStage;

	public void send(final T element) {
		this.pipe.add(element);
	}

	public IPipe<T> getPipe() {
		return this.pipe;
	}

	public void setPipe(final IPipe<T> pipe) {
		this.pipe = pipe;
	}

	public StageWithPort<?, ?> getCachedTargetStage() {
		return this.cachedTargetStage;
	}

	public void setCachedTargetStage(final StageWithPort<?, ?> cachedTargetStage) {
		this.cachedTargetStage = cachedTargetStage;
	}

	public void sendSignal(final Signal signal) {
		if (this.pipe != null) { // if the output port is connected with a pipe
			this.pipe.setSignal(signal);
		}
	}

}
