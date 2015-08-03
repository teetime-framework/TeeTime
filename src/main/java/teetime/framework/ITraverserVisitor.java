package teetime.framework;

import teetime.framework.Traverser.VisitorBehavior;

public interface ITraverserVisitor {

	VisitorBehavior visit(Stage stage);

	VisitorBehavior visit(AbstractPort<?> port);

}
