package teetime.framework;

import java.util.HashSet;
import java.util.Set;

import teetime.framework.StageVisitor.VisitorBehavior;
import teetime.framework.pipe.IPipe;

public class Traversor {

	private final StageVisitor stageVisitor;
	private final Set<Stage> visitedStage = new HashSet<Stage>();

	public Traversor(final StageVisitor stageVisitor) {
		this.stageVisitor = stageVisitor;
	}

	public void traverse(final Stage stage, final IPipe inputPipe) {
		if (!visitedStage.contains(stage)) {
			visitedStage.add(stage);
		} else {
			return;
		}

		VisitorBehavior visitorBehavior = stageVisitor.visit(stage, inputPipe);
		if (visitorBehavior == VisitorBehavior.STOP) {
			return;
		}

		OutputPort<?>[] outputPorts = stage.getOutputPorts();
		for (OutputPort<?> outputPort : outputPorts) {
			IPipe pipe = outputPort.getPipe();
			if (null != pipe) {
				Stage owningStage = pipe.getTargetPort().getOwningStage();
				traverse(owningStage, pipe); // recursive call
			}
		}
	}
}
