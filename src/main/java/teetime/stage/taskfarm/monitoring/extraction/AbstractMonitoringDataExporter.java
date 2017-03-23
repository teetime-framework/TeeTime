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
package teetime.stage.taskfarm.monitoring.extraction;

import java.io.*;

import teetime.stage.taskfarm.monitoring.PipeMonitoringService;
import teetime.stage.taskfarm.monitoring.SingleTaskFarmMonitoringService;

/**
 * Represents basic CSV export functionality for monitoring data exporters.
 * Used for task farms.
 *
 * @author Christian Claus Wiechmann
 */
public abstract class AbstractMonitoringDataExporter {

	private static final String NEWLINE = System.getProperty("line.separator");
	/** monitoring service concerning pipes **/
	private final PipeMonitoringService pipeMonitoringService;
	/** monitoring service concerning a task farm **/
	private final SingleTaskFarmMonitoringService taskFarmMonitoringService;

	/**
	 * Constructor.
	 *
	 * @param pipeMonitoringService
	 *            monitoring service concerning pipes
	 * @param taskFarmMonitoringService
	 *            monitoring service concerning a task farm
	 */
	public AbstractMonitoringDataExporter(final PipeMonitoringService pipeMonitoringService, final SingleTaskFarmMonitoringService taskFarmMonitoringService) {
		this.pipeMonitoringService = pipeMonitoringService;
		this.taskFarmMonitoringService = taskFarmMonitoringService;
	}

	/**
	 * Writes the formatted monitored data to a {@link Writer}.
	 *
	 * @param writer
	 *            writer to be written to
	 */
	protected abstract void extractToWriter(Writer writer);

	/**
	 * Saves the monitored data to a specified {@link File}.
	 *
	 * @param file
	 *            specified file
	 * @throws IOException
	 */
	public void extractToFile(final File file) throws IOException {
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter writer = new FileWriter(file, false);

		this.extractToWriter(writer);

		writer.close();
	}

	/**
	 * Creates a file at the specified path and saves the monitored data to it.
	 *
	 * @param filepath
	 *            specified file path
	 * @throws IOException
	 */
	public void extractToFile(final String filepath) throws IOException {
		File file = new File(filepath);
		extractToFile(file);
	}

	/**
	 * Returns the formatted monitored data as a {@link String}.
	 *
	 * @return formatted monitored data
	 */
	public String extractToString() {
		StringWriter writer = new StringWriter();

		this.extractToWriter(writer);

		return writer.toString();
	}

	/**
	 * Add a CSV line to the specified {@link Writer}. The specified arguments are separated by commas.
	 *
	 * @param writer
	 *            writer to be written to
	 * @param args
	 *            values of the line
	 * @throws IOException
	 */
	protected static void addCSVLineToWriter(final Writer writer, final String... args) throws IOException {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < args.length; i++) {
			builder.append(args[i]);
			if (i != args.length - 1) {
				builder.append(",");
			}
		}

		builder.append(NEWLINE);

		writer.append(builder.toString());
	}

	/**
	 * @return monitoring service concerning pipes
	 */
	public PipeMonitoringService getPipeMonitoringService() {
		return this.pipeMonitoringService;
	}

	/**
	 * @return monitoring service concerning a task farm
	 */
	public SingleTaskFarmMonitoringService getTaskFarmMonitoringService() {
		return this.taskFarmMonitoringService;
	}
}
