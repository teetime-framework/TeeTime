package teetime.framework.pipe;

import teetime.util.ConstructorClosure;

public final class RelayTestPipe<T> extends InterThreadPipe {

	private int numInputObjects;
	private final ConstructorClosure<T> inputObjectCreator;

	public RelayTestPipe(final int numInputObjects, final ConstructorClosure<T> inputObjectCreator) {
		super(null, null);
		this.numInputObjects = numInputObjects;
		this.inputObjectCreator = inputObjectCreator;
	}

	@Override
	public boolean add(final Object element) {
		return false;
	}

	@Override
	public T removeLast() {
		if (this.numInputObjects == 0) {
			return null;
		} else {
			this.numInputObjects--;
			return this.inputObjectCreator.create();
		}
	}

	@Override
	public boolean isEmpty() {
		return (this.numInputObjects == 0);
	}

	@Override
	public int size() {
		return this.numInputObjects;
	}

	@Override
	public T readLast() {
		return null;
	}

}
