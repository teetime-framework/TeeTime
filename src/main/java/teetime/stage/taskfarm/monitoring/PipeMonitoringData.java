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

/**
 * Represents all parameters that are recorded per measurement for one pipe.
 *
 * @author Christian Claus Wiechmann
 */
public class PipeMonitoringData implements IMonitoringData {

	/** number of elements added to the pipe **/
	private final long numPushes;
	/** number of elements removed from the pipe **/
	private final long numPulls;
	/** current amount of elements in the pipe **/
	private final int size;
	/** pipe capacity **/
	private final int capacity;
	/** amount of elements added to the pipe since the last measurement **/
	private final long pushThroughput;
	/** amount of elements removed from the pipe since the last measurement **/
	private final long pullThroughput;
	/** number of wait calls in the pipe **/
	private final int numWaits;
	/** id given to the corresponding measured pipe **/
	private final int uniquePipeId;

	/**
	 * Constructor.
	 *
	 * @param numPushes
	 *            number of elements added to the pipe
	 * @param numPulls
	 *            number of elements removed from the pipe
	 * @param size
	 *            current amount of elements in the pipe
	 * @param capacity
	 *            pipe capacity
	 * @param pushThroughput
	 *            amount of elements added to the pipe since the last measurement
	 * @param pullThroughput
	 *            amount of elements removed from the pipe since the last measurement
	 * @param numWaits
	 *            number of wait calls in the pipe
	 * @param uniquePipeId
	 *            id given to the corresponding measured pipe
	 */
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

	/**
	 * @return number of elements added to the pipe
	 */
	public long getNumPushes() {
		return this.numPushes;
	}

	/**
	 * @return number of elements removed from the pipe
	 */
	public long getNumPulls() {
		return this.numPulls;
	}

	/**
	 * @return current amount of elements in the pipe
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * @return pipe capacity
	 */
	public int getCapacity() {
		return this.capacity;
	}

	/**
	 * @return amount of elements added to the pipe since the last measurement
	 */
	public long getPushThroughput() {
		return this.pushThroughput;
	}

	/**
	 * @return amount of elements removed from the pipe since the last measurement
	 */
	public long getPullThroughput() {
		return this.pullThroughput;
	}

	/**
	 * @return number of wait calls in the pipe
	 */
	public int getNumWaits() {
		return this.numWaits;
	}

	/**
	 * @return id given to the corresponding measured pipe
	 */
	public int getUniquePipeId() {
		return this.uniquePipeId;
	}
}
