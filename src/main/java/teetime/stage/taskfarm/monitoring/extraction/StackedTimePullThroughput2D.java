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

import teetime.stage.taskfarm.monitoring.PipeMonitoringService;
import teetime.stage.taskfarm.monitoring.PipeMonitoringService.PipeMonitoringDataContainer;
import teetime.stage.taskfarm.monitoring.SingleTaskFarmMonitoringService;

public class StackedTimePullThroughput2D extends AbstractStackedCSVExporter {

	public StackedTimePullThroughput2D(final PipeMonitoringService pipeMonitoringService, final SingleTaskFarmMonitoringService taskFarmMonitoringService) {
		super(pipeMonitoringService, taskFarmMonitoringService);
	}

	@Override
	protected void addTripleToCSV(final Writer writer, final int maxNumberOfPipes, final PipeMonitoringDataContainer container)
			throws IOException {
		String[] entryStrings = new String[maxNumberOfPipes + 1];
		entryStrings[0] = Long.toString(container.getTime());

		// add values while keeping pipe throughput consistent with pipe identity
		for (int i = 0; i < container.getPullThroughputsWithPipeIds().size(); i++) {
			Integer pipeId = container.getPullThroughputsWithPipeIds().get(i).getId();
			Long value = container.getPullThroughputsWithPipeIds().get(i).getValue();

			entryStrings[pipeId + 1] = Long.toString(value);
		}

		fillNullValuesWithZeros(entryStrings);

		addCSVLineToWriter(writer, entryStrings);
	}

	@Override
	protected void createHeader(final Writer writer, final int maxNumberOfStages) throws IOException {
		String[] headerStrings = new String[maxNumberOfStages + 1];
		headerStrings[0] = "time";
		for (int i = 0; i < maxNumberOfStages; i++) {
			headerStrings[i + 1] = "pullthroughput" + i;
		}
		addCSVLineToWriter(writer, headerStrings);
	}

}
