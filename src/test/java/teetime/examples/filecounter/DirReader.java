/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
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
package teetime.examples.filecounter;

import java.io.File;

import teetime.framework.AbstractProducerStage;

/**
 * Visits the given directory and all containing sub-directories and sends all containing files to its output port.
 *
 * @author Nelson Tavares de Sousa
 *
 */
class DirReader extends AbstractProducerStage<File> {

	private final File element;

	public DirReader(final File directory) {
		if (directory.isDirectory()) { // Check if it is a directory
			element = directory;
		} else {
			throw new IllegalArgumentException("Given file is not a directory.");
		}
	}

	@Override
	protected void execute() {
		visit(element);
		this.terminate(); // If everything is done, terminate
	}

	/**
	 * Helper method to enable recursive calls
	 *
	 * @param element
	 *            Dir to visit
	 */
	private void visit(final File element) {
		for (File file : element.listFiles()) {
			if (file.isDirectory()) {
				this.visit(file); // Visit recursively all dirs
			} else {
				outputPort.send(file); // Send file to next stage
			}
		}
	}

}
