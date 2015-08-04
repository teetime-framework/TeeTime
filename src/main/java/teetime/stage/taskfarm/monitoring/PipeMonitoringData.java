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

public class PipeMonitoringData implements IMonitoringData {

	private final long numPushes;
	private final long numPulls;
	private final int size;
	private final int capacity;
	private final long pushThroughput;
	private final long pullThroughput;
	private final int numWaits;
	private final int uniquePipeId;

	PipeMonitoringData(final long numPushes, final long numPulls, final int size, final int capacity, final long pushThroughput,
			final long pullThroughput, final int numWaits, final int uniquePipeId) {
		this.numPushes = numPushes;
		this.numPulls = numPulls;
		this.size = size;
		this.capacity = capacity;
		this.pushThroughput = pushThroughput;
		this.pullThroughput = pullThroughput;
		this.numWaits = numWaits;
		this.uniquePipeId = uniquePipeId;
	}

	public long getNumPushes() {
		return numPushes;
	}

	public long getNumPulls() {
		return numPulls;
	}

	public int getSize() {
		return size;
	}

	public int getCapacity() {
		return capacity;
	}

	public long getPushThroughput() {
		return pushThroughput;
	}

	public long getPullThroughput() {
		return pullThroughput;
	}

	public int getNumWaits() {
		return numWaits;
	}

	public int getUniquePipeId() {
		return uniquePipeId;
	}
}
