package teetime.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.Traverser.VisitorBehavior;
import teetime.framework.pipe.DummyPipe;

public class A0UnconnectedPort implements ITraverserVisitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(A0UnconnectedPort.class);

	@Override
	public VisitorBehavior visit(final Stage stage) {
		return VisitorBehavior.CONTINUE;
	}

	@Override
	public VisitorBehavior visit(final AbstractPort<?> port) {
		if (port.getPipe() == null) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Unconnected output port: " + port + ". Connecting with a dummy output port.");
			}
			port.setPipe(DummyPipe.INSTANCE);
			return VisitorBehavior.STOP;
		}
		return VisitorBehavior.CONTINUE;
	}
}
