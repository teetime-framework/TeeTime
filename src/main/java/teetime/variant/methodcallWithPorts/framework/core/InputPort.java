package teetime.variant.methodcallWithPorts.framework.core;

import teetime.variant.methodcallWithPorts.framework.core.pipe.IPipe;

public class InputPort<T> extends AbstractPort<T> {

	private final StageWithPort owningStage;

	InputPort(final StageWithPort owningStage) {
		super();
		this.owningStage = owningStage;
	}

	public T receive() {
		@SuppressWarnings("unchecked")
		T element = (T) this.pipe.removeLast();
		return element;
	}

	public T read() {
		@SuppressWarnings("unchecked")
		T element = (T) this.pipe.readLast();
		return element;
	}

	/**
	 * Connects this input port with the given <code>pipe</code> bi-directionally
	 *
	 * @param pipe
	 */
	@Override
	public void setPipe(final IPipe pipe) {
		this.pipe = pipe;
		pipe.setTargetPort(this);
	}

	public StageWithPort getOwningStage() {
		return this.owningStage;
	}

}
