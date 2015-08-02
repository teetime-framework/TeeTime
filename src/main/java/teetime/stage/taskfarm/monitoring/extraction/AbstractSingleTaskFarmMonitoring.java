package teetime.stage.taskfarm.monitoring.extraction;

import java.io.Writer;
import java.util.List;

import teetime.stage.taskfarm.monitoring.PipeMonitoringService;
import teetime.stage.taskfarm.monitoring.SingleTaskFarmMonitoringService;
import teetime.stage.taskfarm.monitoring.TaskFarmMonitoringData;

public abstract class AbstractSingleTaskFarmMonitoring extends AbstractMonitoringDataExtraction {

	public AbstractSingleTaskFarmMonitoring(final PipeMonitoringService pipeMonitoringService, final SingleTaskFarmMonitoringService taskFarmMonitoringService) {
		super(pipeMonitoringService, taskFarmMonitoringService);
	}

	@Override
	protected void extractToWriter(final Writer writer) {
		List<TaskFarmMonitoringData> monitoredDataValues = this.getTaskFarmMonitoringService().getData();

		writeCSVData(writer, monitoredDataValues);
	}

	protected abstract void writeCSVData(final Writer writer, final List<TaskFarmMonitoringData> monitoredDataValues);
}
