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
package teetime.framework;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import teetime.stage.Counter;
import teetime.stage.InitialElementProducer;
import teetime.stage.basic.Sink;
import teetime.testutil.AssertHelper;

/**
 * Tests whether
 * <ul>
 * <li>setting a stage active within a composite stage works
 * <li>creating a producer within a composite state works
 * <li>creating and connecting two stages within a composite stage works
 * <li>flattening of a composite stage works
 * </ul>
 *
 * @author Christian Wulf
 *
 */
public class AbstractCompositeStageTest {

	@Before
	public void before() {
		AbstractStage.clearInstanceCounters(); // resets the id to zero
	}

	@Test
	public void ensureCorrectNumberOfActiveStages() {
		Execution<NestedConf> exec = new Execution<NestedConf>(new NestedConf());
		assertThat(exec.getConfiguration().getContext().getThreadableStages().size(), is(3));
	}

	@Test
	public void ensureFlatteningDepth1AtRuntime() {
		CounterContainer element = new CounterContainer();
		Execution<CompositeCounterPipelineConfig> execution = new Execution<CompositeCounterPipelineConfig>(new CompositeCounterPipelineConfig(1, element));

		InitialElementProducer<CounterContainer> producer;
		CounterIncrementer stage;

		producer = assertFirstStage(execution);
		stage = assertSecondStage(producer);
		assertLastStage(stage);
	}

	@Test
	public void ensureFlatteningDepth2AtRuntime() {
		CounterContainer element = new CounterContainer();
		Execution<CompositeCounterPipelineConfig> execution = new Execution<CompositeCounterPipelineConfig>(new CompositeCounterPipelineConfig(2, element));

		InitialElementProducer<CounterContainer> producer;
		CounterIncrementer stage;

		producer = assertFirstStage(execution);
		stage = assertSecondStage(producer);
		stage = assertThirdStage(stage);
		assertLastStage(stage);
	}

	private InitialElementProducer<CounterContainer> assertFirstStage(final Execution<CompositeCounterPipelineConfig> execution) {
		InitialElementProducer<CounterContainer> producer = execution.getConfiguration().getProducer();
		assertThat(producer.getId(), is(equalTo("InitialElementProducer-0")));
		return producer;
	}

	private CounterIncrementer assertSecondStage(final InitialElementProducer<CounterContainer> producer) {
		AbstractStage nextStage = producer.getOutputPort().getPipe().getTargetPort().getOwningStage();
		CounterIncrementer stage = AssertHelper.assertInstanceOf(CounterIncrementer.class, nextStage);
		assertThat(stage.getId(), is(equalTo("CounterIncrementer-0")));
		return stage;
	}

	private CounterIncrementer assertThirdStage(CounterIncrementer stage) {
		AbstractStage nextStage = stage.getOutputPort().getPipe().getTargetPort().getOwningStage();
		stage = AssertHelper.assertInstanceOf(CounterIncrementer.class, nextStage);
		assertThat(stage.getId(), is(equalTo("CounterIncrementer-1")));
		return stage;
	}

	private void assertLastStage(final CounterIncrementer stage) {
		AbstractStage nextStage;
		nextStage = stage.getOutputPort().getPipe().getTargetPort().getOwningStage();
		Sink<?> sink = AssertHelper.assertInstanceOf(Sink.class, nextStage);
		assertThat(sink.getId(), is(equalTo("Sink-0")));
	}

	private class NestedConf extends Configuration {

		private final InitialElementProducer<Object> init;
		private final Sink<Object> sink;
		private final TestNestingCompositeStage compositeStage;

		public NestedConf() {
			init = new InitialElementProducer<Object>(new Object());
			sink = new Sink<Object>();
			compositeStage = new TestNestingCompositeStage();
			connectPorts(init.getOutputPort(), compositeStage.firstCompositeStage.firstCounter.getInputPort());
			connectPorts(compositeStage.secondCompositeStage.secondCounter.getOutputPort(), sink.getInputPort());

		}
	}

	private class TestCompositeOneStage extends AbstractCompositeStage {

		private final Counter firstCounter = new Counter();

		public TestCompositeOneStage() {
			firstCounter.declareActive();
		}

	}

	private class TestCompositeTwoStage extends AbstractCompositeStage {

		private final Counter firstCounter = new Counter();
		private final Counter secondCounter = new Counter();

		public TestCompositeTwoStage() {
			firstCounter.declareActive();
			connectPorts(firstCounter.getOutputPort(), secondCounter.getInputPort());
		}

	}

	private class TestNestingCompositeStage extends AbstractCompositeStage {

		public TestCompositeOneStage firstCompositeStage;
		public TestCompositeTwoStage secondCompositeStage;

		public TestNestingCompositeStage() {
			firstCompositeStage = new TestCompositeOneStage();
			secondCompositeStage = new TestCompositeTwoStage();
			connectPorts(firstCompositeStage.firstCounter.getOutputPort(), secondCompositeStage.firstCounter.getInputPort());
		}

	}

}
