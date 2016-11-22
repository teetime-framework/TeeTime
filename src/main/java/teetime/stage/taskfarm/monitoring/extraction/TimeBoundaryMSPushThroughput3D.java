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

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import teetime.stage.taskfarm.monitoring.PipeMonitoringService;
import teetime.stage.taskfarm.monitoring.SingleTaskFarmMonitoringService;
import teetime.stage.taskfarm.monitoring.TaskFarmMonitoringData;

/**
 * Represents a CSV file containing time-boundary-mean push throughput-total push throughput-tuples.
 *
 * @author Christian Claus Wiechmann
 */
public class TimeBoundaryMSPushThroughput3D extends AbstractGeneralCSVExporter {

	/**
	 * Constructor.
	 *
	 * @param pipeMonitoringService
	 *            monitoring service concerning pipes
	 * @param taskFarmMonitoringService
	 *            monitoring service concerning a task farm
	 */
	public TimeBoundaryMSPushThroughput3D(final PipeMonitoringService pipeMonitoringService, final SingleTaskFarmMonitoringService taskFarmMonitoringService) {
		super(pipeMonitoringService, taskFarmMonitoringService);
	}

	@Override
	protected void writeCSVData(final Writer writer, final List<TaskFarmMonitoringData> monitoredDataValues) {
		try {
			addCSVLineToWriter(writer, "time", "boundary", "mpushthroughput", "pushthroughputsum");

			for (TaskFarmMonitoringData taskFarmMonitoringData : monitoredDataValues) {
				addCSVLineToWriter(writer,
						Long.toString(taskFarmMonitoringData.getTime()),
						Double.toString(taskFarmMonitoringData.getThroughputBoundary()),
						Double.toString(taskFarmMonitoringData.getMeanPushThroughput()),
						Double.toString(taskFarmMonitoringData.getSumOfPushThroughput()));
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("The writer could not be written to: " + e.getMessage(), e);
		}
	}
}
