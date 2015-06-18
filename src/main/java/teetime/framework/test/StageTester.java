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
package teetime.framework.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import teetime.framework.Analysis;
import teetime.framework.AnalysisContext;
import teetime.framework.AnalysisException;
import teetime.framework.Stage;
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
	private final Stage stage;

	private StageTester(final Stage stage) {
		this.stage = stage;
	}

	public static StageTester test(final Stage stage) {
		if (stage.getCurrentState() != StageState.CREATED) {
			throw new AssertionError("This stage has already been tested in this test method. Move this test into a new test method.");
		}
		return new StageTester(stage);
	}

	public <I> InputHolder<I> send(final Iterable<I> input) {
		final InputHolder<I> inputHolder = new InputHolder<I>(this, stage, input);
		this.inputHolders.add(inputHolder);
		return inputHolder;
	}

	public <I> InputHolder<I> send(final I... input) {
		return this.send(Arrays.asList(input));
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
	 * @throws AnalysisException
	 *             if at least one exception in one thread has occurred within the analysis.
	 *             The exception contains the pairs of thread and throwable.
	 *
	 */
	public void start() {
		final AnalysisContext configuration = new Configuration(inputHolders, stage, outputHolders);
		final Analysis<AnalysisContext> analysis = new Analysis<AnalysisContext>(configuration);
		analysis.executeBlocking();
	}

	private final class Configuration extends AnalysisContext {

		public Configuration(final List<InputHolder<?>> inputHolders, final Stage stage, final List<OutputHolder<?>> outputHolders) {
			for (InputHolder<?> inputHolder : inputHolders) {
				final InitialElementProducer<Object> producer = new InitialElementProducer<Object>(inputHolder.getInput());
				connectPorts(producer.getOutputPort(), inputHolder.getPort());
				addThreadableStage(producer);
			}

			addThreadableStage(stage);

			for (OutputHolder<?> outputHolder : outputHolders) {
				final CollectorSink<Object> sink = new CollectorSink<Object>(outputHolder.getOutputElements());
				connectPorts(outputHolder.getPort(), sink.getInputPort());
			}
		}
	}

}
