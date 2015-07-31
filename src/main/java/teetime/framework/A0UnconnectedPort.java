package teetime.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.pipe.DummyPipe;

public class A0UnconnectedPort implements IPortVisitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(A0UnconnectedPort.class);

	@Override
	public void visit(final AbstractPort<?> port) {
		if (port.getPipe() == null) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Unconnected output port: " + port + ". Connecting with a dummy output port.");
			}
			port.setPipe(DummyPipe.INSTANCE);
		}
	}
}
