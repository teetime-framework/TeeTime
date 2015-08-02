package teetime.stage.taskfarm.monitoring.extraction;

import java.io.IOException;
import java.io.Writer;

import teetime.stage.taskfarm.monitoring.PipeMonitoringService;
import teetime.stage.taskfarm.monitoring.PipeMonitoringService.PipeMonitoringDataContainer;
import teetime.stage.taskfarm.monitoring.SingleTaskFarmMonitoringService;

public class StackedTimeSizeWithCapacity2D extends AbstractStackedMonitoring {

	public StackedTimeSizeWithCapacity2D(final PipeMonitoringService pipeMonitoringService, final SingleTaskFarmMonitoringService taskFarmMonitoringService) {
		super(pipeMonitoringService, taskFarmMonitoringService);
	}

	@Override
	protected void addTripleToCSV(final Writer writer, final int maxNumberOfPipes, final PipeMonitoringDataContainer container)
			throws IOException {
		String[] entryStrings = new String[maxNumberOfPipes + 2];
		entryStrings[0] = Long.toString(container.getTime());
		entryStrings[1] = Integer.toString(container.getCapacitiesWithPipeIds().get(0).getValue());

		// add values while keeping pipe size consistent with pipe identity
		for (int i = 0; i < container.getSizesWithPipeIds().size(); i++) {
			Integer pipeId = container.getSizesWithPipeIds().get(i).getId();
			Integer value = container.getSizesWithPipeIds().get(i).getValue();

			entryStrings[pipeId + 2] = Integer.toString(value);
		}

		fillNullValuesWithZeros(entryStrings);

		addCSVLineToWriter(writer, entryStrings);
	}

	@Override
	protected void createHeader(final Writer writer, final int maxNumberOfPipes) throws IOException {
		String[] headerStrings = new String[maxNumberOfPipes + 2];
		headerStrings[0] = "time";
		headerStrings[1] = "capacity";
		for (int i = 0; i < maxNumberOfPipes; i++) {
			headerStrings[i + 2] = "size" + i;
		}
		addCSVLineToWriter(writer, headerStrings);
	}

}
