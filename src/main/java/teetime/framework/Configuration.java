package teetime.framework;

public abstract class Configuration {

	protected abstract <T> void connectPorts(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity);

	protected abstract <T> void connectPorts(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort);

	protected abstract void addThreadableStage(final Stage stage);
}
