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
	private final List<ImmutableTriple<Long, List<Integer>, List<Integer>>> timeCapacitiesSizes =
			new LinkedList<ImmutableTriple<Long, List<Integer>, List<Integer>>>();
	private final List<ImmutableTriple<Long, List<Long>, List<Long>>> timePushPullThroughput =
			new LinkedList<ImmutableTriple<Long, List<Long>, List<Long>>>();

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

		List<Integer> capacities = new LinkedList<Integer>();
		List<Integer> sizes = new LinkedList<Integer>();
		List<Long> pushThroughputs = new LinkedList<Long>();
		List<Long> pullThroughputs = new LinkedList<Long>();

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

				capacities.add(pipe.capacity());
				sizes.add(pipe.size());
				pushThroughputs.add(pipe.getPushThroughput());
				pullThroughputs.add(pipe.getPullThroughput());
			}
		}

		ImmutableTriple<Long, List<Integer>, List<Integer>> timeCapacitiesSizesEntry = new ImmutableTriple<Long, List<Integer>, List<Integer>>(
				this.startingTimestamp - currentTimestamp, capacities, sizes);
		this.timeCapacitiesSizes.add(timeCapacitiesSizesEntry);

		ImmutableTriple<Long, List<Long>, List<Long>> timePushPullThroughputEntry = new ImmutableTriple<Long, List<Long>, List<Long>>(
				this.startingTimestamp - currentTimestamp, pushThroughputs, pullThroughputs);
		this.timePushPullThroughput.add(timePushPullThroughputEntry);
	}

	public List<ImmutableTriple<Long, List<Integer>, List<Integer>>> getTimeCapacitiesSizes() {
		return timeCapacitiesSizes;
	}

	public List<ImmutableTriple<Long, List<Long>, List<Long>>> getTimePushPullThroughput() {
		return timePushPullThroughput;
	}

	public class ImmutableTriple<A, B, C> {
		private final A a;
		private final B b;
		private final C c;

		public ImmutableTriple(final A a, final B b, final C c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}

		public A getA() {
			return a;
		}

		public B getB() {
			return b;
		}

		public C getC() {
			return c;
		}
	}
}
