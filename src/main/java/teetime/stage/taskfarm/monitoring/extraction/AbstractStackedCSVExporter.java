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

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import teetime.stage.taskfarm.monitoring.PipeMonitoringService;
import teetime.stage.taskfarm.monitoring.PipeMonitoringService.PipeMonitoringDataContainer;
import teetime.stage.taskfarm.monitoring.SingleTaskFarmMonitoringService;

abstract public class AbstractStackedCSVExporter extends AbstractMonitoringDataExporter {

	public AbstractStackedCSVExporter(final PipeMonitoringService pipeMonitoringService, final SingleTaskFarmMonitoringService taskFarmMonitoringService) {
		super(pipeMonitoringService, taskFarmMonitoringService);
	}

	@Override
	final protected void extractToWriter(final Writer writer) {
		List<PipeMonitoringDataContainer> containers = getPipeMonitoringService().getData();
		int maxNumberOfPipes = this.getPipeMonitoringService().getPipes().size();

		try {
			createHeader(writer, maxNumberOfPipes);

			for (PipeMonitoringDataContainer container : containers) {
				if (container.getCapacitiesWithPipeIds().size() > 0) {
					addTripleToCSV(writer, maxNumberOfPipes, container);
				}
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("The writer could not be written to: " + e.getMessage());
		}
	}

	protected void fillNullValuesWithZeros(final String[] values) {
		for (int i = 0; i < values.length; i++) {
			if (values[i] == null) {
				values[i] = "0";
			}
		}
	}

	protected abstract void createHeader(final Writer writer, final int maxNumberOfStages) throws IOException;

	protected abstract void addTripleToCSV(final Writer writer, final int maxNumberOfPipes, final PipeMonitoringDataContainer container)
			throws IOException;
}
