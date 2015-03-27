package teetime.framework;

import teetime.framework.StageVisitor.VisitorBehavior;
import teetime.framework.pipe.IPipe;

public class Traversor {

	private final StageVisitor stageVisitor;

	public Traversor(final StageVisitor stageVisitor) {
		this.stageVisitor = stageVisitor;
	}

	public void traverse(final Stage stage) {
		VisitorBehavior visitorBehavior = stageVisitor.visit(stage);
		if (visitorBehavior == VisitorBehavior.STOP) {
			return;
		}

		OutputPort<?>[] outputPorts = stage.getOutputPorts();
		for (OutputPort<?> outputPort : outputPorts) {
			IPipe pipe = outputPort.getPipe();
			if (null != pipe) {
				Stage owningStage = pipe.getTargetPort().getOwningStage();
				traverse(owningStage); // recursive call
			}
		}
	}
}
