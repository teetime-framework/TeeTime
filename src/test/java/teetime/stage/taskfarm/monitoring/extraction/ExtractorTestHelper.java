package teetime.stage.taskfarm.monitoring.extraction;

import teetime.stage.taskfarm.monitoring.PipeMonitoringService;

final class ExtractorTestHelper {

	private ExtractorTestHelper() {}

	static PipeMonitoringService generatePipeBehavior() {
		ExtractionTestPipe pipe1 = new ExtractionTestPipe();
		ExtractionTestPipe pipe2 = new ExtractionTestPipe();
		ExtractionTestPipe pipe3 = new ExtractionTestPipe();
		ExtractionTestPipe pipe4 = new ExtractionTestPipe();

		PipeMonitoringService service = new PipeMonitoringService();

		// Plan:
		// Pipe 1: Tick 1-7
		// Pipe 2: Tick 3
		// Pipe 3: Tick 4-5
		// Pipe 4: Tick 7
		service.addMonitoredItem(pipe1);
		service.addMonitoringData();
		wait50Millis();
		service.addMonitoringData();
		wait50Millis();

		service.addMonitoredItem(pipe2);
		service.addMonitoringData();
		wait50Millis();

		service.addMonitoredItem(pipe3);
		pipe2.setActive(false);
		service.addMonitoringData();
		wait50Millis();
		service.addMonitoringData();
		wait50Millis();
		pipe3.setActive(false);
		service.addMonitoringData();
		wait50Millis();

		service.addMonitoredItem(pipe4);
		service.addMonitoringData();
		return service;
	}

	static void wait50Millis() {
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
}
