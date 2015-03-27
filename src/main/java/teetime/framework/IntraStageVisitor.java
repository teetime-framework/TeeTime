package teetime.framework;

import java.util.ArrayList;
import java.util.List;

import teetime.framework.pipe.IPipe;

public class IntraStageVisitor implements IStageVisitor {

	private final List<Stage> visitedStages;

	public IntraStageVisitor() {
		this.visitedStages = new ArrayList<Stage>();
	}

	@Override
	public VisitorBehavior visit(final Stage stage, final IPipe inputPipe) {
		if (inputPipe instanceof AbstractIntraThreadPipe) {
			visitedStages.add(stage);
			return VisitorBehavior.CONTINUE;
		}
		return VisitorBehavior.STOP;
	}

	public List<Stage> getVisitedStages() {
		return visitedStages;
	}

}
