package teetime.framework.pipe;

import org.junit.Test;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;

public class SpScPipeTest {

	@Test
	public void testSignalOrdering() throws Exception {
		OutputPort<? extends Object> sourcePort = null;
		InputPort<Object> targetPort = null;
		IPipe pipe = new SpScPipe(sourcePort, targetPort, 1);

		// TODO implement test
		// pipe.sendSignal(signal);

		// pipe.getSignal();
	}
}
