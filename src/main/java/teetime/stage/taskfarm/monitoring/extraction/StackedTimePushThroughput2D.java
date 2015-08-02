package teetime.stage.taskfarm.monitoring.extraction;

import java.io.IOException;
import java.io.Writer;

import teetime.stage.taskfarm.monitoring.PipeMonitoringService;
import teetime.stage.taskfarm.monitoring.PipeMonitoringService.PipeMonitoringDataContainer;
import teetime.stage.taskfarm.monitoring.SingleTaskFarmMonitoringService;

public class StackedTimePushThroughput2D extends AbstractStackedMonitoring {

	public StackedTimePushThroughput2D(final PipeMonitoringService pipeMonitoringService, final SingleTaskFarmMonitoringService taskFarmMonitoringService) {
		super(pipeMonitoringService, taskFarmMonitoringService);
	}

	@Override
	protected void addTripleToCSV(final Writer writer, final int maxNumberOfPipes, final PipeMonitoringDataContainer container)
			throws IOException {
		String[] entryStrings = new String[maxNumberOfPipes + 1];
		entryStrings[0] = Long.toString(container.getTime());

		// add values while keeping pipe throughput consistent with pipe identity
		for (int i = 0; i < container.getPushThroughputsWithPipeIds().size(); i++) {
			Integer pipeId = container.getPushThroughputsWithPipeIds().get(i).getId();
			Long value = container.getPushThroughputsWithPipeIds().get(i).getValue();

			entryStrings[pipeId + 2] = Long.toString(value);
		}

		fillNullValuesWithZeros(entryStrings);

		addCSVLineToWriter(writer, entryStrings);
	}

	@Override
	protected void createHeader(final Writer writer, final int maxNumberOfStages) throws IOException {
		String[] headerStrings = new String[maxNumberOfStages + 1];
		headerStrings[0] = "time";
		for (int i = 0; i < maxNumberOfStages; i++) {
			headerStrings[i + 1] = "pushthroughput" + i;
		}
		addCSVLineToWriter(writer, headerStrings);
	}

}
