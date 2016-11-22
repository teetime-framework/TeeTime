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
package teetime.framework.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import teetime.framework.AbstractStage;
import teetime.framework.Configuration;
import teetime.framework.Execution;
import teetime.framework.ExecutionException;
import teetime.framework.StageState;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;

/**
 * This class can be used to test single stages in JUnit test cases.
 *
 * @author Nils Christian Ehmke
 */
public final class StageTester {

	private final List<InputHolder<?>> inputHolders = new ArrayList<InputHolder<?>>();
	private final List<OutputHolder<?>> outputHolders = new ArrayList<OutputHolder<?>>();
	private final AbstractStage stage;

	private StageTester(final AbstractStage stage) {
		this.stage = stage;
	}

	public static StageTester test(final AbstractStage stage) { // NOPMD
		if (stage.getCurrentState() != StageState.CREATED) {
			throw new AssertionError("This stage has already been tested in this test method. Move this test into a new test method.");
		}
		return new StageTester(stage);
	}

	/**
	 * @param elements
	 *            which serve as input. If nothing should be sent, pass
	 *
	 *            <pre>
	 * Collections.&lt;your type&gt;emptyList()
	 *            </pre>
	 *
	 * @return
	 */
	public <I> InputHolder<I> send(final I... elements) {
		return this.send(Arrays.asList(elements));
	}

	/**
	 * @param elements
	 *            which serve as input. If nothing should be sent, pass
	 *
	 *            <pre>
	 * Collections.&lt;your type&gt;emptyList()
	 *            </pre>
	 *
	 * @return
	 */
	public <I> InputHolder<I> send(final Collection<I> elements) {
		final InputHolder<I> inputHolder = new InputHolder<I>(this, stage, elements);
		this.inputHolders.add(inputHolder);
		return inputHolder;
	}

	public <O> OutputHolder<O> receive(final List<O> outputList) {
		final OutputHolder<O> outputHolder = new OutputHolder<O>(this, outputList);
		this.outputHolders.add(outputHolder);
		return outputHolder;
	}

	public StageTester and() {
		return this;
	}

	/**
	 * This method will start the test and block until it is finished.
	 *
	 * @throws ExecutionException
	 *             if at least one exception in one thread has occurred within the analysis.
	 *             The exception contains the pairs of thread and throwable.
	 *
	 */
	public void start() {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		final Configuration configuration = new TestConfiguration(inputHolders, stage, outputHolders);
		final Execution<Configuration> analysis = new Execution<Configuration>(configuration);
		analysis.executeBlocking();
	}

	private final class TestConfiguration<I> extends Configuration {
		@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
		public TestConfiguration(final List<InputHolder<I>> inputHolders, final AbstractStage stage, final List<OutputHolder<?>> outputHolders) {
			for (InputHolder<I> inputHolder : inputHolders) {
				final InitialElementProducer<I> producer = new InitialElementProducer<I>(inputHolder.getInput());
				connectPorts(producer.getOutputPort(), inputHolder.getPort());
			}

			stage.declareActive();

			for (OutputHolder<?> outputHolder : outputHolders) {
				final CollectorSink<Object> sink = new CollectorSink<Object>(outputHolder.getOutputElements());
				connectPorts(outputHolder.getPort(), sink.getInputPort());
			}
		}
	}

}
