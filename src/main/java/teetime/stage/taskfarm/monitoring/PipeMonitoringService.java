package teetime.stage.taskfarm.monitoring;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import teetime.framework.pipe.IMonitorablePipe;

public class PipeMonitoringService {

	private static final long INIT = -1;

	private long startingTimestamp = INIT;

	private final Map<IMonitorablePipe, List<MonitoringData>> data = new HashMap<IMonitorablePipe, List<MonitoringData>>();
	private final List<IMonitorablePipe> pipes = new LinkedList<IMonitorablePipe>();

	public Map<IMonitorablePipe, List<MonitoringData>> getData() {
		return this.data;
	}

	public void addPipe(final IMonitorablePipe pipe) {
		this.pipes.add(pipe);
		List<MonitoringData> pipeValues = new LinkedList<MonitoringData>();
		this.data.put(pipe, pipeValues);
	}

	public void addMonitoringData() {
		long currentTimestamp = System.currentTimeMillis();
		if (this.startingTimestamp == INIT) {
			this.startingTimestamp = currentTimestamp;
		}

		for (IMonitorablePipe pipe : this.pipes) {
			MonitoringData monitoringData = null;
			try {
				monitoringData = new MonitoringData(this.startingTimestamp - currentTimestamp,
						pipe.getNumPushes(),
						pipe.getNumPulls(),
						pipe.size(),
						pipe.capacity(),
						pipe.getPushThroughput(),
						pipe.getPullThroughput(),
						pipe.getNumWaits());
			} catch (NullPointerException e) {
				// data extraction from pipe was not successful, write zero values
				monitoringData = new MonitoringData(this.startingTimestamp - currentTimestamp,
						0,
						0,
						0,
						0,
						0,
						0,
						0);
			}
			List<MonitoringData> pipeValues = this.data.get(pipe);
			pipeValues.add(monitoringData);
		}
	}
}
