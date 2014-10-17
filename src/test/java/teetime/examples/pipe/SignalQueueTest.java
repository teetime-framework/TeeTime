package teetime.examples.pipe;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import teetime.framework.pipe.SpScPipe;
import teetime.framework.signal.ISignal;
import teetime.framework.signal.StartingSignal;
import teetime.framework.signal.TerminatingSignal;
import teetime.framework.signal.ValidatingSignal;

public class SignalQueueTest {

	@Test
	public void executeTest() {
		ArrayList<ISignal> list = new ArrayList<ISignal>();
		list.add(new StartingSignal());
		list.add(new TerminatingSignal());
		list.add(new ValidatingSignal());
		list.add(new StartingSignal());
		list.add(new TerminatingSignal());
		list.add(new ValidatingSignal());
		list.add(new StartingSignal());
		list.add(new TerminatingSignal());
		list.add(new ValidatingSignal());

		SpScPipe pipe = new SignalQueueConfiguration().pipe;
		for (ISignal s : list) {
			pipe.sendSignal(s);
		}

		ArrayList<ISignal> secondList = new ArrayList<ISignal>();
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
