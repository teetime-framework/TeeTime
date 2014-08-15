package teetime.variant.methodcallWithPorts.framework.core.pipe;

public class OrderedGrowableArrayPipeFactory implements IPipeFactory {

	/**
	 * Hint: The capacity for this pipe implementation is ignored
	 */
	@Override
	public <T> IPipe<T> create(final int capacity) {
		return new OrderedGrowableArrayPipe<T>();
	}

}
