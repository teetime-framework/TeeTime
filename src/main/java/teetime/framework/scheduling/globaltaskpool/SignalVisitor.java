package teetime.framework.scheduling.globaltaskpool;

import teetime.framework.*;
import teetime.framework.Traverser.VisitorBehavior;
import teetime.framework.pipe.AbstractSynchedPipe;
import teetime.framework.pipe.DummyPipe;
import teetime.framework.signal.ISignal;

public class SignalVisitor implements ITraverserVisitor {

	private final ISignal signal;

	public SignalVisitor(final ISignal signal) {
		this.signal = signal;
	}

	@Override
	public VisitorBehavior visit(final AbstractStage stage) {
		if (stage.isProducer()) {
			stage.onSignal(signal, null);
		}
		return VisitorBehavior.CONTINUE_FORWARD;
	}

	@Override
	public VisitorBehavior visit(final AbstractPort<?> port) {
		if (!(port instanceof OutputPort)) {
			throw new IllegalStateException("Unexpected port type: " + port.getClass());
		}
		InputPort<?> inputPort = port.getPipe().getTargetPort();

		// drain signal from internal signal queue
		AbstractSynchedPipe<?> synchedPipe = (AbstractSynchedPipe<?>) port.getPipe();
		ISignal receivedSignal = synchedPipe.getSignal();
		if (null == receivedSignal) {
			return VisitorBehavior.CONTINUE_FORWARD;
		}
		if (receivedSignal != signal) {
			throw new IllegalStateException("Unexpected signal: " + receivedSignal);
		}
		inputPort.getOwningStage().onSignal(receivedSignal, inputPort);
		return VisitorBehavior.CONTINUE_FORWARD;
	}

	@Override
	public void visit(final DummyPipe pipe, final AbstractPort<?> port) {
		// do nothing
	}

}
