package teetime.variant.methodcallWithPorts.framework.core.pipe;

public class SpScPipeFactory implements IPipeFactory {

	@Override
	public <T> IPipe<T> create(final int capacity) {
		return new SpScPipe<T>(capacity);
	}

}
