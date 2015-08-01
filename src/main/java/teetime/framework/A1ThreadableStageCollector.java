package teetime.framework;

import java.util.HashSet;
import java.util.Set;

import teetime.framework.Traverser.VisitorBehavior;

public class A1ThreadableStageCollector implements ITraverserVisitor {

	private final Set<Stage> threadableStages = new HashSet<Stage>();

	public Set<Stage> getThreadableStages() {
		return threadableStages;
	}

	@Override
	public VisitorBehavior visit(final Stage stage) {
		if (stage.getOwningThread() != null && !threadableStages.contains(stage)) {
			threadableStages.add(stage);
		}
		return VisitorBehavior.CONTINUE;
	}

	@Override
	public VisitorBehavior visit(final AbstractPort<?> port) {
		return VisitorBehavior.CONTINUE;
	}

}
