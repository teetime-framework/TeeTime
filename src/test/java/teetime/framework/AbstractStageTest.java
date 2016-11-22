/**
 * Copyright © 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.framework;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import teetime.framework.signal.StartingSignal;
import teetime.framework.validation.AnalysisNotValidException;
import teetime.stage.Cache;
import teetime.stage.Counter;
import teetime.stage.InitialElementProducer;
import teetime.stage.basic.Sink;
import teetime.stage.basic.merger.Merger;

public class AbstractStageTest {

	@Test
	public void testId() {
		AbstractStage.clearInstanceCounters();

		Counter<Object> counter0 = new Counter<Object>();
		Counter<Object> counter1 = new Counter<Object>();
		Assert.assertEquals("Counter-0", counter0.getId());
		Assert.assertEquals("Counter-1", counter1.getId());

		for (int i = 0; i < 100; i++) {
			Cache<Object> cache = new Cache<Object>();
			Assert.assertEquals("Cache-" + i, cache.getId());
		}
	}

	@Test
	public void testSetOwningThread() throws Exception {
		TestConfig tc = new TestConfig();
		new Execution<TestConfig>(tc);
		assertEquals(tc.init.getOwningThread(), tc.delay.getOwningThread());
		assertThat(tc.delay.getExceptionListener(), is(notNullValue()));
		assertEquals(tc.init.getExceptionListener(), tc.delay.getExceptionListener());
	}

	@Test
	public void testActiveFlag() {
		TestConfig config = new TestConfig();
		assertFalse(config.init.isActive());
		assertFalse(config.delay.isActive());

		new Execution<Configuration>(config);
		assertTrue(config.init.isActive());
		assertFalse(config.delay.isActive());
	}

	private static class TestConfig extends Configuration {
		public final DelayAndTerminate delay;
		public InitialElementProducer<String> init;

		public TestConfig() {
			init = new InitialElementProducer<String>("Hello");
			delay = new DelayAndTerminate(0);
			connectPorts(init.getOutputPort(), delay.getInputPort());
		}
	}

	private static class DelayAndTerminate extends AbstractConsumerStage<String> {

		private final long delayInMs;

		public DelayAndTerminate(final long delayInMs) {
			super();
			this.delayInMs = delayInMs;
		}

		@Override
		protected void execute(final String element) {
			try {
				Thread.sleep(delayInMs);
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
		}

	}

	@Test(expected = AnalysisNotValidException.class)
	public void testCheckTypeCompliance() throws Exception {
		try {
			// Correct connection
			new Execution<Configuration>(new TestConnectionsConfig(false), true).executeBlocking();
		} catch (AnalysisNotValidException e) {
			fail();
		}
		// Incorrect connection should fail!
		new Execution<Configuration>(new TestConnectionsConfig(true), true).executeBlocking();
	}

	private class TestConnectionsConfig extends Configuration {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		TestConnectionsConfig(final boolean fails) {
			EmptyStage stage = new EmptyStage();
			if (fails) {
				connectPorts((OutputPort) new EmptyStage().createOutputPort(Object.class), new EmptyStage().createInputPort(Integer.class));
			} else {
				connectPorts(stage.createOutputPort(Integer.class), new EmptyStage().createInputPort(Object.class));
			}
			stage.declareActive();
		}

	}

	private class EmptyStage extends AbstractStage {

		@Override
		protected void execute() {
			terminateStage();
		}
	}
	//
	//
	// Moved from MergerSignalTest
	//
	//

	private Merger<Integer> arbitraryStage;
	private InputPort<Integer> firstPort;
	private InputPort<Integer> secondPort;

	private static class ConfigToTestSignalHandling extends Configuration {

		private final Sink<Integer> sink;

		public ConfigToTestSignalHandling(final Merger<Integer> arbitraryStage, final InputPort<Integer> firstPort, final InputPort<Integer> secondPort) {
			InitialElementProducer<Integer> firstProducer = new InitialElementProducer<Integer>();
			InitialElementProducer<Integer> secondProducer = new InitialElementProducer<Integer>();
			sink = new Sink<Integer>();

			connectPorts(firstProducer.getOutputPort(), firstPort);
			connectPorts(secondProducer.getOutputPort(), secondPort);
			connectPorts(arbitraryStage.getOutputPort(), sink.getInputPort());

			arbitraryStage.declareActive();
		}

		public Sink<Integer> getSink() {
			return sink;
		}
	}

	@Before
	public void beforeSignalTesting() {
		arbitraryStage = new Merger<Integer>();
		firstPort = arbitraryStage.getNewInputPort();
		secondPort = arbitraryStage.getNewInputPort();
	}

	private Sink<Integer> resetSinkStatus() {
		ConfigToTestSignalHandling config = new ConfigToTestSignalHandling(arbitraryStage, firstPort, secondPort);
		new Execution<ConfigToTestSignalHandling>(config); // necessary to initialize the owning thread for onStarting()
		return config.getSink();
	}

	// FIXME make the tests work again

	@Test
	@Ignore
	public void testSameSignal() {
		Sink<Integer> sink;

		sink = resetSinkStatus();
		((AbstractStage) arbitraryStage).onSignal(new StartingSignal(), firstPort);
		// assertTrue(mergerOutputPipe.startSent());
		assertEquals(sink.getCurrentState(), StageState.STARTED);

		sink = resetSinkStatus();
		((AbstractStage) arbitraryStage).onSignal(new StartingSignal(), secondPort);
		// assertFalse(mergerOutputPipe.startSent());
		// assertEquals(sink.getCurrentState(), StageState.STARTED);
	}

	// @Test
	// public void testDifferentSignals() {
	// ((AbstractStage) arbitraryStage).onSignal(new StartingSignal(), firstPort);
	// assertTrue(mergerOutputPipe.startSent());
	// mergerOutputPipe.reset();
	//
	// ((AbstractStage) arbitraryStage).onSignal(new TerminatingSignal(), secondPort);
	// assertFalse(mergerOutputPipe.startSent());
	// }
	//
	// @Test
	// public void testInterleavedSignals() {
	// ((AbstractStage) arbitraryStage).onSignal(new StartingSignal(), firstPort);
	// assertTrue(mergerOutputPipe.startSent());
	// assertFalse(mergerOutputPipe.terminateSent());
	// mergerOutputPipe.reset();
	//
	// ((AbstractStage) arbitraryStage).onSignal(new TerminatingSignal(), secondPort);
	// assertFalse(mergerOutputPipe.startSent());
	// assertFalse(mergerOutputPipe.terminateSent());
	// mergerOutputPipe.reset();
	//
	// ((AbstractStage) arbitraryStage).onSignal(new TerminatingSignal(), firstPort);
	// assertFalse(mergerOutputPipe.startSent());
	// assertTrue(mergerOutputPipe.terminateSent());
	// mergerOutputPipe.reset();
	//
	// ((AbstractStage) arbitraryStage).onSignal(new TerminatingSignal(), firstPort);
	// assertFalse(mergerOutputPipe.startSent());
	// assertFalse(mergerOutputPipe.terminateSent());
	// mergerOutputPipe.reset();
	//
	// ((AbstractStage) arbitraryStage).onSignal(new StartingSignal(), secondPort);
	// assertFalse(mergerOutputPipe.startSent());
	// assertFalse(mergerOutputPipe.terminateSent());
	// }
	//
	// @Test
	// public void testMultipleSignals() {
	// ((AbstractStage) arbitraryStage).onSignal(new StartingSignal(), firstPort);
	// assertTrue(mergerOutputPipe.startSent());
	// mergerOutputPipe.reset();
	//
	// ((AbstractStage) arbitraryStage).onSignal(new StartingSignal(), firstPort);
	// assertFalse(mergerOutputPipe.startSent());
	// mergerOutputPipe.reset();
	//
	// ((AbstractStage) arbitraryStage).onSignal(new StartingSignal(), firstPort);
	// assertFalse(mergerOutputPipe.startSent());
	// mergerOutputPipe.reset();
	//
	// ((AbstractStage) arbitraryStage).onSignal(new StartingSignal(), secondPort);
	// assertFalse(mergerOutputPipe.startSent());
	// }

}
