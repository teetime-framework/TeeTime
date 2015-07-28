package teetime.stage.taskfarm.monitoring.extraction;

import java.io.Writer;
import java.util.List;
import java.util.Map;

import teetime.stage.taskfarm.TaskFarmStage;
import teetime.stage.taskfarm.monitoring.PipeMonitoringService;
import teetime.stage.taskfarm.monitoring.TaskFarmMonitoringData;
import teetime.stage.taskfarm.monitoring.TaskFarmMonitoringService;

public abstract class AbstractSingleTaskFarmMonitoring extends AbstractMonitoringDataExtraction {

	public AbstractSingleTaskFarmMonitoring(final PipeMonitoringService pipeMonitoringService, final TaskFarmMonitoringService taskFarmMonitoringService) {
		super(pipeMonitoringService, taskFarmMonitoringService);
	}

	@Override
	protected void extractToWriter(final Writer writer) {
		Map<TaskFarmStage<?, ?, ?>, List<TaskFarmMonitoringData>> taskFarmData = this.getTaskFarmMonitoringService().getData();

		List<TaskFarmMonitoringData> monitoredDataValues = extractSingleTaskFarmMonitoringList(taskFarmData);

		writeCSVData(writer, monitoredDataValues);
	}

	protected abstract void writeCSVData(final Writer writer, final List<TaskFarmMonitoringData> monitoredDataValues);

	private List<TaskFarmMonitoringData> extractSingleTaskFarmMonitoringList(final Map<TaskFarmStage<?, ?, ?>, List<TaskFarmMonitoringData>> taskFarmData) {
		if (taskFarmData.values().size() != 1) {
			throw new IllegalArgumentException("This extractor requires a TaskFarmMonitoringService that monitors exactly one Task Farm.");
		}

		List<TaskFarmMonitoringData> monitoredDataValues = null;
		for (List<TaskFarmMonitoringData> loopMonitoredDataValues : taskFarmData.values()) {
			monitoredDataValues = loopMonitoredDataValues;
			break;
		}

		return monitoredDataValues;
	}

}
