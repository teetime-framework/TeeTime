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

	private final List<TimeCapacitiesSizes> timeCapacitiesSizes =
			new LinkedList<TimeCapacitiesSizes>();

	private final List<TimePushPullThroughputs> timePushPullThroughputs =
			new LinkedList<TimePushPullThroughputs>();

	private final List<IMonitorablePipe> pipes = new LinkedList<IMonitorablePipe>();

	@Override
	public Map<IMonitorablePipe, List<PipeMonitoringData>> getData() {
		return this.data;
	}

	@Override
	public void addMonitoredItem(final IMonitorablePipe pipe) {
		if (!data.containsKey(pipe)) {
			List<PipeMonitoringData> pipeValues = new LinkedList<PipeMonitoringData>();
			this.data.put(pipe, pipeValues);
			this.pipes.add(pipe);
		}
	}

	@Override
	public void addMonitoringData() {
		long currentTimestamp = System.currentTimeMillis();
		if (this.startingTimestamp == INIT) {
			this.startingTimestamp = currentTimestamp;
		}

		List<Integer> capacities = new LinkedList<Integer>();
		List<Integer> sizes = new LinkedList<Integer>();
		List<Integer> pipeIndizes = new LinkedList<Integer>();
		List<Long> pushThroughputs = new LinkedList<Long>();
		List<Long> pullThroughputs = new LinkedList<Long>();

		for (IMonitorablePipe pipe : this.pipes) {
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

				capacities.add(pipe.capacity());
				sizes.add(pipe.size());
				pipeIndizes.add(this.pipes.indexOf(pipe));
				pushThroughputs.add(pipe.getPushThroughput());
				pullThroughputs.add(pipe.getPullThroughput());
			}
		}

		// PipeMonitoringDataContainer c = new (monitoringDatas);
		// c.getPushThroughputs();
		// c.getPullThroughputs();

		TimeCapacitiesSizes timeCapacitiesSizesEntry = new TimeCapacitiesSizes(
				this.startingTimestamp - currentTimestamp, capacities, sizes, pipeIndizes);
		this.timeCapacitiesSizes.add(timeCapacitiesSizesEntry);

		TimePushPullThroughputs timePushPullThroughputEntry = new TimePushPullThroughputs(
				this.startingTimestamp - currentTimestamp, pushThroughputs, pullThroughputs, pipeIndizes);
		this.timePushPullThroughputs.add(timePushPullThroughputEntry);
	}

	public List<TimeCapacitiesSizes> getTimeCapacitiesSizes() {
		return timeCapacitiesSizes;
	}

	public List<TimePushPullThroughputs> getTimePushPullThroughput() {
		return timePushPullThroughputs;
	}

	public List<IMonitorablePipe> getPipes() {
		return pipes;
	}

	public class TimeCapacitiesSizes {
		private final Long time;
		private final List<Integer> capacities;
		private final List<Integer> sizes;
		private final List<Integer> pipeIndizes;

		TimeCapacitiesSizes(final Long time, final List<Integer> capacities, final List<Integer> sizes, final List<Integer> pipeIndizes) {
			this.time = time;
			this.capacities = capacities;
			this.sizes = sizes;
			this.pipeIndizes = pipeIndizes;
		}

		public Long getTime() {
			return time;
		}

		public List<Integer> getCapacities() {
			return capacities;
		}

		public List<Integer> getSizes() {
			return sizes;
		}

		public List<Integer> getPipeIndizes() {
			return pipeIndizes;
		}
	}

	public class TimePushPullThroughputs {
		private final Long time;
		private final List<Long> pushThroughputs;
		private final List<Long> pullThroughputs;
		private final List<Integer> pipeIndizes;

		TimePushPullThroughputs(final Long time, final List<Long> pushThroughputs, final List<Long> pullThroughputs, final List<Integer> pipeIndizes) {
			this.time = time;
			this.pushThroughputs = pushThroughputs;
			this.pullThroughputs = pullThroughputs;
			this.pipeIndizes = pipeIndizes;
		}

		public Long getTime() {
			return time;
		}

		public List<Long> getPushThroughputs() {
			return pushThroughputs;
		}

		public List<Long> getPullThroughputs() {
			return pullThroughputs;
		}

		public List<Integer> getPipeIndizes() {
			return pipeIndizes;
		}
	}
}
