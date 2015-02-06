/**
 * Copyright (C) 2015 TeeTime (http://teetime.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.framework.pipe;

import java.util.ArrayList;
import java.util.List;

import teetime.framework.AbstractInterThreadPipe;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.signal.ISignal;
import teetime.framework.signal.StartingSignal;
import teetime.framework.signal.TerminatingSignal;
import teetime.framework.signal.ValidatingSignal;

public class SpScPipeTest {

	// @Ignore
	// ignore as long as this test passes null ports to SpScPipe
	// @Test
	public void testSignalOrdering() throws Exception {
		OutputPort<Object> sourcePort = null;
		InputPort<Object> targetPort = null;
		AbstractInterThreadPipe pipe = new SpScPipe(sourcePort, targetPort, 1); // IPipe does not provide getSignal method

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
		// Assert.assertEquals(list, secondList);
	}
}
