package teetime.framework.termination;

import teetime.framework.AbstractPort;
import teetime.framework.AbstractStage;
import teetime.framework.ITraverserVisitor;
import teetime.framework.InputPort;
import teetime.framework.Traverser.VisitorBehavior;
import teetime.framework.pipe.DummyPipe;

class ActiveConsumerStageFinder implements ITraverserVisitor {

	private InputPort<?> activeConsumerStageInputPort;

	@Override
	public VisitorBehavior visit(final AbstractStage stage) {
		return VisitorBehavior.CONTINUE_FORWARD;
	}

	@Override
	public VisitorBehavior visit(final AbstractPort<?> outputPort) {
		InputPort<?> targetPort = outputPort.getPipe().getTargetPort();
		AbstractStage targetStage = targetPort.getOwningStage();

		if (targetStage.isActive() && !targetStage.isProducer()) {
			activeConsumerStageInputPort = targetPort;
			return VisitorBehavior.STOP;
		}
		return VisitorBehavior.CONTINUE_FORWARD;
	}

	@Override
	public void visit(final DummyPipe pipe, final AbstractPort<?> port) {
		// do nothing
	}

	public InputPort<?> getActiveConsumerStageInputPort() {
		return activeConsumerStageInputPort;
	}

}
