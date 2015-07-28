package teetime.stage.taskfarm.monitoring.extraction;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import teetime.stage.taskfarm.monitoring.PipeMonitoringService;
import teetime.stage.taskfarm.monitoring.TaskFarmMonitoringData;
import teetime.stage.taskfarm.monitoring.TaskFarmMonitoringService;

public class TimeBoundaryStages3D extends AbstractSingleTaskFarmMonitoring {

	public TimeBoundaryStages3D(final PipeMonitoringService pipeMonitoringService, final TaskFarmMonitoringService taskFarmMonitoringService) {
		super(pipeMonitoringService, taskFarmMonitoringService);
	}

	@Override
	protected void writeCSVData(final Writer writer, final List<TaskFarmMonitoringData> monitoredDataValues) {
		try {
			addCSVLineToWriter(writer, "time", "boundary", "stages");

			for (TaskFarmMonitoringData taskFarmMonitoringData : monitoredDataValues) {
				addCSVLineToWriter(writer,
						Long.toString(taskFarmMonitoringData.getTime()),
						Double.toString(taskFarmMonitoringData.getThroughputBoundary()),
						Integer.toString(taskFarmMonitoringData.getStages()));
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("The writer could not be written to: " + e.getMessage());
		}
	}
}
