package teetime.variant.methodcallWithPorts.framework.core;

import teetime.variant.methodcallWithPorts.framework.core.pipe.IPipe;

public class InputPort<T> {

	private final StageWithPort<?, ?> owningStage;
	private IPipe<T> pipe;

	public InputPort(final StageWithPort<?, ?> owningStage) {
		super();
		this.owningStage = owningStage;
	}

	public T receive() {
		T element = this.pipe.removeLast();
		return element;
	}

	public T read() {
		T element = this.pipe.readLast();
		return element;
	}

	public IPipe<T> getPipe() {
		return this.pipe;
	}

	/**
	 * Connects this input port with the given <code>pipe</code> bi-directionally
	 * 
	 * @param pipe
	 */
	public void setPipe(final IPipe<T> pipe) {
		this.pipe = pipe;
		pipe.setTargetPort(this);
	}

	public StageWithPort<?, ?> getOwningStage() {
		return this.owningStage;
	}

}
