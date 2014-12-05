package teetime.framework;

import teetime.framework.pipe.IPipe;

public class InputPort<T> extends AbstractPort<T> {

	private final AbstractBasicStage owningStage;

	InputPort(final AbstractBasicStage owningStage) {
		super();
		this.owningStage = owningStage;
	}

	public T receive() {
		@SuppressWarnings("unchecked")
		final T element = (T) this.pipe.removeLast();
		return element;
	}

	public T read() {
		@SuppressWarnings("unchecked")
		final T element = (T) this.pipe.readLast();
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
	}

	public AbstractBasicStage getOwningStage() {
		return this.owningStage;
	}

}
