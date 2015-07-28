package teetime.stage.taskfarm.monitoring.extraction;

import java.io.Writer;

import teetime.stage.taskfarm.monitoring.PipeMonitoringService;
import teetime.stage.taskfarm.monitoring.TaskFarmMonitoringService;

public class TimeBoundary2D extends AbstractMonitoringDataExtraction {

	public TimeBoundary2D(final PipeMonitoringService pipeMonitoringService, final TaskFarmMonitoringService taskFarmMonitoringService) {
		super(pipeMonitoringService, taskFarmMonitoringService);
	}

	@Override
	protected void extractToWriter(final Writer writer) {
		// TODO Auto-generated method stub

	}

}
