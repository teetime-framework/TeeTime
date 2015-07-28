package teetime.stage.taskfarm.monitoring.extraction;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import teetime.stage.taskfarm.monitoring.PipeMonitoringService;
import teetime.stage.taskfarm.monitoring.TaskFarmMonitoringData;
import teetime.stage.taskfarm.monitoring.TaskFarmMonitoringService;

public class TimeBoundary2D extends AbstractSingleTaskFarmMonitoring {

	public TimeBoundary2D(final PipeMonitoringService pipeMonitoringService, final TaskFarmMonitoringService taskFarmMonitoringService) {
		super(pipeMonitoringService, taskFarmMonitoringService);
	}

	@Override
	protected void writeCSVData(final Writer writer, final List<TaskFarmMonitoringData> monitoredDataValues) {
		try {
			addCSVLineToWriter(writer, "time", "boundary");

			// just add last time-boundary pair so that it will record the analysis duration
			TaskFarmMonitoringData taskFarmMonitoringData = monitoredDataValues.get(monitoredDataValues.size() - 1);
			addCSVLineToWriter(writer,
					Long.toString(taskFarmMonitoringData.getTime()),
					Double.toString(taskFarmMonitoringData.getThroughputBoundary()));
		} catch (IOException e) {
			throw new IllegalArgumentException("The writer could not be written to: " + e.getMessage());
		}
	}

}
