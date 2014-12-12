package teetime.framework;

public final class InputPort<T> extends AbstractPort<T> {

	private final Stage owningStage;

	InputPort(final Stage owningStage) {
		super();
		this.owningStage = owningStage;
	}

	/**
	 *
	 * @return the next element from the connected pipe
	 */
	@SuppressWarnings("unchecked")
	public T receive() {
		return (T) this.pipe.removeLast();
	}

	public Stage getOwningStage() {
		return this.owningStage;
	}

}
