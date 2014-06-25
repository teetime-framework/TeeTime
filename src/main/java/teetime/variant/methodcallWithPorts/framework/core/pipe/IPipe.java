package teetime.variant.methodcallWithPorts.framework.core.pipe;

import teetime.variant.methodcallWithPorts.framework.core.StageWithPort;

public interface IPipe<T> {

	public abstract void add(T element);

	public abstract T removeLast();

	public abstract boolean isEmpty();

	public abstract int size();

	public abstract T readLast();

	public abstract void close();

	public abstract boolean isClosed();

	public abstract StageWithPort<T, ?> getTargetStage();

}
