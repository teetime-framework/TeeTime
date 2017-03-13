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

import java.util.List;
import java.util.Optional;

import teetime.framework.Configuration;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;
import teetime.stage.string.*;
import teetime.stage.util.CountingMap;

/**
 * Configuration that counts the occurrences of words in a list of strings.
 * Procedure: read string list > to lower case > remove punctuation > count word occurrences
 *
 *
 * @author Sören Henning
 *
 */
public class ListWordCounterConfiguration extends Configuration {

	private final CollectorSink<CountingMap<String>> collector = new CollectorSink<>();

	public ListWordCounterConfiguration(final List<String> strings) {
		final InitialElementProducer<String> producer = new InitialElementProducer<>(strings);
		final ToLowerCase toLowerCase = new ToLowerCase();
		final WordcharacterFilter wordcharacterFilter = new WordcharacterFilter();
		final WordCounter wordCounter = new WordCounter();

		this.connectPorts(producer.getOutputPort(), toLowerCase.getInputPort());
		this.connectPorts(toLowerCase.getOutputPort(), wordcharacterFilter.getInputPort());
		this.connectPorts(wordcharacterFilter.getOutputPort(), wordCounter.getInputPort());
		this.connectPorts(wordCounter.getOutputPort(), this.collector.getInputPort());
	}

	public Optional<CountingMap<String>> getCountingMap() {
		final List<CountingMap<String>> maps = this.collector.getElements();
		if (maps.size() > 0) {
			return Optional.of(maps.get(0));
		} else {
			return Optional.empty();
		}
	}

}
