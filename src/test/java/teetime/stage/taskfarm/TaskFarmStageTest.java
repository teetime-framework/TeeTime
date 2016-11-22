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
package teetime.stage.taskfarm;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import teetime.framework.Execution;
import teetime.stage.taskfarm.monitoring.PipeMonitoringService;
import teetime.stage.taskfarm.monitoring.SingleTaskFarmMonitoringService;
import teetime.stage.taskfarm.monitoring.extraction.AbstractMonitoringDataExporter;
import teetime.stage.taskfarm.monitoring.extraction.StackedTimePullThroughput2D;
import teetime.stage.taskfarm.monitoring.extraction.StackedTimePushThroughput2D;
import teetime.stage.taskfarm.monitoring.extraction.StackedTimeSizeWithCapacity2D;
import teetime.stage.taskfarm.monitoring.extraction.TimeBoundary2D;
import teetime.stage.taskfarm.monitoring.extraction.TimeBoundaryMSPullThroughput3D;
import teetime.stage.taskfarm.monitoring.extraction.TimeBoundaryMSPushThroughput3D;
import teetime.stage.taskfarm.monitoring.extraction.TimeBoundaryStages3D;

public class TaskFarmStageTest {

	private static final int NUMBER_OF_TEST_ELEMENTS = 50000;

	@Test
	public void simpleTaskFarmStageTest() throws IOException {
		final TaskFarmStageTestConfiguration configuration = new TaskFarmStageTestConfiguration(NUMBER_OF_TEST_ELEMENTS);
		final Execution<TaskFarmStageTestConfiguration> execution = new Execution<TaskFarmStageTestConfiguration>(configuration);

		execution.executeBlocking();

		assertThat(TaskFarmStageTestConfiguration.getNumOfElements(), is(NUMBER_OF_TEST_ELEMENTS));

		checkIfLoggingWorks(configuration);
	}

	private void checkIfLoggingWorks(final TaskFarmStageTestConfiguration configuration) throws IOException {
		PipeMonitoringService pipeService = configuration.getTaskFarmStage().getPipeMonitoringService();
		SingleTaskFarmMonitoringService taskFarmService = configuration.getTaskFarmStage().getTaskFarmMonitoringService();
		applyExtractors(pipeService, taskFarmService);
	}

	private void applyExtractors(final PipeMonitoringService pipeService, final SingleTaskFarmMonitoringService taskFarmService) throws IOException {
		extractToTempFile(new StackedTimeSizeWithCapacity2D(pipeService, taskFarmService));
		extractToTempFile(new StackedTimePullThroughput2D(pipeService, taskFarmService));
		extractToTempFile(new StackedTimePushThroughput2D(pipeService, taskFarmService));
		extractToTempFile(new TimeBoundary2D(pipeService, taskFarmService));
		extractToTempFile(new TimeBoundaryMSPullThroughput3D(pipeService, taskFarmService));
		extractToTempFile(new TimeBoundaryMSPushThroughput3D(pipeService, taskFarmService));
		extractToTempFile(new TimeBoundaryStages3D(pipeService, taskFarmService));
	}

	private void extractToTempFile(final AbstractMonitoringDataExporter extractor) throws IOException {
		File tempFile = File.createTempFile(extractor.getClass().getSimpleName(), ".tmp");
		tempFile.deleteOnExit();

		extractor.extractToFile(tempFile);

		assertTrue(tempFile.exists());
		assertThat(tempFile.length(), is(greaterThan(0l)));
	}

}
