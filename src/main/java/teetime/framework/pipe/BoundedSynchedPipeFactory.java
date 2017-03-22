package teetime.framework.pipe;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;

public final class BoundedSynchedPipeFactory implements IPipeFactory {

	private static final int DEFAULT_CAPACITY = 1024;

	public static final BoundedSynchedPipeFactory INSTANCE = new BoundedSynchedPipeFactory();

	private BoundedSynchedPipeFactory() {}

	/**
	 * Uses a default capacity of {@value #DEFAULT_CAPACITY}.
	 */
	@Override
	public <T> IPipe<T> newPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		return newPipe(sourcePort, targetPort, DEFAULT_CAPACITY);
	}

	@Override
	public <T> IPipe<T> newPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		return new BoundedSynchedPipe<>(sourcePort, targetPort, capacity);
	}

}