package teetime.framework.scheduling.globaltaskqueue;

import java.util.HashSet;
import java.util.Set;

import teetime.framework.*;
import teetime.framework.pipe.DummyPipe;

/**
 * Created by nilsziermann on 30.12.16.
 */
class TaskQueueA1StageCollector implements ITraverserVisitor {

	private final Set<AbstractStage> stages = new HashSet<AbstractStage>();

	public Set<AbstractStage> getStages() {
		return this.stages;
	}

	@Override
	public Traverser.VisitorBehavior visit(final AbstractStage stage) {
		if (!stages.contains(stage) && stage.getCurrentState() == StageState.CREATED) {
			stages.add(stage);
		}
		// visitor termination condition: stop if the stage already runs or has been terminated
		return stage.getCurrentState() == StageState.CREATED ? Traverser.VisitorBehavior.CONTINUE : Traverser.VisitorBehavior.STOP;
	}

	@Override
	public Traverser.VisitorBehavior visit(final AbstractPort<?> port) {
		return Traverser.VisitorBehavior.CONTINUE;
	}

	@Override
	public void visit(final DummyPipe pipe, final AbstractPort<?> port) {
		// do nothing
	}

}
