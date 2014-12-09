package teetime.framework;

import teetime.framework.signal.ISignal;

public final class OutputPort<T> extends AbstractPort<T> {

	OutputPort() {
		super();
	}

	/**
	 * @param element
	 *            to be sent
	 */
	public void send(final T element) {
		if (this.pipe.add(element)) {
			this.pipe.reportNewElement();
		}
	}

	/**
	 *
	 * @param signal
	 *            to be sent
	 */
	public void sendSignal(final ISignal signal) {
		this.pipe.sendSignal(signal);
	}

}
