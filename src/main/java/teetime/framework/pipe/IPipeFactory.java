package teetime.framework.pipe;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;

public interface IPipeFactory {

	<T> IPipe<T> newPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort);

	<T> IPipe<T> newPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, int capacity);
}
