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

import java.util.function.Function;

import teetime.stage.basic.ITransformation;

/**
 * Builder class for creating configurations. Configurations can be created by calling the {@code from()} method.
 *
 * @author Sören Henning
 *
 * @since 3.0
 *
 */
public class ConfigurationBuilder {

	private final Configuration configuration;

	private ConfigurationBuilder(final Configuration configuration) {
		this.configuration = configuration;
	}

	private <O> ConfigurationBuilder.Connection<O> start(final AbstractProducerStage<O> stage) {
		final OutputPort<O> outputPort = stage.getOutputPort();
		return new Connection<O>(outputPort);
	}

	protected static <O> ConfigurationBuilder.Connection<O> create(final Configuration configuration, final AbstractProducerStage<O> stage) {
		final ConfigurationBuilder config = new ConfigurationBuilder(configuration);
		return config.start(stage);
	}

	public static <O> ConfigurationBuilder.Connection<O> from(final AbstractProducerStage<O> stage) {
		return create(new Configuration(), stage);
	}

	public class Connection<I> {

		private final OutputPort<I> lastPort;

		private Connection(final OutputPort<I> lastPort) {
			this.lastPort = lastPort;
		}

		public <O> Connection<O> to(final ITransformation<I, O> stage) {
			final InputPort<I> inputPort = stage.getInputPort();
			final OutputPort<O> outputPort = stage.getOutputPort();

			ConfigurationBuilder.this.configuration.connectPorts(this.lastPort, inputPort);

			return new Connection<O>(outputPort);
		}

		public <S extends AbstractStage, O> Connection<O> to(final S stage, final Function<S, InputPort<I>> inputPort, final Function<S, OutputPort<O>> outputPort) {
			final ITransformation<I, O> transformerStage = TransfomerStage.of(stage, inputPort, outputPort);
			return this.to(transformerStage);
		}

		public Configuration end(final AbstractConsumerStage<I> stage) {
			final InputPort<I> inputPort = stage.getInputPort();
			ConfigurationBuilder.this.configuration.connectPorts(this.lastPort, inputPort);

			return ConfigurationBuilder.this.configuration;
		}

		public OutputPort<I> getOutputPort() {
			return this.lastPort;
		}

	}

	private static class TransfomerStage<I, O> implements ITransformation<I, O> {

		private final InputPort<I> inputPort;
		private final OutputPort<O> outputPort;

		private TransfomerStage(final InputPort<I> inputPort, final OutputPort<O> outputPort) {
			this.inputPort = inputPort;
			this.outputPort = outputPort;
		}

		@Override
		public InputPort<I> getInputPort() {
			return this.inputPort;
		}

		@Override
		public OutputPort<O> getOutputPort() {
			return this.outputPort;
		}

		public static <S extends AbstractStage, I, O> TransfomerStage<I, O> of(final S stage, final Function<S, InputPort<I>> inputPort,
				final Function<S, OutputPort<O>> outputPort) {
			return new TransfomerStage<I, O>(inputPort.apply(stage), outputPort.apply(stage));
		}

	}

}
