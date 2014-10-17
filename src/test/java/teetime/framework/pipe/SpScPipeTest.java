package teetime.framework.pipe;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.signal.ISignal;
import teetime.framework.signal.StartingSignal;
import teetime.framework.signal.TerminatingSignal;
import teetime.framework.signal.ValidatingSignal;

public class SpScPipeTest {

	@Test
	public void testSignalOrdering() throws Exception {
		OutputPort<? extends Object> sourcePort = null;
		InputPort<Object> targetPort = null;
		InterThreadPipe pipe = new SpScPipe(sourcePort, targetPort, 1); // IPipe does not provide getSignal method

		List<ISignal> list = new ArrayList<ISignal>();
		list.add(new StartingSignal());
		list.add(new TerminatingSignal());
		list.add(new ValidatingSignal());
		list.add(new StartingSignal());
		list.add(new TerminatingSignal());
		list.add(new ValidatingSignal());
		list.add(new StartingSignal());
		list.add(new TerminatingSignal());
		list.add(new ValidatingSignal());

		for (ISignal s : list) {
			pipe.sendSignal(s);
		}

		List<ISignal> secondList = new ArrayList<ISignal>();
		while (true) {
			ISignal temp = pipe.getSignal();
			if (temp == null) {
				break;
			}
			secondList.add(temp);
		}
		Assert.assertEquals(list, secondList);
	}
}
