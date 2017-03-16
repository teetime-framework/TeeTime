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

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import teetime.framework.pipe.BoundedSynchedPipeFactory;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;

public class ConfigurationTest {

	public static final Integer[] INPUT_ELEMENTS = { 1, 2, 3 }; // NOPMD
	public static final List<Integer> EXPECTED_OUTPUT_ELEMENTS = Arrays.asList(INPUT_ELEMENTS); // NOPMD

	private static class ConfigurationWithProvidedOrCustomPipe extends Configuration {

		private final CollectorSink<Integer> collectorSink;

		public ConfigurationWithProvidedOrCustomPipe(final boolean withProvidedPipe) {
			InitialElementProducer<Integer> producer = new InitialElementProducer<Integer>(INPUT_ELEMENTS);
			CollectorSink<Integer> collectorSink = new CollectorSink<Integer>();

			collectorSink.declareActive();

			if (withProvidedPipe) {
				connectPorts(producer.getOutputPort(), collectorSink.getInputPort());
			} else {
				// AbstractPipe<Integer> pipe = new BoundedSynchedPipe<Integer>(producer.getOutputPort(), collectorSink.getInputPort(), 512);
				// registerCustomPipe(pipe);
				connectPorts(producer.getOutputPort(), collectorSink.getInputPort(), 512, BoundedSynchedPipeFactory.INSTANCE);
			}

			this.collectorSink = collectorSink;
		}

		List<Integer> getOutputElements() {
			return collectorSink.getElements();
		}
	}

	@Test
	public void configWithProvidedPipeShouldBeExecutable() throws Exception {
		ConfigurationWithProvidedOrCustomPipe configuration = new ConfigurationWithProvidedOrCustomPipe(true);
		Execution<Configuration> execution = new Execution<Configuration>(configuration);
		execution.executeBlocking();
		assertThat(configuration.getOutputElements(), is(EXPECTED_OUTPUT_ELEMENTS));
	}

	@Test
	public void configWithCustomPipeShouldBeExecutable() throws Exception {
		ConfigurationWithProvidedOrCustomPipe configuration = new ConfigurationWithProvidedOrCustomPipe(false);
		Execution<Configuration> execution = new Execution<Configuration>(configuration);
		execution.executeBlocking();
		assertThat(configuration.getOutputElements(), is(EXPECTED_OUTPUT_ELEMENTS));
	}
}
