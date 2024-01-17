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
package teetime.stage.string;

import teetime.framework.CompositeStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.performancelogging.ActivationStateLogger;
import teetime.framework.performancelogging.CompositeStateLoggable;
import teetime.stage.MappingCounter;
import teetime.stage.taskfarm.ITaskFarmDuplicable;
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
public class WordCounter extends CompositeStage implements ITaskFarmDuplicable<String, CountingMap<String>>, CompositeStateLoggable {

	private final Tokenizer tokenizer;
	private final MappingCounter<String> mapCounter;

	public WordCounter() {
		this.tokenizer = new Tokenizer("\\s");
		final ToLowerCase toLowerCase = new ToLowerCase();
		this.mapCounter = new MappingCounter<>();

		connectPorts(this.tokenizer.getOutputPort(), toLowerCase.getInputPort());
		connectPorts(toLowerCase.getOutputPort(), this.mapCounter.getInputPort());
	}

	@Override
	public InputPort<String> getInputPort() {
		return this.tokenizer.getInputPort();
	}

	@Override
	public OutputPort<CountingMap<String>> getOutputPort() {
		return this.mapCounter.getOutputPort();
	}

	@Override
	public ITaskFarmDuplicable<String, CountingMap<String>> duplicate() {
		return new WordCounter();
	}

	@Override
	public void registerStatebles() {
		ActivationStateLogger.getInstance().register(tokenizer);
		ActivationStateLogger.getInstance().register(tokenizer.getOutputPort().getPipe().getTargetPort().getOwningStage());
		ActivationStateLogger.getInstance().register(mapCounter);
	}
}
