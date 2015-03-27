package teetime.framework;

import teetime.framework.pipe.IPipe;

public interface IStageVisitor {

	public enum VisitorBehavior {
		CONTINUE, STOP
	}

	VisitorBehavior visit(Stage stage, IPipe inputPipe);

}
