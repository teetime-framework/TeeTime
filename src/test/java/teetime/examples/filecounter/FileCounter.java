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
package teetime.examples.filecounter;

import java.io.File;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

/**
 * Simply counts all incoming elements and passes the final result upon termination.
 *
 * @author Nelson Tavares de Sousa
 *
 */
class FileCounter extends AbstractConsumerStage<File> {

	private final OutputPort<Integer> outputPort = createOutputPort();

	private int counter = 0;

	@Override
	protected void execute(final File element) {
		counter++; // Increment for every incoming file
	}

	@Override
	public void onTerminating() {
		outputPort.send(counter);
		super.onTerminating();
	}

	public OutputPort<Integer> getOutputPort() {
		return outputPort;
	}
}
