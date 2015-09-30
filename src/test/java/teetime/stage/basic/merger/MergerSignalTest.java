/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
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
package teetime.stage.basic.merger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import teetime.framework.InputPort;
import teetime.framework.signal.StartingSignal;
import teetime.framework.signal.TerminatingSignal;

public class MergerSignalTest {

	private Merger<Integer> merger;
	private InputPort<Integer> firstPort;
	private InputPort<Integer> secondPort;
	private MergerTestingPipe mergerOutputPipe;

	@Before
	public void beforeSignalTesting() {
		merger = new Merger<Integer>();

		firstPort = merger.getNewInputPort();
		secondPort = merger.getNewInputPort();

		mergerOutputPipe = new MergerTestingPipe();
		merger.getOutputPort().setPipe(mergerOutputPipe);
	}

	@Test
	public void testSameSignal() {
		merger.onSignal(new StartingSignal(), firstPort);
		assertTrue(mergerOutputPipe.startSent());
		mergerOutputPipe.reset();
		merger.onSignal(new StartingSignal(), secondPort);
		assertFalse(mergerOutputPipe.startSent());
	}

	@Test
	public void testDifferentSignals() {
		merger.onSignal(new StartingSignal(), firstPort);
		assertTrue(mergerOutputPipe.startSent());
		mergerOutputPipe.reset();

		merger.onSignal(new TerminatingSignal(), secondPort);
		assertFalse(mergerOutputPipe.startSent());
	}

	@Test
	public void testInterleavedSignals() {
		merger.onSignal(new StartingSignal(), firstPort);
		assertTrue(mergerOutputPipe.startSent());
		assertFalse(mergerOutputPipe.terminateSent());
		mergerOutputPipe.reset();

		merger.onSignal(new TerminatingSignal(), secondPort);
		assertFalse(mergerOutputPipe.startSent());
		assertFalse(mergerOutputPipe.terminateSent());
		mergerOutputPipe.reset();

		merger.onSignal(new TerminatingSignal(), firstPort);
		assertFalse(mergerOutputPipe.startSent());
		assertTrue(mergerOutputPipe.terminateSent());
		mergerOutputPipe.reset();

		merger.onSignal(new TerminatingSignal(), firstPort);
		assertFalse(mergerOutputPipe.startSent());
		assertFalse(mergerOutputPipe.terminateSent());
		mergerOutputPipe.reset();

		merger.onSignal(new StartingSignal(), secondPort);
		assertFalse(mergerOutputPipe.startSent());
		assertFalse(mergerOutputPipe.terminateSent());
	}

	@Test
	public void testMultipleSignals() {
		merger.onSignal(new StartingSignal(), firstPort);
		assertTrue(mergerOutputPipe.startSent());
		mergerOutputPipe.reset();

		merger.onSignal(new StartingSignal(), firstPort);
		assertFalse(mergerOutputPipe.startSent());
		mergerOutputPipe.reset();

		merger.onSignal(new StartingSignal(), firstPort);
		assertFalse(mergerOutputPipe.startSent());
		mergerOutputPipe.reset();

		merger.onSignal(new StartingSignal(), secondPort);
		assertFalse(mergerOutputPipe.startSent());
	}
}
