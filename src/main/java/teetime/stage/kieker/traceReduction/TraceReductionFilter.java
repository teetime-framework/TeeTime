/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package teetime.stage.kieker.traceReduction;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import teetime.framework.ConsumerStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;

import kieker.analysis.plugin.filter.flow.TraceEventRecords;

/**
 * This filter collects incoming traces for a specified amount of time.
 * Any traces representing the same series of events will be used to calculate statistical informations like the average runtime of this kind of trace.
 * Only one specimen of these traces containing this information will be forwarded from this filter.
 * 
 * Statistical outliers regarding the runtime of the trace will be treated special and therefore send out as they are and will not be mixed with others.
 * 
 * @author Jan Waller, Florian Biss
 * 
 * @since
 */
public class TraceReductionFilter extends ConsumerStage<TraceEventRecords> {

	private final InputPort<Long> triggerInputPort = this.createInputPort();
	private final OutputPort<TraceEventRecords> outputPort = this.createOutputPort();

	private final Map<TraceEventRecords, TraceAggregationBuffer> trace2buffer;

	private long maxCollectionDurationInNs;

	public TraceReductionFilter(final Map<TraceEventRecords, TraceAggregationBuffer> trace2buffer) {
		this.trace2buffer = trace2buffer;
	}

	@Override
	protected void execute(final TraceEventRecords traceEventRecords) {
		Long timestampInNs = this.triggerInputPort.receive();
		if (timestampInNs != null) {
			this.processTimeoutQueue(timestampInNs);
		}

		final long timestamp = System.nanoTime();
		this.countSameTraces(traceEventRecords, timestamp);
	}

	private void countSameTraces(final TraceEventRecords traceEventRecords, final long timestamp) {
		synchronized (this.trace2buffer) {
			TraceAggregationBuffer traceBuffer = this.trace2buffer.get(traceEventRecords);
			if (traceBuffer == null) {
				traceBuffer = new TraceAggregationBuffer(timestamp, traceEventRecords);
				this.trace2buffer.put(traceEventRecords, traceBuffer);
			}
			traceBuffer.count();
		}
	}

	@Override
	public void onIsPipelineHead() {
		synchronized (this.trace2buffer) { // BETTER hide and improve synchronization in the buffer
			for (final Entry<TraceEventRecords, TraceAggregationBuffer> entry : this.trace2buffer.entrySet()) {
				final TraceAggregationBuffer buffer = entry.getValue();
				final TraceEventRecords record = buffer.getTraceEventRecords();
				record.setCount(buffer.getCount());
				this.send(this.outputPort, record);
			}
			this.trace2buffer.clear();
		}

		super.onIsPipelineHead();
	}

	private void processTimeoutQueue(final long timestampInNs) {
		final long bufferTimeoutInNs = timestampInNs - this.maxCollectionDurationInNs;
		synchronized (this.trace2buffer) {
			for (final Iterator<Entry<TraceEventRecords, TraceAggregationBuffer>> iterator = this.trace2buffer.entrySet().iterator(); iterator.hasNext();) {
				final TraceAggregationBuffer traceBuffer = iterator.next().getValue();
				// this.logger.debug("traceBuffer.getBufferCreatedTimestamp(): " + traceBuffer.getBufferCreatedTimestamp() + " vs. " + bufferTimeoutInNs
				// + " (bufferTimeoutInNs)");
				if (traceBuffer.getBufferCreatedTimestamp() <= bufferTimeoutInNs) {
					final TraceEventRecords record = traceBuffer.getTraceEventRecords();
					record.setCount(traceBuffer.getCount());
					this.send(this.outputPort, record);
				}
				iterator.remove();
			}
		}
	}

	public long getMaxCollectionDuration() {
		return this.maxCollectionDurationInNs;
	}

	public void setMaxCollectionDuration(final long maxCollectionDuration) {
		this.maxCollectionDurationInNs = maxCollectionDuration;
	}

	public InputPort<Long> getTriggerInputPort() {
		return this.triggerInputPort;
	}

	public OutputPort<TraceEventRecords> getOutputPort() {
		return this.outputPort;
	}
}
