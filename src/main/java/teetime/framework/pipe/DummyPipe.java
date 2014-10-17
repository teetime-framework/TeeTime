package teetime.framework.pipe;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.signal.ISignal;

/**
 * A pipe implementation used to connect unconnected output ports.
 *
 * @author Christian Wulf
 *
 */
@SuppressWarnings("rawtypes")
public final class DummyPipe implements IPipe {

	@Override
	public boolean add(final Object element) {
		return false;
	}

	@Override
	public Object removeLast() {
		return null;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public Object readLast() {
		return null;
	}

	@Override
	public InputPort<Object> getTargetPort() {
		return null;
	}

	@Override
	public void sendSignal(final ISignal signal) {}

	@Override
	public void connectPorts(final OutputPort sourcePort, final InputPort targetPort) {}

	@Override
	public void reportNewElement() {
		// do nothing
	}

}
