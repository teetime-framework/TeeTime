package teetime.stage.taskfarm.monitoring.extraction;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import teetime.stage.taskfarm.monitoring.PipeMonitoringService;
import teetime.stage.taskfarm.monitoring.PipeMonitoringService.TimePushPullThroughputs;
import teetime.stage.taskfarm.monitoring.TaskFarmMonitoringService;

public class StackedTimePullThroughput2D extends AbstractMonitoringDataExtraction {

	public StackedTimePullThroughput2D(final PipeMonitoringService pipeMonitoringService, final TaskFarmMonitoringService taskFarmMonitoringService) {
		super(pipeMonitoringService, taskFarmMonitoringService);
	}

	@Override
	protected void extractToWriter(final Writer writer) {
		List<TimePushPullThroughputs> values = getPipeMonitoringService().getTimePushPullThroughput();
		int maxNumberOfStages = this.getTaskFarmMonitoringService().getMaxNumberOfStages();

		try {
			createHeader(writer, maxNumberOfStages);

			for (TimePushPullThroughputs value : values) {
				addTripleToCSV(writer, maxNumberOfStages, value);
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("The writer could not be written to: " + e.getMessage());
		}
	}

	private void addTripleToCSV(final Writer writer, final int maxNumberOfStages, final TimePushPullThroughputs value)
			throws IOException {
		String[] entryStrings = new String[maxNumberOfStages + 1];
		entryStrings[0] = Long.toString(value.getTime());

		// add values while keeping pipe throughput consistent with pipe identity
		for (int i = 0; i < value.getPullThroughputs().size(); i++) {
			entryStrings[value.getPipeIndizes().get(i) + 2] = Long.toString(value.getPullThroughputs().get(i));
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
