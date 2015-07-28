package teetime.stage.taskfarm.monitoring.extraction;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;

import teetime.stage.taskfarm.monitoring.PipeMonitoringService;
import teetime.stage.taskfarm.monitoring.PipeMonitoringService.ImmutableTriple;
import teetime.stage.taskfarm.monitoring.TaskFarmMonitoringData;
import teetime.stage.taskfarm.monitoring.TaskFarmMonitoringService;

public class StackedTimePullThroughput2D extends AbstractMonitoringDataExtraction {

	public StackedTimePullThroughput2D(final PipeMonitoringService pipeMonitoringService, final TaskFarmMonitoringService taskFarmMonitoringService) {
		super(pipeMonitoringService, taskFarmMonitoringService);
	}

	@Override
	protected void extractToWriter(final Writer writer) {
		List<ImmutableTriple<Long, List<Long>, List<Long>>> triples = getPipeMonitoringService().getTimePushPullThroughput();
		int maxNumberOfStages = getMaxNumberOfStages();

		try {
			createHeader(writer, maxNumberOfStages);

			for (ImmutableTriple<Long, List<Long>, List<Long>> triple : triples) {
				addTripleToCSV(writer, maxNumberOfStages, triple);
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("The writer could not be written to: " + e.getMessage());
		}
	}

	private int getMaxNumberOfStages() {
		int maxNumberOfStages = 0;

		Collection<List<TaskFarmMonitoringData>> listOfEntries = this.getTaskFarmMonitoringService().getData().values();
		for (List<TaskFarmMonitoringData> dataEntries : listOfEntries) {
			for (TaskFarmMonitoringData taskFarmMonitoringData : dataEntries) {
				if (taskFarmMonitoringData.getStages() > maxNumberOfStages) {
					maxNumberOfStages = taskFarmMonitoringData.getStages();
				}
			}
		}

		return maxNumberOfStages;
	}

	private void addTripleToCSV(final Writer writer, final int maxNumberOfStages, final ImmutableTriple<Long, List<Long>, List<Long>> triple)
			throws IOException {
		String[] entryStrings = new String[maxNumberOfStages + 1];
		entryStrings[0] = Long.toString(triple.getA());

		for (int i = 0; i < triple.getC().size(); i++) {
			entryStrings[i + 1] = Long.toString(triple.getC().get(i));
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
