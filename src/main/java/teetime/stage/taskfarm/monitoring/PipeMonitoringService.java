package teetime.stage.taskfarm.monitoring;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import teetime.framework.pipe.IMonitorablePipe;

public class PipeMonitoringService implements IMonitoringService<IMonitorablePipe, PipeMonitoringData> {

	private static final long INIT = -1;

	private long startingTimestamp = INIT;

	private final Map<IMonitorablePipe, List<PipeMonitoringData>> data = new HashMap<IMonitorablePipe, List<PipeMonitoringData>>();

	@Override
	public Map<IMonitorablePipe, List<PipeMonitoringData>> getData() {
		return this.data;
	}

	@Override
	public void addMonitoredItem(final IMonitorablePipe pipe) {
		if (!data.containsKey(pipe)) {
			List<PipeMonitoringData> pipeValues = new LinkedList<PipeMonitoringData>();
			this.data.put(pipe, pipeValues);
		}
	}

	@Override
	public void addMonitoringData() {
		long currentTimestamp = System.currentTimeMillis();
		if (this.startingTimestamp == INIT) {
			this.startingTimestamp = currentTimestamp;
		}

		for (IMonitorablePipe pipe : this.data.keySet()) {
			if (pipe != null) {
				PipeMonitoringData monitoringData = new PipeMonitoringData(this.startingTimestamp - currentTimestamp,
						pipe.getNumPushes(),
						pipe.getNumPulls(),
						pipe.size(),
						pipe.capacity(),
						pipe.getPushThroughput(),
						pipe.getPullThroughput(),
						pipe.getNumWaits());

				List<PipeMonitoringData> pipeValues = this.data.get(pipe);
				pipeValues.add(monitoringData);
			}
		}
	}
}
