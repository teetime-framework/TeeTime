package teetime.variant.methodcallWithPorts.framework.core.pipe;

public class SingleElementPipeFactory implements IPipeFactory {

	/**
	 * Hint: The capacity for this pipe implementation is ignored
	 */
	@Override
	public <T> IPipe<T> create(final int capacity) {
		return new SingleElementPipe<T>();
	}

}
