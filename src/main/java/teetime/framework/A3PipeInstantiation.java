package teetime.framework;

import java.util.HashSet;
import java.util.Set;

import teetime.framework.Traverser.VisitorBehavior;
import teetime.framework.pipe.IPipe;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.InstantiationPipe;
import teetime.framework.pipe.SingleElementPipeFactory;
import teetime.framework.pipe.SpScPipeFactory;
import teetime.framework.pipe.UnboundedSpScPipeFactory;

public class A3PipeInstantiation implements ITraverserVisitor {

	private static final IPipeFactory interBoundedThreadPipeFactory = new SpScPipeFactory();
	private static final IPipeFactory interUnboundedThreadPipeFactory = new UnboundedSpScPipeFactory();
	private static final IPipeFactory intraThreadPipeFactory = new SingleElementPipeFactory();

	private final Set<IPipe<?>> visitedPipes = new HashSet<IPipe<?>>();

	@Override
	public VisitorBehavior visit(final Stage stage) {
		return VisitorBehavior.CONTINUE;
	}

	@Override
	public VisitorBehavior visit(final AbstractPort<?> port) {
		IPipe<?> pipe = port.getPipe();
		if (visitedPipes.contains(pipe)) {
			return VisitorBehavior.STOP;
		}
		visitedPipes.add(pipe);

		instantiatePipe(pipe);

		return VisitorBehavior.CONTINUE;
	}

	private <T> void instantiatePipe(final IPipe<T> pipe) {
		if (!(pipe instanceof InstantiationPipe)) { // if manually connected
			return;
		}

		Thread sourceStageThread = pipe.getSourcePort().getOwningStage().getOwningThread();
		Thread targetStageThread = pipe.getTargetPort().getOwningStage().getOwningThread();

		if (targetStageThread != null && sourceStageThread != targetStageThread) {
			// inter
			if (pipe.capacity() != 0) {
				interBoundedThreadPipeFactory.create(pipe.getSourcePort(), pipe.getTargetPort(), pipe.capacity());
			} else {
				interUnboundedThreadPipeFactory.create(pipe.getSourcePort(), pipe.getTargetPort(), 4);
			}
			return;
		} else {
			// normal or reflexive pipe => intra
		}

		intraThreadPipeFactory.create(pipe.getSourcePort(), pipe.getTargetPort(), 4);
	}

}
