package teetime.variant.methodcall.framework.core;

import teetime.variant.methodcall.framework.core.pipe.IPipe;

public class OutputPort<T> {

	private IPipe<T> pipe;

	public void send(final T element) {
		this.pipe.add(element);
	}

	public IPipe<T> getPipe() {
		return this.pipe;
	}

	public void setPipe(final IPipe<T> pipe) {
		this.pipe = pipe;
	}
}
