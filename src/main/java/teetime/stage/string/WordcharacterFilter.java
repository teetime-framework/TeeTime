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
package teetime.stage.string;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

/**
 * Receives a string and passes it with removed punctuation and similar characters on to the next stage. Only [a-zA-Z ] will be passed on.
 *
 * @since 1.1
 *
 * @author Nelson Tavares de Sousa
 *
 */
public final class WordcharacterFilter extends AbstractConsumerStage<String> {

	private final OutputPort<String> outputPort = this.createOutputPort();

	@Override
	protected void execute(final String element) {
		this.outputPort.send(element.replaceAll("[^a-zA-Z ]", ""));

	}

	public OutputPort<String> getOutputPort() {
		return this.outputPort;
	}

}
