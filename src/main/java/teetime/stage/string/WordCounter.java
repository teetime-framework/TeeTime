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
package teetime.stage.string;

import java.util.ArrayList;
import java.util.Collection;

import teetime.framework.CompositeStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.Stage;
import teetime.stage.MappingCounter;
import teetime.stage.util.CountingMap;

/**
 * Intermediate stage, which receives texts and counts the occurring words.
 * The result (a {@link CountingMap}) is passed on upon termination.
 *
 * @since 1.1
 *
 * @author Nelson Tavares de Sousa
 *
 */
public final class WordCounter extends CompositeStage {

	// This fields are needed for the methods to work.
	private final Tokenizer tokenizer = new Tokenizer(" ");
	private final MappingCounter<String> mapCounter = new MappingCounter<String>();
	private final ArrayList<Stage> lastStages = new ArrayList<Stage>();

	// The connection of the different stages is realized within the construction of a instance of this class.
	public WordCounter() {
		this.lastStages.add(this.mapCounter);

		final ToLowerCase toLowerCase = new ToLowerCase();
		final WordcharacterFilter wordcharacterFilter = new WordcharacterFilter();

		connectStages(this.tokenizer.getOutputPort(), toLowerCase.getInputPort());
		connectStages(toLowerCase.getOutputPort(), wordcharacterFilter.getInputPort());
		connectStages(wordcharacterFilter.getOutputPort(), this.mapCounter.getInputPort());
	}

	@Override
	protected Stage getFirstStage() {
		return this.tokenizer;
	}

	@Override
	protected Collection<? extends Stage> getLastStages() {
		return this.lastStages;
	}

	public InputPort<String> getInputPort() {
		return this.tokenizer.getInputPort();
	}

	public OutputPort<CountingMap<String>> getOutputPort() {
		return this.mapCounter.getOutputPort();
	}

}
