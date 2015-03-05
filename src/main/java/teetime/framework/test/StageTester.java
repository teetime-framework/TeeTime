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
package teetime.framework.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import teetime.framework.Analysis;
import teetime.framework.AnalysisConfiguration;
import teetime.framework.Stage;
import teetime.framework.StageState;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.stage.CollectorSink;
import teetime.stage.IterableProducer;
import teetime.util.Pair;

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

	public Collection<Pair<Thread, Throwable>> start() {
		final AnalysisConfiguration configuration = new Configuration(inputHolders, stage, outputHolders);
		final Analysis analysis = new Analysis(configuration);
		return analysis.start();
	}

	private final class Configuration extends AnalysisConfiguration {

		public Configuration(final List<InputHolder<?>> inputHolders, final Stage stage, final List<OutputHolder<?>> outputHolders) {
			final IPipeFactory interPipeFactory = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTER, PipeOrdering.QUEUE_BASED, false);
			for (InputHolder<?> inputHolder : inputHolders) {
				final IterableProducer<Object> producer = new IterableProducer<Object>(inputHolder.getInput());
				interPipeFactory.create(producer.getOutputPort(), inputHolder.getPort());
				addThreadableStage(producer);
			}

			addThreadableStage(stage);

			final IPipeFactory intraPipeFactory = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);
			for (OutputHolder<?> outputHolder : outputHolders) {
				final CollectorSink<Object> sink = new CollectorSink<Object>(outputHolder.getOutputElements());
				intraPipeFactory.create(outputHolder.getPort(), sink.getInputPort());
			}
		}

	}

}
