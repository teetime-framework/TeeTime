package teetime.framework;

import teetime.framework.signal.ISignal;

public final class OutputPort<T> extends AbstractPort<T> {

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

	public void sendSignal(final ISignal signal) {
		this.pipe.sendSignal(signal);
	}

	public void reportNewElement() {
		this.pipe.reportNewElement();
	}

}
