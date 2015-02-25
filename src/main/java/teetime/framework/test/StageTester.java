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
import java.util.Collections;
import java.util.List;

import teetime.framework.Analysis;
import teetime.framework.AnalysisConfiguration;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.Stage;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.stage.CollectorSink;
import teetime.stage.IterableProducer;

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
		return new StageTester(stage);
	}

	public <I> InputHolder<I> send(final Iterable<I> input) {
		InputHolder<I> inputHolder = new InputHolder<I>(input);
		this.inputHolders.add(inputHolder);
		return inputHolder;
	}

	public <I> InputHolder<I> send(final I... input) {
		return this.send(Arrays.asList(input));
	}

	public <I> InputHolder<I> send(final I input) {
		return this.send(Collections.singletonList(input));
	}

	public <O> OutputHolder<O> receive(final List<O> output) {
		OutputHolder<O> outputHolder = new OutputHolder<O>(output);
		this.outputHolders.add(outputHolder);
		return outputHolder;
	}

	public StageTester and() {
		return this;
	}

	public void start() {
		final AnalysisConfiguration configuration = new Configuration();
		final Analysis analysis = new Analysis(configuration);
		analysis.start();
	}

	public final class InputHolder<I> {

		private final Iterable<Object> input;
		private InputPort<Object> port;

		@SuppressWarnings("unchecked")
		private InputHolder(final Iterable<I> input) {
			this.input = (Iterable<Object>) input;
		}

		@SuppressWarnings("unchecked")
		public StageTester to(final InputPort<I> port) {
			if (port.getOwningStage() != stage) {
				throw new AssertionError();
			}
			this.port = (InputPort<Object>) port;

			return StageTester.this;
		}

		public Iterable<Object> getInput() {
			return input;
		}

		public InputPort<Object> getPort() {
			return port;
		}

	}

	public final class OutputHolder<O> {

		private final List<Object> output;
		private OutputPort<Object> port;

		@SuppressWarnings("unchecked")
		private OutputHolder(final List<O> output) {
			this.output = (List<Object>) output;
		}

		@SuppressWarnings("unchecked")
		public StageTester from(final OutputPort<O> port) {
			this.port = (OutputPort<Object>) port;

			return StageTester.this;
		}

		public List<Object> getOutput() {
			return output;
		}

		public OutputPort<Object> getPort() {
			return port;
		}

	}

	private final class Configuration extends AnalysisConfiguration {

		public Configuration() {
			IPipeFactory pipeFactory = AnalysisConfiguration.PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);

			for (InputHolder<?> inputHolder : inputHolders) {
				final IterableProducer<Object> producer = new IterableProducer<Object>(inputHolder.getInput());
				pipeFactory.create(producer.getOutputPort(), inputHolder.getPort());
				addThreadableStage(producer);
			}
			for (OutputHolder<?> outputHolder : outputHolders) {
				final CollectorSink<Object> sink = new CollectorSink<Object>(outputHolder.getOutput());
				pipeFactory.create(outputHolder.getPort(), sink.getInputPort());
			}
		}

	}

}
