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
package teetime.stage.basic.merger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import teetime.framework.InputPort;
import teetime.framework.signal.StartingSignal;
import teetime.framework.signal.TerminatingSignal;

public class MergerSignalTest {

	private Merger<Integer> merger;
	private InputPort<Integer> firstPort;
	private InputPort<Integer> secondPort;
	private MergerTestingPipe testPipe;

	public void beforeSignalTesting() {
		merger = new Merger<Integer>();

		firstPort = merger.getNewInputPort();
		secondPort = merger.getNewInputPort();

		testPipe = new MergerTestingPipe();
		merger.getOutputPort().setPipe(testPipe);
	}

	@Test
	public void testSameSignal() {
		this.beforeSignalTesting();
		merger.onSignal(new StartingSignal(), firstPort);
		assertTrue(testPipe.startSent());
		testPipe.reset();
		merger.onSignal(new StartingSignal(), secondPort);
		assertFalse(testPipe.startSent());
	}

	@Test
	public void testDifferentSignals() {
		this.beforeSignalTesting();
		merger.onSignal(new StartingSignal(), firstPort);
		assertTrue(testPipe.startSent());
		testPipe.reset();

		merger.onSignal(new TerminatingSignal(), secondPort);
		assertFalse(testPipe.startSent());
	}

	@Test
	public void testInterleavedSignals() {
		this.beforeSignalTesting();
		merger.onSignal(new StartingSignal(), firstPort);
		assertTrue(testPipe.startSent());
		assertFalse(testPipe.terminateSent());
		testPipe.reset();

		merger.onSignal(new TerminatingSignal(), secondPort);
		assertFalse(testPipe.startSent());
		assertFalse(testPipe.terminateSent());
		testPipe.reset();

		merger.onSignal(new TerminatingSignal(), firstPort);
		assertFalse(testPipe.startSent());
		assertTrue(testPipe.terminateSent());
		testPipe.reset();

		merger.onSignal(new TerminatingSignal(), firstPort);
		assertFalse(testPipe.startSent());
		assertFalse(testPipe.terminateSent());
		testPipe.reset();

		merger.onSignal(new StartingSignal(), secondPort);
		assertFalse(testPipe.startSent());
		assertFalse(testPipe.terminateSent());
	}

	@Test
	public void testMultipleSignals() {
		this.beforeSignalTesting();
		merger.onSignal(new StartingSignal(), firstPort);
		assertTrue(testPipe.startSent());
		testPipe.reset();

		merger.onSignal(new StartingSignal(), firstPort);
		assertFalse(testPipe.startSent());
		testPipe.reset();

		merger.onSignal(new StartingSignal(), firstPort);
		assertFalse(testPipe.startSent());
		testPipe.reset();

		merger.onSignal(new StartingSignal(), secondPort);
		assertFalse(testPipe.startSent());
	}
}
