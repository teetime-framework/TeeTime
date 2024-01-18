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
package teetime.examples.listwordcounter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import teetime.framework.Configuration;
import teetime.framework.ConfigurationBuilder;
import teetime.framework.Execution;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;
import teetime.stage.string.ToLowerCase;
import teetime.stage.string.WordCounter;
import teetime.stage.string.WordcharacterFilter;
import teetime.stage.util.CountingMap;

/**
 * Execute stages that count count the occurrences of words in a list of
 * strings. Procedure: read string list > to lower case > remove punctuation >
 * count word occurrences
 *
 * @author Sören Henning
 *
 */
public class ListWordCounterTest {

	private static final List<String> STRINGS = Arrays.asList("Hello World!", "Hello TeeTime.", "Bye world");

	public ListWordCounterTest() {
		// empty constructor
	}

	@Test
	public void executeTestWithDefaultConfiguration() throws IOException {
		final ListWordCounterConfiguration configuration = new ListWordCounterConfiguration(STRINGS);
		final Execution<ListWordCounterConfiguration> execution = new Execution<>(configuration);
		execution.executeBlocking();

		CountingMap<String> map = configuration.getCountingMap().orElse(new CountingMap<>());

		Assert.assertEquals(2, map.get("hello"));
		Assert.assertEquals(2, map.get("world"));
		Assert.assertEquals(1, map.get("teetime"));
		Assert.assertEquals(1, map.get("bye"));
	}

	@Test
	public void executeTestWithBuilderBasedConfiguration() throws IOException {
		final ListWordCounterConfigurationFromBuilder configuration = new ListWordCounterConfigurationFromBuilder(
				STRINGS);
		final Execution<ListWordCounterConfigurationFromBuilder> execution = new Execution<>(configuration);
		execution.executeBlocking();

		CountingMap<String> map = configuration.getCountingMap().orElse(new CountingMap<>());

		Assert.assertEquals(2, map.get("hello"));
		Assert.assertEquals(2, map.get("world"));
		Assert.assertEquals(1, map.get("teetime"));
		Assert.assertEquals(1, map.get("bye"));
	}

	@Test
	public void executeTestWithConfigurationCreatedByBuilder() throws IOException {
		final CollectorSink<CountingMap<String>> collector = new CollectorSink<>();

		final Configuration configuration = ConfigurationBuilder.from(new InitialElementProducer<>(STRINGS))
				.to(new ToLowerCase()).to(new WordcharacterFilter()).to(new WordCounter()).end(collector);
		final Execution<Configuration> execution = new Execution<Configuration>(configuration);
		execution.executeBlocking();

		final List<CountingMap<String>> maps = collector.getElements();

		Assert.assertTrue(!maps.isEmpty());

		CountingMap<String> map = maps.get(0);

		Assert.assertEquals(2, map.get("hello"));
		Assert.assertEquals(2, map.get("world"));
		Assert.assertEquals(1, map.get("teetime"));
		Assert.assertEquals(1, map.get("bye"));
	}

}
