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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import teetime.stage.InitialElementProducer;
import teetime.stage.InstanceOfFilter;
import teetime.stage.basic.Sink;
import teetime.stage.basic.merger.Merger;
import teetime.util.StopWatch;

public class ExecutionTest {

	private static final long DELAY_IN_MS = 500;
	private static final long ABSOLUTE_MAX_ERROR_IN_MS = 15; // handle timer granularity

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private Execution<TestConfig> execution;

	@Before
	public void before() {
		TestConfig configuration = new TestConfig();
		execution = new Execution<TestConfig>(configuration);
	}

	@Test
	public void testExecuteNonBlocking() throws Exception {
		StopWatch watch = new StopWatch();
		watch.start();
		execution.executeNonBlocking();
		watch.end();

		assertThat(watch.getDurationInMs(), is(lessThan(DELAY_IN_MS)));
		assertFalse(execution.getConfiguration().delay.finished);

		execution.waitForTermination();
		assertTrue(execution.getConfiguration().delay.finished);
	}

	@Test
	public void testExecuteNonBlockingWithFuture() throws Exception {
		StopWatch watch = new StopWatch();
		watch.start();
		Future<Void> future = execution.executeNonBlocking();
		watch.end();

		assertThat(watch.getDurationInMs(), is(lessThan(DELAY_IN_MS)));
		assertFalse(execution.getConfiguration().delay.finished);

		future.get();
		assertThat(future.isDone(), is(true));
		assertThat(future.isCancelled(), is(false));
		assertTrue(execution.getConfiguration().delay.finished);

		assertThat(future.cancel(true), is(false));
	}

	@Test
	public void testExecuteBlocking() {
		StopWatch watch = new StopWatch();
		watch.start();
		execution.executeBlocking();
		watch.end();

		assertThat(watch.getDurationInMs(), is(greaterThanOrEqualTo(DELAY_IN_MS - ABSOLUTE_MAX_ERROR_IN_MS)));
	}

	private static class TestConfig extends Configuration {
		public final DelayAndTerminate delay;

		public TestConfig() {
			final InitialElementProducer<String> init = new InitialElementProducer<String>("Hello");
			delay = new DelayAndTerminate(DELAY_IN_MS);
			connectPorts(init.getOutputPort(), delay.getInputPort());
		}
	}

	private static class DelayAndTerminate extends AbstractConsumerStage<String> {

		public boolean finished;

		private final long delayInMs;

		public DelayAndTerminate(final long delayInMs) {
			super();
			this.delayInMs = delayInMs;
		}

		@Override
		protected void execute(final String element) throws InterruptedException {
			Thread.sleep(delayInMs);
			finished = true;
		}

	}

	@Test
	public void testInstantiatePipes() throws Exception {
		Execution<AnalysisTestConfig> interAnalysis = new Execution<AnalysisTestConfig>(new AnalysisTestConfig(true));
		assertThat(interAnalysis.getConfiguration().init.getOwningThread(), is(not(interAnalysis.getConfiguration().sink.getOwningThread())));

		Execution<AnalysisTestConfig> intraAnalysis = new Execution<AnalysisTestConfig>(new AnalysisTestConfig(false));
		assertThat(intraAnalysis.getConfiguration().init.getOwningThread(), is(intraAnalysis.getConfiguration().sink.getOwningThread()));
	}

	private class AnalysisTestConfig extends Configuration {
		public InitialElementProducer<Object> init = new InitialElementProducer<Object>();
		public Sink<Object> sink = new Sink<Object>();

		public AnalysisTestConfig(final boolean inter) {
			connectPorts(init.getOutputPort(), sink.getInputPort());
			if (inter) {
				sink.declareActive();
			}
		}
	}

	@Test
	public void testInstantiatePipesIncorrectConfiguration() {
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Crossing threads");
		InvalidTestConfig configuration = new InvalidTestConfig();
		new Execution<InvalidTestConfig>(configuration);
	}

	private static class InvalidTestConfig extends Configuration {
		private final InitialElementProducer<Object> init = new InitialElementProducer<Object>();
		private final InstanceOfFilter<Object, Object> iof = new InstanceOfFilter<Object, Object>(Object.class);
		private final Merger<Object> merger = new Merger<Object>();

		public InvalidTestConfig() {
			connectPorts(init.getOutputPort(), iof.getInputPort());
			connectPorts(iof.getMatchedOutputPort(), merger.createInputPort());
			connectPorts(init.createOutputPort(), merger.createInputPort());
			iof.declareActive();
		}
	}

	@Test
	public void automaticallyAddHeadStages() {
		AutomaticallyConfig context = new AutomaticallyConfig();
		new Execution<Configuration>(context).executeBlocking();
		assertTrue(context.executed);
	}

	private class AutomaticallyConfig extends Configuration {

		public boolean executed;

		public AutomaticallyConfig() {
			AutomaticallyAddedStage aas = new AutomaticallyAddedStage();
			Sink<Object> sink = new Sink<Object>();
			connectPorts(aas.getOutputPort(), sink.getInputPort());
		}

		private class AutomaticallyAddedStage extends AbstractProducerStage<Object> {

			@Override
			protected void execute() {
				executed = true;
				terminateStage();
			}
		}
	}

	@Test
	public void threadNameing() {
		NameConfig configuration = new NameConfig();
		new Execution<NameConfig>(configuration); // do not execute, but just initialize the execution

		String actualThreadName = configuration.stageWithNamedThread.getOwningThread().getName();
		String expectedThreadName = "Thread for " + configuration.stageWithNamedThread.getId();
		assertThat(actualThreadName, is(expectedThreadName));
	}

	private class NameConfig extends Configuration {

		InitialElementProducer<Object> stageWithNamedThread;

		public NameConfig() {
			stageWithNamedThread = new InitialElementProducer<Object>(new Object());
			Sink<Object> sink = new Sink<Object>();

			stageWithNamedThread.declareActive();

			connectPorts(stageWithNamedThread.getOutputPort(), sink.getInputPort());
		}

	}

	@Test(expected = IllegalStateException.class)
	public void initializeConfigOnlyOnce() {
		NameConfig configuration = new NameConfig();
		new Execution<NameConfig>(configuration);
		new Execution<NameConfig>(configuration); // do not execute, but just initialize the execution
	}

	@Test(expected = IllegalStateException.class)
	public void executeConfigOnlyOnce() {
		NameConfig configuration = new NameConfig();
		Execution<NameConfig> execution = new Execution<NameConfig>(configuration);
		execution.executeNonBlocking();
		execution.executeNonBlocking();
	}

	@Test
	public void mainMethod() {
		assertThat(MainMethodTestConfig.executed, is(false));
		Execution.main("teetime.framework.MainMethodTestConfig");
		assertThat(MainMethodTestConfig.executed, is(true));
	}

	@Test
	public void testDifferentOwningThreads() {
		TestConfiguration config = new TestConfiguration();
		new Execution<TestConfiguration>(config); // sets owning thread for each stage

		Thread wordCounterThread = config.distributor.getOutputPorts().get(0).pipe.getTargetPort().getOwningStage().getOwningThread();
		assertThat(config.distributor.getOwningThread(), is(not(wordCounterThread)));
	}

}
