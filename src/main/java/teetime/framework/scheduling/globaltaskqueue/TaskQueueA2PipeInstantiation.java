package teetime.framework.scheduling.globaltaskqueue;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.*;
import teetime.framework.pipe.*;

/**
 * Created by nilsziermann on 30.12.16.
 */
class TaskQueueA2PipeInstantiation implements ITraverserVisitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskQueueA2PipeInstantiation.class);

	private final Set<IPipe<?>> visitedPipes = new HashSet<IPipe<?>>();

	@Override
	public Traverser.VisitorBehavior visit(final AbstractStage stage) {
		return Traverser.VisitorBehavior.CONTINUE;
	}

	@Override
	public Traverser.VisitorBehavior visit(final AbstractPort<?> port) {
		IPipe<?> pipe = port.getPipe();
		if (visitedPipes.contains(pipe)) {
			return Traverser.VisitorBehavior.STOP; // NOPMD two returns are better
		}
		visitedPipes.add(pipe);

		instantiatePipe(pipe);

		return Traverser.VisitorBehavior.CONTINUE;
	}

	@Override
	public void visit(final DummyPipe pipe, final AbstractPort<?> port) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Unconnected port " + port + " in stage " + port.getOwningStage().getId());
		}
	}

	private <T> void instantiatePipe(final IPipe<T> pipe) {
		if (!(pipe instanceof InstantiationPipe)) { // if manually connected
			return;
		}

		new UnboundedMpMcSynchedPipe<T>(pipe.getSourcePort(), pipe.getTargetPort());
		LOGGER.debug("Connected (unbounded MpMc) {} and {}", pipe.getSourcePort(), pipe.getTargetPort());
	}
}
