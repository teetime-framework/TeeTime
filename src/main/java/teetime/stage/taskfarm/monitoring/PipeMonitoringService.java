/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://christianwulf.github.io/teetime)
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

public class PipeMonitoringService implements IMonitoringService<IMonitorablePipe, PipeMonitoringData> {

	private static final long INIT = -1;

	private long startingTimestamp = INIT;
	private final List<IMonitorablePipe> pipes = new LinkedList<IMonitorablePipe>();
	private final List<PipeMonitoringDataContainer> containers = new LinkedList<PipeMonitoringDataContainer>();

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
	public void addMonitoringData() {
		long currentTimestamp = System.currentTimeMillis();
		if (this.startingTimestamp == INIT) {
			this.startingTimestamp = currentTimestamp;
		}

		PipeMonitoringDataContainer container = new PipeMonitoringDataContainer(currentTimestamp - this.startingTimestamp);

		for (int i = 0; i < this.pipes.size(); i++) {
			IMonitorablePipe pipe = this.pipes.get(i);
			if (pipe != null) {
				PipeMonitoringData monitoringData = new PipeMonitoringData(pipe.getNumPushes(),
						pipe.getNumPulls(),
						pipe.size(),
						pipe.capacity(),
						pipe.getPushThroughput(),
						pipe.getPullThroughput(),
						pipe.getNumWaits(),
						i
						);

				container.addMonitoringData(monitoringData);
			}
		}

		this.containers.add(container);
	}

	public List<IMonitorablePipe> getPipes() {
		return this.pipes;
	}

	public class PipeMonitoringDataContainer {
		private final Long time;
		private final List<PipeMonitoringData> monitoringDatas = new LinkedList<PipeMonitoringData>();

		public PipeMonitoringDataContainer(final Long time) {
			this.time = time;
		}

		public void addMonitoringData(final PipeMonitoringData data) {
			this.monitoringDatas.add(data);
		}

		public List<ValueWithId<Long>> getPushThroughputsWithPipeIds() {
			List<ValueWithId<Long>> results = new LinkedList<ValueWithId<Long>>();
			for (PipeMonitoringData data : this.monitoringDatas) {
				results.add(new ValueWithId<Long>(data.getPushThroughput(), data.getUniquePipeId()));
			}
			return results;
		}

		public List<ValueWithId<Long>> getPullThroughputsWithPipeIds() {
			List<ValueWithId<Long>> results = new LinkedList<ValueWithId<Long>>();
			for (PipeMonitoringData data : this.monitoringDatas) {
				results.add(new ValueWithId<Long>(data.getPullThroughput(), data.getUniquePipeId()));
			}
			return results;
		}

		public List<ValueWithId<Integer>> getSizesWithPipeIds() {
			List<ValueWithId<Integer>> results = new LinkedList<ValueWithId<Integer>>();
			for (PipeMonitoringData data : this.monitoringDatas) {
				results.add(new ValueWithId<Integer>(data.getSize(), data.getUniquePipeId()));
			}
			return results;
		}

		public List<ValueWithId<Integer>> getCapacitiesWithPipeIds() {
			List<ValueWithId<Integer>> results = new LinkedList<ValueWithId<Integer>>();
			for (PipeMonitoringData data : this.monitoringDatas) {
				results.add(new ValueWithId<Integer>(data.getCapacity(), data.getUniquePipeId()));
			}
			return results;
		}

		public Long getTime() {
			return time;
		}
	}

	public class ValueWithId<T> {
		private final T value;
		private final Integer id;

		public ValueWithId(final T value, final Integer id) {
			this.value = value;
			this.id = id;
		}

		public T getValue() {
			return this.value;
		}

		public Integer getId() {
			return this.id;
		}
	}
}
