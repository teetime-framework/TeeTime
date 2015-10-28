/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://christianwulf.github.io/teetime)
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
package teetime.stage.taskfarm.monitoring.extraction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import teetime.stage.taskfarm.monitoring.PipeMonitoringService;
import teetime.stage.taskfarm.monitoring.SingleTaskFarmMonitoringService;

public abstract class AbstractMonitoringDataExporter {

	private final static String NEWLINE = System.getProperty("line.separator");
	private final PipeMonitoringService pipeMonitoringService;
	private final SingleTaskFarmMonitoringService taskFarmMonitoringService;

	public AbstractMonitoringDataExporter(final PipeMonitoringService pipeMonitoringService, final SingleTaskFarmMonitoringService taskFarmMonitoringService) {
		this.pipeMonitoringService = pipeMonitoringService;
		this.taskFarmMonitoringService = taskFarmMonitoringService;
	}

	protected abstract void extractToWriter(Writer writer);

	public void extractToFile(final File file) throws IOException {
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter writer = new FileWriter(file, false);

		this.extractToWriter(writer);

		writer.close();
	}

	public void extractToFile(final String filepath) throws IOException {
		File file = new File(filepath);
		extractToFile(file);
	}

	public String extractToString() {
		StringWriter writer = new StringWriter();

		this.extractToWriter(writer);

		return writer.toString();
	}

	public static void addCSVLineToWriter(final Writer writer, final String... args) throws IOException {
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

	public PipeMonitoringService getPipeMonitoringService() {
		return pipeMonitoringService;
	}

	public SingleTaskFarmMonitoringService getTaskFarmMonitoringService() {
		return taskFarmMonitoringService;
	}
}
