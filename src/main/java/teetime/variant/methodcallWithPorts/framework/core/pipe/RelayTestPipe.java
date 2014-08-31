package teetime.variant.methodcallWithPorts.framework.core.pipe;

import teetime.util.ConstructorClosure;

public final class RelayTestPipe<T> extends InterThreadPipe<T> {

	private int numInputObjects;
	private final ConstructorClosure<T> inputObjectCreator;

	public RelayTestPipe(final int numInputObjects,
			final ConstructorClosure<T> inputObjectCreator) {
		this.numInputObjects = numInputObjects;
		this.inputObjectCreator = inputObjectCreator;
	}

	@Override
	public boolean add(final T element) {
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
