/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime.sourceforge.net)
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
package teetime.framework;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import teetime.stage.InitialElementProducer;
import teetime.stage.InstanceOfFilter;
import teetime.stage.basic.Sink;
import teetime.util.StopWatch;

public class AnalysisTest {

	private static final long DELAY_IN_MS = 500;
	private static final long ABSOLUTE_MAX_ERROR_IN_MS = 2;

	private Analysis<TestConfig> analysis;

	@Before
	public void before() {
		TestConfig configuration = new TestConfig();
		analysis = new Analysis<TestConfig>(configuration);
	}

	@Test
	public void testExecuteNonBlocking() throws Exception {
		StopWatch watch = new StopWatch();
		watch.start();
		analysis.executeNonBlocking();
		watch.end();

		assertThat(watch.getDurationInMs(), is(lessThan(DELAY_IN_MS)));
		assertFalse(analysis.getConfiguration().delay.finished);

		analysis.waitForTermination();
		assertTrue(analysis.getConfiguration().delay.finished);
	}

	@Test
	public void testExecuteBlocking() {
		StopWatch watch = new StopWatch();
		watch.start();
		analysis.executeBlocking();
		watch.end();

		assertThat(watch.getDurationInMs() + ABSOLUTE_MAX_ERROR_IN_MS, is(greaterThanOrEqualTo(DELAY_IN_MS)));
	}

	private static class TestConfig extends ConfigurationContext {
		public final DelayAndTerminate delay;

		public TestConfig() {
			final InitialElementProducer<String> init = new InitialElementProducer<String>("Hello");
			delay = new DelayAndTerminate(DELAY_IN_MS);
			connectPorts(init.getOutputPort(), delay.getInputPort());
			addThreadableStage(init);
		}
	}

	private static class DelayAndTerminate extends AbstractConsumerStage<String> {

		private final long delayInMs;

		public boolean finished;

		public DelayAndTerminate(final long delayInMs) {
			super();
			this.delayInMs = delayInMs;
		}

		@Override
		protected void execute(final String element) {
			try {
				Thread.sleep(delayInMs);
			} catch (InterruptedException e) {
			}
			finished = true;
		}

	}

	@Test
	public void testInstantiatePipes() throws Exception {
		Analysis<AnalysisTestConfig> interAnalysis = new Analysis<AnalysisTestConfig>(new AnalysisTestConfig(true));
		assertThat(interAnalysis.getConfiguration().init.getOwningThread(), is(not(interAnalysis.getConfiguration().sink.getOwningThread())));

		Analysis<AnalysisTestConfig> intraAnalysis = new Analysis<AnalysisTestConfig>(new AnalysisTestConfig(false));
		assertThat(intraAnalysis.getConfiguration().init.getOwningThread(), is(intraAnalysis.getConfiguration().sink.getOwningThread()));
	}

	private class AnalysisTestConfig extends ConfigurationContext {
		public InitialElementProducer<Object> init = new InitialElementProducer<Object>();
		public Sink<Object> sink = new Sink<Object>();

		public AnalysisTestConfig(final boolean inter) {
			connectPorts(init.getOutputPort(), sink.getInputPort());
			addThreadableStage(init);
			if (inter) {
				addThreadableStage(sink);
			}
		}
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testInstantiatePipesIncorrectConfiguration() {
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Crossing threads");
		InvalidTestConfig configuration = new InvalidTestConfig();
		new Analysis<InvalidTestConfig>(configuration);
	}

	private class InvalidTestConfig extends ConfigurationContext {
		public InitialElementProducer<Object> init = new InitialElementProducer<Object>();
		public InstanceOfFilter<Object, Object> iof = new InstanceOfFilter<Object, Object>(Object.class);
		public Sink<Object> sink = new Sink<Object>();

		public InvalidTestConfig() {
			connectPorts(init.getOutputPort(), iof.getInputPort());
			connectPorts(iof.getMatchedOutputPort(), sink.getInputPort());
			connectPorts(init.createOutputPort(), sink.createInputPort());
			addThreadableStage(init);
			addThreadableStage(iof);
		}
	}

	@Test
	public void automaticallyAddHeadStages() {
		AutomaticallyConfig context = new AutomaticallyConfig();
		new Analysis<ConfigurationContext>(context).executeBlocking();
		assertTrue(context.executed);
	}

	private class AutomaticallyConfig extends ConfigurationContext {

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
				terminate();
			}

		}

	}

}
