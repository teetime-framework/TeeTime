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

import java.io.Writer;
import java.util.List;

import teetime.stage.taskfarm.monitoring.PipeMonitoringService;
import teetime.stage.taskfarm.monitoring.SingleTaskFarmMonitoringService;
import teetime.stage.taskfarm.monitoring.TaskFarmMonitoringData;

/**
 * Represents a data exporter for general task farm measurements.
 *
 * @author ChristianClaus
 *
 */
public abstract class AbstractGeneralCSVExporter extends AbstractMonitoringDataExporter {

	/**
	 * Constructor.
	 *
	 * @param pipeMonitoringService
	 *            monitoring service concerning pipes
	 * @param taskFarmMonitoringService
	 *            monitoring service concerning a task farm
	 */
	public AbstractGeneralCSVExporter(final PipeMonitoringService pipeMonitoringService, final SingleTaskFarmMonitoringService taskFarmMonitoringService) {
		super(pipeMonitoringService, taskFarmMonitoringService);
	}

	@Override
	final protected void extractToWriter(final Writer writer) {
		List<TaskFarmMonitoringData> monitoredDataValues = this.getTaskFarmMonitoringService().getData();

		writeCSVData(writer, monitoredDataValues);
	}

	/**
	 * Write and format CSV data to the specified {@link Writer} using the monitoring measurements.
	 * 
	 * @param writer
	 *            writer to be written to
	 * @param monitoredDataValues
	 *            monitored measurements
	 */
	protected abstract void writeCSVData(final Writer writer, final List<TaskFarmMonitoringData> monitoredDataValues);
}
