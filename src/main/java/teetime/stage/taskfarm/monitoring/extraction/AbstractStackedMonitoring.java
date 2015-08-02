package teetime.stage.taskfarm.monitoring.extraction;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import teetime.stage.taskfarm.monitoring.PipeMonitoringService;
import teetime.stage.taskfarm.monitoring.PipeMonitoringService.PipeMonitoringDataContainer;
import teetime.stage.taskfarm.monitoring.SingleTaskFarmMonitoringService;

abstract public class AbstractStackedMonitoring extends AbstractMonitoringDataExtraction {

	public AbstractStackedMonitoring(final PipeMonitoringService pipeMonitoringService, final SingleTaskFarmMonitoringService taskFarmMonitoringService) {
		super(pipeMonitoringService, taskFarmMonitoringService);
	}

	@Override
	final protected void extractToWriter(final Writer writer) {
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

	protected void fillNullValuesWithZeros(final String[] values) {
		for (int i = 0; i < values.length; i++) {
			if (values[i] == null) {
				values[i] = "0";
			}
		}
	}

	protected abstract void createHeader(final Writer writer, final int maxNumberOfStages) throws IOException;

	protected abstract void addTripleToCSV(final Writer writer, final int maxNumberOfPipes, final PipeMonitoringDataContainer container)
			throws IOException;
}
