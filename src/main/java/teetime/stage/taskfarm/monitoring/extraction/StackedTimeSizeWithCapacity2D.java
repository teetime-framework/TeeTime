package teetime.stage.taskfarm.monitoring.extraction;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import teetime.stage.taskfarm.monitoring.PipeMonitoringService;
import teetime.stage.taskfarm.monitoring.PipeMonitoringService.TimeCapacitiesSizes;
import teetime.stage.taskfarm.monitoring.TaskFarmMonitoringService;

public class StackedTimeSizeWithCapacity2D extends AbstractMonitoringDataExtraction {

	public StackedTimeSizeWithCapacity2D(final PipeMonitoringService pipeMonitoringService, final TaskFarmMonitoringService taskFarmMonitoringService) {
		super(pipeMonitoringService, taskFarmMonitoringService);
	}

	@Override
	protected void extractToWriter(final Writer writer) {
		List<TimeCapacitiesSizes> values = getPipeMonitoringService().getTimeCapacitiesSizes();
		int maxNumberOfPipes = getPipeMonitoringService().getPipes().size();

		try {
			createHeader(writer, maxNumberOfPipes);

			for (TimeCapacitiesSizes value : values) {
				addTripleToCSV(writer, maxNumberOfPipes, value);
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("The writer could not be written to: " + e.getMessage());
		}
	}

	private void addTripleToCSV(final Writer writer, final int maxNumberOfPipes, final TimeCapacitiesSizes value)
			throws IOException {
		String[] entryStrings = new String[maxNumberOfPipes + 2];
		entryStrings[0] = Long.toString(value.getTime());
		entryStrings[1] = Integer.toString(value.getCapacities().get(0));

		// add values while keeping pipe size consistent with pipe identity
		for (int i = 0; i < value.getSizes().size(); i++) {
			entryStrings[value.getPipeIndizes().get(i) + 2] = Integer.toString(value.getSizes().get(i));
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
