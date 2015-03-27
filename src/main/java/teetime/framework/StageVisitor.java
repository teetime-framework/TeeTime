package teetime.framework;

public interface StageVisitor {

	public enum VisitorBehavior {
		CONTINUE, STOP
	}

	VisitorBehavior visit(Stage stage);

}
