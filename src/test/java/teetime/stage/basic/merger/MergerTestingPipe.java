package teetime.stage.basic.merger;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.IPipe;
import teetime.framework.signal.ISignal;
import teetime.framework.signal.StartingSignal;
import teetime.framework.signal.TerminatingSignal;

public class MergerTestingPipe implements IPipe {

	private boolean startSent = false;
	private boolean terminateSent = false;

	public MergerTestingPipe() {}

	@Override
	public void sendSignal(final ISignal signal) {
		if (signal.getClass().equals(StartingSignal.class)) {
			this.startSent = true;
		} else if (signal.getClass().equals(TerminatingSignal.class)) {
			this.terminateSent = true;
		}
	}

	public boolean startSent() {
		return this.startSent;
	}

	public boolean terminateSent() {
		return this.terminateSent;
	}

	@Override
	public boolean add(final Object element) {
		return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object removeLast() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object readLast() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputPort<?> getTargetPort() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> void connectPorts(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reportNewElement() {
		// TODO Auto-generated method stub

	}

}
