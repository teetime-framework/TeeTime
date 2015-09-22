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

import org.junit.Ignore;
import org.junit.Test;

import teetime.stage.Counter;
import teetime.stage.InitialElementProducer;
import teetime.stage.basic.Sink;

public class AbstractCompositeStageTest {

	@Ignore
	@Test
	public void testNestedStages() {
		Execution<NestedConf> exec = new Execution<NestedConf>(new NestedConf());
		// assertThat(exec.getConfiguration().getContext().getThreadableStages().size(), is(3));
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
