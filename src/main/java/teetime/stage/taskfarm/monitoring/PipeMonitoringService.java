/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.stage.taskfarm.monitoring;

import java.util.LinkedList;
import java.util.List;

import teetime.framework.pipe.IMonitorablePipe;
import teetime.stage.taskfarm.adaptation.history.TaskFarmHistoryService;

/**
 * Represents a monitoring service for implementations of {@link IMonitorablePipe}.
 *
 * @author Christian Claus Wiechmann
 */
public class PipeMonitoringService implements IMonitoringService<IMonitorablePipe, PipeMonitoringData> {

	private static final long INIT = -1;

	/** time of monitoring start to calculate elapsed time **/
	private long startingTimestamp = INIT;
	/** list of monitored pipes **/
	private final List<IMonitorablePipe> pipes = new LinkedList<IMonitorablePipe>();
	/** monitored data **/
	private final List<PipeMonitoringDataContainer> containers = new LinkedList<PipeMonitoringDataContainer>();
	/** task farm history service to access the latest throughput measurement **/
	private final TaskFarmHistoryService<?, ?, ?> history;

	/**
	 * Creates a new pipe monitoring service for task farms using a specified
	 * history service to access throughput measurements.
	 *
	 * @param history
	 *            specified history service
	 */
	public PipeMonitoringService(final TaskFarmHistoryService<?, ?, ?> history) {
		this.history = history;
	}

	/**
	 * Creates a new general pipe monitoring service.
	 */
	public PipeMonitoringService() {
		this(null);
	}

	@Override
	public List<PipeMonitoringDataContainer> getData() {
		return this.containers;
	}

	@Override
	public void addMonitoredItem(final IMonitorablePipe pipe) {
		if (!this.pipes.contains(pipe)) {
			this.pipes.add(pipe);
		}
	}

	@Override
	public void doMeasurement() {
		long currentTimestamp = System.currentTimeMillis();
		if (this.startingTimestamp == INIT) {
			this.startingTimestamp = currentTimestamp;
		}

		PipeMonitoringDataContainer container = new PipeMonitoringDataContainer(currentTimestamp - this.startingTimestamp);

		for (int i = 0; i < this.pipes.size(); i++) {
			IMonitorablePipe pipe = this.pipes.get(i);
			if (pipe != null) {
				// if we use a task farm, we want to use throughput measurements of the history service
				long pullThroughput = 0;
				long pushThroughput = 0;

				if (this.history == null) {
					pullThroughput = pipe.getPullThroughput();
					pushThroughput = pipe.getPushThroughput();
				} else {
					pullThroughput = this.history.getLastPullThroughputOfPipe(pipe);
					pushThroughput = this.history.getLastPushThroughputOfPipe(pipe);
				}

				PipeMonitoringData monitoringData = new PipeMonitoringData(pipe.getNumPushes(),
						pipe.getNumPulls(),
						pipe.size(),
						pipe.capacity(),
						pushThroughput,
						pullThroughput,
						pipe.getNumWaits(),
						i);

				container.addMonitoringData(monitoringData);
			}
		}

		this.containers.add(container);
	}

	/**
	 * @return a list of all monitored pipes
	 */
	public List<IMonitorablePipe> getPipes() {
		return this.pipes;
	}

	/**
	 * Represents a measurement that contains data for each monitored pipe.
	 *
	 * @author Christian Claus Wiechmann
	 */
	public class PipeMonitoringDataContainer {
		/** time of measurement **/
		private final Long time;
		/** data of all pipes for this measurement **/
		private final List<PipeMonitoringData> monitoringDatas = new LinkedList<PipeMonitoringData>();

		/**
		 * Constructor.
		 *
		 * @param time
		 *            time of measurement
		 */
		public PipeMonitoringDataContainer(final Long time) {
			this.time = time;
		}

		/**
		 * Adds data to this measurement.
		 *
		 * @param data
		 *            data to be added
		 */
		public void addMonitoringData(final PipeMonitoringData data) {
			this.monitoringDatas.add(data);
		}

		/**
		 * @return push throughput measurements with their corresponding pipe ids
		 */
		public List<ValueWithId<Long>> getPushThroughputsWithPipeIds() {
			List<ValueWithId<Long>> results = new LinkedList<ValueWithId<Long>>();
			for (PipeMonitoringData data : this.monitoringDatas) {
				results.add(new ValueWithId<Long>(data.getPushThroughput(), data.getUniquePipeId()));
			}
			return results;
		}

		/**
		 * @return pull throughput measurements with their corresponding pipe ids
		 */
		public List<ValueWithId<Long>> getPullThroughputsWithPipeIds() {
			List<ValueWithId<Long>> results = new LinkedList<ValueWithId<Long>>();
			for (PipeMonitoringData data : this.monitoringDatas) {
				results.add(new ValueWithId<Long>(data.getPullThroughput(), data.getUniquePipeId()));
			}
			return results;
		}

		/**
		 * @return pipe size measurements with their corresponding pipe ids
		 */
		public List<ValueWithId<Integer>> getSizesWithPipeIds() {
			List<ValueWithId<Integer>> results = new LinkedList<ValueWithId<Integer>>();
			for (PipeMonitoringData data : this.monitoringDatas) {
				results.add(new ValueWithId<Integer>(data.getSize(), data.getUniquePipeId()));
			}
			return results;
		}

		/**
		 * @return pipe capacity measurements with their corresponding pipe ids
		 */
		public List<ValueWithId<Integer>> getCapacitiesWithPipeIds() {
			List<ValueWithId<Integer>> results = new LinkedList<ValueWithId<Integer>>();
			for (PipeMonitoringData data : this.monitoringDatas) {
				results.add(new ValueWithId<Integer>(data.getCapacity(), data.getUniquePipeId()));
			}
			return results;
		}

		/**
		 * @return time of this measurement
		 */
		public Long getTime() {
			return this.time;
		}
	}

	/**
	 * Represents a value-id-pair.
	 *
	 * @author Christian Claus Wiechmann
	 *
	 * @param <T>
	 *            value type
	 */
	public class ValueWithId<T> {
		private final T value;
		private final Integer id;

		/**
		 * Constructor.
		 *
		 * @param value
		 * @param id
		 */
		public ValueWithId(final T value, final Integer id) {
			this.value = value;
			this.id = id;
		}

		/**
		 * @return value
		 */
		public T getValue() {
			return this.value;
		}

		/**
		 * @return id
		 */
		public Integer getId() {
			return this.id;
		}
	}
}
