package teetime.stage.taskfarm.monitoring.extraction;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import teetime.stage.taskfarm.monitoring.PipeMonitoringService;
import teetime.stage.taskfarm.monitoring.SingleTaskFarmMonitoringService;
import teetime.stage.taskfarm.monitoring.TaskFarmMonitoringData;

public class TimeBoundary2D extends AbstractSingleTaskFarmMonitoring {

	public TimeBoundary2D(final PipeMonitoringService pipeMonitoringService, final SingleTaskFarmMonitoringService taskFarmMonitoringService) {
		super(pipeMonitoringService, taskFarmMonitoringService);
	}

	@Override
	protected void writeCSVData(final Writer writer, final List<TaskFarmMonitoringData> monitoredDataValues) {
		try {
			addCSVLineToWriter(writer, "time", "boundary");

			if (monitoredDataValues.size() > 0) {
				// just add last time-boundary pair so that it will record the duration of the monitoring
				TaskFarmMonitoringData taskFarmMonitoringData = monitoredDataValues.get(monitoredDataValues.size() - 1);
				addCSVLineToWriter(writer,
						Long.toString(taskFarmMonitoringData.getTime()),
						Double.toString(taskFarmMonitoringData.getThroughputBoundary()));
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("The writer could not be written to: " + e.getMessage());
		}
	}

}
