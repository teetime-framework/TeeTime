package teetime.variant.methodcallWithPorts.framework.core.pipe;

public interface IPipeFactory {

	<T> IPipe<T> create(int capacity);

}
