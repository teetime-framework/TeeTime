package teetime.variant.methodcallWithPorts.framework.core.pipe;

public interface IPipe<T> {

	public abstract void add(T element);

	public abstract T removeLast();

	public abstract boolean isEmpty();

	public abstract int size();

	public abstract T readLast();

	public abstract void close();

	public abstract boolean isClosed();

}
