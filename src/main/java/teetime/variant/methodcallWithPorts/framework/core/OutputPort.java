package teetime.variant.methodcallWithPorts.framework.core;

import teetime.variant.methodcallWithPorts.framework.core.signal.Signal;

public final class OutputPort<T> extends AbstractPort<T> {

	/**
	 * Performance cache: Avoids the following method chain
	 *
	 * <pre>
	 * this.getPipe().getTargetPort().getOwningStage()
	 * </pre>
	 */
	// private StageWithPort cachedTargetStage;

	OutputPort() {
		super();
	}

	/**
	 *
	 * @param element
	 * @return <code>true</code> iff the given <code>element</code> could be sent, <code>false</code> otherwise (then use a re-try strategy)
	 */
	public boolean send(final T element) {
		return this.pipe.add(element);
	}

	// public StageWithPort getCachedTargetStage() {
	// return this.cachedTargetStage;
	// }

	@Deprecated
	public void setCachedTargetStage(final StageWithPort cachedTargetStage) {
		// this.cachedTargetStage = cachedTargetStage;
	}

	public void sendSignal(final Signal signal) {
		this.pipe.setSignal(signal);
	}

	public void reportNewElement() {
		this.pipe.reportNewElement();
	}

}
