package teetime.framework;

import teetime.framework.pipe.IPipe;

public interface StageVisitor {

	public enum VisitorBehavior {
		CONTINUE, STOP
	}

	VisitorBehavior visit(Stage stage, IPipe inputPipe);

}
