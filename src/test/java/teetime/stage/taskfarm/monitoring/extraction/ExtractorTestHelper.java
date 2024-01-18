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

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.IMonitorablePipe;
import teetime.framework.pipe.IPipe;
import teetime.stage.taskfarm.DynamicTaskFarmStage;
import teetime.stage.taskfarm.ITaskFarmDuplicable;
import teetime.stage.taskfarm.monitoring.PipeMonitoringService;
import teetime.stage.taskfarm.monitoring.SingleTaskFarmMonitoringService;

final class ExtractorTestHelper {

	private ExtractorTestHelper() {
	}

	static PipeMonitoringService generate4PipeMonitoringServiceWithBehavior() {
		ExtractionTestPipe<Integer> pipe1 = new ExtractionTestPipe<Integer>();
		ExtractionTestPipe<Integer> pipe2 = new ExtractionTestPipe<Integer>();
		ExtractionTestPipe<Integer> pipe3 = new ExtractionTestPipe<Integer>();
		ExtractionTestPipe<Integer> pipe4 = new ExtractionTestPipe<Integer>();

		PipeMonitoringService service = new PipeMonitoringService(null);

		// Plan:
		// Pipe 1: Tick 1-7
		// Pipe 2: Tick 3
		// Pipe 3: Tick 4-5
		// Pipe 4: Tick 7
		service.addMonitoredItem(pipe1);
		service.doMeasurement();
		wait50Millis();
		service.doMeasurement();
		wait50Millis();

		service.addMonitoredItem(pipe2);
		service.doMeasurement();
		wait50Millis();

		service.addMonitoredItem(pipe3);
		pipe2.setActive(false);
		service.doMeasurement();
		wait50Millis();
		service.doMeasurement();
		wait50Millis();
		pipe3.setActive(false);
		service.doMeasurement();
		wait50Millis();

		service.addMonitoredItem(pipe4);
		service.doMeasurement();
		return service;
	}

	static PipeMonitoringService generateEmpty5PipeMonitoringService() {
		ExtractionTestPipe<Integer> pipe1 = new ExtractionTestPipe<Integer>();
		ExtractionTestPipe<Integer> pipe2 = new ExtractionTestPipe<Integer>();
		ExtractionTestPipe<Integer> pipe3 = new ExtractionTestPipe<Integer>();
		ExtractionTestPipe<Integer> pipe4 = new ExtractionTestPipe<Integer>();
		ExtractionTestPipe<Integer> pipe5 = new ExtractionTestPipe<Integer>();

		PipeMonitoringService service = new PipeMonitoringService(null);
		service.addMonitoredItem(pipe1);
		service.addMonitoredItem(pipe2);
		service.addMonitoredItem(pipe3);
		service.addMonitoredItem(pipe4);
		service.addMonitoredItem(pipe4); // duplicates should not matter
		service.addMonitoredItem(pipe5);

		return service;
	}

	static SingleTaskFarmMonitoringService generateSingleTaskFarmMonitoringServiceWithBehavior() {
		@SuppressWarnings("unchecked")
		DynamicTaskFarmStage<Integer, Integer, ?> taskFarmStage = (DynamicTaskFarmStage<Integer, Integer, ?>) createDummyTaskFarm();
		SingleTaskFarmMonitoringService service = new SingleTaskFarmMonitoringService(taskFarmStage, null);

		// Plan (boundary=0.4):
		// Enclosed Stage 1: Tick 1-9
		// Enclosed Stage 2: Tick 4-9
		// Enclosed Stage 3: Tick 7-9
		taskFarmStage.getConfiguration().setThroughputScoreBoundary(0.4);
		service.doMeasurement();
		wait50Millis();
		service.doMeasurement();
		wait50Millis();
		service.doMeasurement();
		wait50Millis();

		taskFarmStage.getWorkerStages().add(createDummyEnclosedStage());
		service.doMeasurement();
		wait50Millis();
		service.doMeasurement();
		wait50Millis();
		service.doMeasurement();
		wait50Millis();

		taskFarmStage.getWorkerStages().add(createDummyEnclosedStage());
		service.doMeasurement();
		wait50Millis();
		service.doMeasurement();
		wait50Millis();
		service.doMeasurement();

		return service;
	}

	static SingleTaskFarmMonitoringService generateEmptySingleTaskFarmMonitoringService() {
		DynamicTaskFarmStage<?, ?, ?> taskFarmStage = createDummyTaskFarm();
		return new SingleTaskFarmMonitoringService(taskFarmStage, null);
	}

	private static DynamicTaskFarmStage<?, ?, ?> createDummyTaskFarm() {
		DynamicTaskFarmStage<Integer, Integer, ITaskFarmDuplicable<Integer, Integer>> taskFarmStage;
		taskFarmStage = new DynamicTaskFarmStage<Integer, Integer, ITaskFarmDuplicable<Integer, Integer>>(
				createDummyEnclosedStage(), 1);
		return taskFarmStage;
	}

	private static ITaskFarmDuplicable<Integer, Integer> createDummyEnclosedStage() {
		return new ITaskFarmDuplicable<Integer, Integer>() {
			IMonitorablePipe inputPipe = new ExtractionTestPipe<Integer>();

			@Override
			public ITaskFarmDuplicable<Integer, Integer> duplicate() {
				return null;
			}

			@SuppressWarnings("unchecked")
			@Override
			public InputPort<Integer> getInputPort() {
				return new ExtractionTestInputPort<Integer>((IPipe<Integer>) inputPipe);
			}

			@Override
			public OutputPort<Integer> getOutputPort() {
				return new ExtractionTestOutputPort<Integer>();
			}
		};
	}

	static void wait50Millis() {
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
}
