package teetime.framework;

import java.util.HashSet;
import java.util.Set;

import teetime.framework.pipe.IPipe;

public class A1ThreadableStageCollector implements IPipeVisitor {

	private final Set<Stage> threadableStages = new HashSet<Stage>();
	private final Set<IPipe<?>> visitedPipes = new HashSet<IPipe<?>>();

	public Set<Stage> getThreadableStages() {
		return threadableStages;
	}

	@Override
	public VisitorBehavior visit(final IPipe<?> pipe) {
		if (visitedPipes.contains(pipe)) {
			return VisitorBehavior.STOP;
		}
		visitedPipes.add(pipe);

		collectThreadableStage(pipe.getSourcePort().getOwningStage());
		collectThreadableStage(pipe.getTargetPort().getOwningStage());

		return VisitorBehavior.CONTINUE;
	}

	private void collectThreadableStage(final Stage stage) {
		if (stage.getOwningThread() != null && !threadableStages.contains(stage)) {
			threadableStages.add(stage);
		}
	}
}
