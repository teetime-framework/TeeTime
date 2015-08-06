/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://christianwulf.github.io/teetime)
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import teetime.framework.AbstractInterThreadPipe;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.signal.ISignal;
import teetime.framework.signal.StartingSignal;
import teetime.framework.signal.TerminatingSignal;
import teetime.framework.signal.ValidatingSignal;
import teetime.stage.basic.merger.Merger;

public class SpScPipeTest {

	// @Ignore
	// ignore as long as this test passes null ports to SpScPipe
	@Test
	public void testSignalOrdering() throws Exception {
		Merger<Object> portSource = new Merger<Object>();
		OutputPort<Object> sourcePort = portSource.getOutputPort();
		InputPort<Object> targetPort = portSource.getNewInputPort();
		AbstractInterThreadPipe pipe = new SpScPipe(sourcePort, targetPort, 1); // IPipe does not provide getSignal method

		List<ISignal> signals = new ArrayList<ISignal>();
		signals.add(new StartingSignal());
		signals.add(new TerminatingSignal());
		signals.add(new ValidatingSignal());
		signals.add(new StartingSignal());
		signals.add(new TerminatingSignal());
		signals.add(new ValidatingSignal());
		signals.add(new StartingSignal());
		signals.add(new TerminatingSignal());
		signals.add(new ValidatingSignal());

		for (ISignal s : signals) {
			pipe.sendSignal(s);
		}

		List<ISignal> secondSignals = new ArrayList<ISignal>();
		while (true) {
			ISignal temp = pipe.getSignal();
			if (temp == null) {
				break;
			}
			secondSignals.add(temp);
		}
		assertEquals(signals, secondSignals);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAdd() throws Exception {
		SpScPipe pipe = new SpScPipe(null, null, 4);
		assertFalse(pipe.add(null));
	}
}
