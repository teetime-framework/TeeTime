package teetime.stage.taskfarm.monitoring.extraction;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import teetime.stage.taskfarm.monitoring.PipeMonitoringService;
import teetime.stage.taskfarm.monitoring.PipeMonitoringService.PipeMonitoringDataContainer;
import teetime.stage.taskfarm.monitoring.SingleTaskFarmMonitoringService;

public class StackedTimePullThroughput2D extends AbstractMonitoringDataExtraction {

	public StackedTimePullThroughput2D(final PipeMonitoringService pipeMonitoringService, final SingleTaskFarmMonitoringService taskFarmMonitoringService) {
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
		String[] entryStrings = new String[maxNumberOfPipes + 1];
		entryStrings[0] = Long.toString(container.getTime());

		// add values while keeping pipe throughput consistent with pipe identity
		for (int i = 0; i < container.getPullThroughputsWithPipeIds().size(); i++) {
			Integer pipeId = container.getPullThroughputsWithPipeIds().get(i).getId();
			Long value = container.getPullThroughputsWithPipeIds().get(i).getValue();

			entryStrings[pipeId + 2] = Long.toString(value);
		}

		// give elements without values standard values for this timestamp
		for (int i = 0; i < entryStrings.length; i++) {
			if (entryStrings[i] == null) {
				entryStrings[i] = "0";
			}
		}

		addCSVLineToWriter(writer, entryStrings);
	}

	private void createHeader(final Writer writer, final int maxNumberOfStages) throws IOException {
		String[] headerStrings = new String[maxNumberOfStages + 1];
		headerStrings[0] = "time";
		for (int i = 0; i < maxNumberOfStages; i++) {
			headerStrings[i + 1] = "pullthroughput" + i;
		}
		addCSVLineToWriter(writer, headerStrings);
	}

}
