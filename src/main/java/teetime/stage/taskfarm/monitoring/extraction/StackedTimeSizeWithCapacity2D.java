package teetime.stage.taskfarm.monitoring.extraction;

import java.io.Writer;
import java.util.List;
import java.util.Map;

import teetime.framework.pipe.IMonitorablePipe;
import teetime.stage.taskfarm.monitoring.PipeMonitoringData;
import teetime.stage.taskfarm.monitoring.PipeMonitoringService;
import teetime.stage.taskfarm.monitoring.TaskFarmMonitoringService;

public class StackedTimeSizeWithCapacity2D extends AbstractMonitoringDataExtraction {

	public StackedTimeSizeWithCapacity2D(final PipeMonitoringService pipeMonitoringService, final TaskFarmMonitoringService taskFarmMonitoringService) {
		super(pipeMonitoringService, taskFarmMonitoringService);
	}

	@Override
	protected void extractToWriter(final Writer writer) {
		Map<IMonitorablePipe, List<PipeMonitoringData>> data = getPipeMonitoringService().getData();
		int maxNumberOfStages = data.keySet().size();
	}

}
