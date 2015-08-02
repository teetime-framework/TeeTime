package teetime.stage.taskfarm.monitoring.extraction;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import teetime.stage.taskfarm.monitoring.PipeMonitoringService;
import teetime.stage.taskfarm.monitoring.PipeMonitoringService.PipeMonitoringDataContainer;
import teetime.stage.taskfarm.monitoring.SingleTaskFarmMonitoringService;

public class StackedTimeSizeWithCapacity2D extends AbstractMonitoringDataExtraction {

	public StackedTimeSizeWithCapacity2D(final PipeMonitoringService pipeMonitoringService, final SingleTaskFarmMonitoringService taskFarmMonitoringService) {
		super(pipeMonitoringService, taskFarmMonitoringService);
	}

	@Override
	protected void extractToWriter(final Writer writer) {
		List<PipeMonitoringDataContainer> containers = getPipeMonitoringService().getData();
		int maxNumberOfPipes = this.getPipeMonitoringService().getPipes().size();

		try {
			createHeader(writer, maxNumberOfPipes);

			for (PipeMonitoringDataContainer container : containers) {
				addTripleToCSV(writer, maxNumberOfPipes, container);
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("The writer could not be written to: " + e.getMessage());
		}
	}

	private void addTripleToCSV(final Writer writer, final int maxNumberOfPipes, final PipeMonitoringDataContainer container)
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

		// give elements without values standard values for this timestamp
		for (int i = 0; i < entryStrings.length; i++) {
			if (entryStrings[i] == null) {
				entryStrings[i] = "0";
			}
		}

		addCSVLineToWriter(writer, entryStrings);
	}

	private void createHeader(final Writer writer, final int maxNumberOfPipes) throws IOException {
		String[] headerStrings = new String[maxNumberOfPipes + 2];
		headerStrings[0] = "time";
		headerStrings[1] = "capacity";
		for (int i = 0; i < maxNumberOfPipes; i++) {
			headerStrings[i + 2] = "size" + i;
		}
		addCSVLineToWriter(writer, headerStrings);
	}

}
