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

import teetime.framework.Configuration;
import teetime.framework.Execution;

/**
 * This configuration gives you a short insight into how you can program with TeeTime.
 * Three simple stages are instantiated and connected.
 * Thanks to the main method, we are capable of running the config from the console.
 *
 * @author Nelson Tavares de Sousa
 *
 */
class PrintResultConfig extends Configuration {

	public PrintResultConfig(final File dir) {
		// The Three Staages
		DirReader dirReader = new DirReader(dir);
		FileCounter fileCounter = new FileCounter();
		ResultPrinter resultPrinter = new ResultPrinter();

		// Connect stages
		connectPorts(dirReader.getOutputPort(), fileCounter.getInputPort());
		connectPorts(fileCounter.getOutputPort(), resultPrinter.getInputPort());
	}

	/**
	 * Runs the config on the current working directory
	 *
	 * @param args
	 *            No further usage
	 */
	public static void main(final String... args) {
		PrintResultConfig config = new PrintResultConfig(new File("."));

		Execution<PrintResultConfig> execution = new Execution<PrintResultConfig>(config);
		execution.executeBlocking();
	}
}
