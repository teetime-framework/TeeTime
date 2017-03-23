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
import teetime.stage.taskfarm.monitoring.PipeMonitoringService.PipeMonitoringDataContainer;
import teetime.stage.taskfarm.monitoring.SingleTaskFarmMonitoringService;

/**
 * Represents a CSV file concerning pipe monitoring measurements.
 *
 * @author Christian Claus Wiechmann
 */
public abstract class AbstractStackedCSVExporter extends AbstractMonitoringDataExporter {

	/**
	 * Constructor.
	 *
	 * @param pipeMonitoringService
	 *            monitoring service concerning pipes
	 * @param taskFarmMonitoringService
	 *            monitoring service concerning a task farm
	 */
	public AbstractStackedCSVExporter(final PipeMonitoringService pipeMonitoringService, final SingleTaskFarmMonitoringService taskFarmMonitoringService) {
		super(pipeMonitoringService, taskFarmMonitoringService);
	}

	@Override
	protected final void extractToWriter(final Writer writer) {
		List<PipeMonitoringDataContainer> containers = getPipeMonitoringService().getData();
		int maxNumberOfPipes = this.getPipeMonitoringService().getPipes().size();

		try {
			createHeader(writer, maxNumberOfPipes);

			for (PipeMonitoringDataContainer container : containers) {
				if (container.getCapacitiesWithPipeIds().size() > 0) {
					addLineOfValuesToCSV(writer, maxNumberOfPipes, container);
				}
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("The writer could not be written to: " + e.getMessage());
		}
	}

	/**
	 * If a {@link String} in the given array is <code>null</code>, it is replaced with <code>"0"</code>.
	 *
	 * @param values
	 *            array of {@link String} values
	 */
	protected void fillNullValuesWithZeros(final String[] values) {
		for (int i = 0; i < values.length; i++) {
			if (values[i] == null) {
				values[i] = "0";
			}
		}
	}

	/**
	 * Creates the header of a CSV file.
	 *
	 * @param writer
	 *            writer for the header to be written to
	 * @param maxNumberOfStages
	 *            maximal number of stages of task farm measurements
	 * @throws IOException
	 */
	protected abstract void createHeader(final Writer writer, final int maxNumberOfStages) throws IOException;

	/**
	 * Adds a line of values, separated by commas, to the {@link Writer}.
	 *
	 * @param writer
	 *            writer to be written to
	 * @param maxNumberOfPipes
	 *            maximum amount of pipes monitored
	 * @param container
	 *            pipe measurement container
	 * @throws IOException
	 */
	protected abstract void addLineOfValuesToCSV(final Writer writer, final int maxNumberOfPipes, final PipeMonitoringDataContainer container)
			throws IOException;
}
