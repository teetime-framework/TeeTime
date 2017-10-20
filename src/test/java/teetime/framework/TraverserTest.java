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

import org.junit.Test;

import teetime.framework.Traverser.VisitorBehavior;
import teetime.framework.pipe.DummyPipe;
import teetime.stage.Counter;
import teetime.stage.basic.Sink;

public class TraverserTest {

	private static class DoNothingVisitor implements ITraverserVisitor {

		@Override
		public VisitorBehavior visit(final AbstractStage stage) {
			return VisitorBehavior.CONTINUE_FORWARD;
		}

		@Override
		public VisitorBehavior visit(final AbstractPort<?> port) {
			return VisitorBehavior.CONTINUE_FORWARD;
		}

		@Override
		public void visit(final DummyPipe pipe, final AbstractPort<?> port) {
			// do nothing
		}

	}

	@Test
	public void testCreatedStage() throws Exception {
		TestConfiguration config = new TestConfiguration();
		// config.init.onValidating(Collections.<InvalidPortConnection>emptyList());
		// new Execution<TestConfiguration>(config); // validates

		Traverser traverser = new Traverser(new DoNothingVisitor());
		traverser.traverse(config.init);

		assertThat(traverser.getVisitedStages().size(), is(3 + 2 * 3 + 2));
	}

	@Test
	public void testValidatedStage() throws Exception {
		TestConfiguration config = new TestConfiguration();
		// config.init.onValidating(Collections.<InvalidPortConnection>emptyList());
		new Execution<TestConfiguration>(config); // validates

		Traverser traverser = new Traverser(new DoNothingVisitor());
		traverser.traverse(config.init);

		assertThat(traverser.getVisitedStages().size(), is(3 + 2 * 3 + 2));
	}

	@Test
	public void testStartedStage() throws Exception {
		TestConfiguration config = new TestConfiguration();
		// config.init.onValidating(Collections.<InvalidPortConnection>emptyList());
		new Execution<TestConfiguration>(config); // validates and sets owning thread of each stage
		config.init.onStarting(); // requires a non-null owning thread

		Traverser traverser = new Traverser(new DoNothingVisitor());
		traverser.traverse(config.init);

		assertThat(traverser.getVisitedStages(), is(empty()));
	}

	@Test
	public void testTerminatingStage() throws Exception {
		TestConfiguration config = new TestConfiguration();
		// config.init.onValidating(Collections.<InvalidPortConnection>emptyList());
		new Execution<TestConfiguration>(config); // validates and sets owning thread of each stage
		config.init.onStarting(); // requires a non-null owning thread
		config.init.terminateStage();

		Traverser traverser = new Traverser(new DoNothingVisitor());
		traverser.traverse(config.init);

		assertThat(traverser.getVisitedStages(), is(empty()));
	}

	@Test
	public void testTerminatedStage() throws Exception {
		TestConfiguration config = new TestConfiguration();
		// config.init.onValidating(Collections.<InvalidPortConnection>emptyList());
		new Execution<TestConfiguration>(config); // validates and sets owning thread of each stage
		config.init.onStarting(); // requires a non-null owning thread
		config.init.onTerminating();

		Traverser traverser = new Traverser(new DoNothingVisitor());
		traverser.traverse(config.init);

		assertThat(traverser.getVisitedStages(), is(empty()));
	}

	@Test(expected = IllegalStateException.class)
	public void unconnectedInputPortShouldThrowException() throws Exception {
		UnconnectedInputPortConfig config = new UnconnectedInputPortConfig();
		new Execution<Configuration>(config);
	}

	private static class UnconnectedInputPortConfig extends Configuration {
		public UnconnectedInputPortConfig() {
			Counter<Object> counter = new Counter<Object>();
			Sink<Object> sink = new Sink<Object>();
			connectPorts(counter.getOutputPort(), sink.getInputPort());
		}
	}

}
