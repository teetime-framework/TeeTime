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
package teetime.variant.methodcallWithPorts.stage.kieker.traceReconstruction;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import teetime.util.concurrent.hashmap.ConcurrentHashMapWithDefault;
import teetime.util.concurrent.hashmap.TraceBuffer;
import teetime.variant.methodcallWithPorts.framework.core.ConsumerStage;

import kieker.analysis.plugin.filter.flow.TraceEventRecords;
import kieker.common.record.flow.IFlowRecord;
import kieker.common.record.flow.trace.AbstractTraceEvent;
import kieker.common.record.flow.trace.TraceMetadata;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
public class TraceReconstructionFilter extends ConsumerStage<IFlowRecord, TraceEventRecords> {

	private TimeUnit timeunit;
	private long maxTraceDuration = Long.MAX_VALUE;
	private long maxTraceTimeout = Long.MAX_VALUE;
	private boolean timeout;
	private long maxEncounteredLoggingTimestamp = -1;

	private static final Map<Long, TraceBuffer> traceId2trace = new ConcurrentHashMapWithDefault<Long, TraceBuffer>(new TraceBuffer());

	@Override
	protected void execute5(final IFlowRecord element) {
		final Long traceId = this.reconstructTrace(element);
		if (traceId != null) {
			this.putIfFinished(traceId);
			this.processTimestamp(element);
		}
	}

	private void processTimestamp(final IFlowRecord record) {
		if (this.timeout) {
			synchronized (this) {
				final long loggingTimestamp = this.getTimestamp(record);
				// can we assume a rough order of logging timestamps? (yes, except with DB reader)
				if (loggingTimestamp > this.maxEncounteredLoggingTimestamp) {
					this.maxEncounteredLoggingTimestamp = loggingTimestamp;
				}
				this.processTimeoutQueue(this.maxEncounteredLoggingTimestamp);
			}
		}
	}

	private long getTimestamp(final IFlowRecord record) {
		if (record instanceof AbstractTraceEvent) {
			return ((AbstractTraceEvent) record).getTimestamp();
		}
		return -1;
	}

	private void putIfFinished(final Long traceId) {
		final TraceBuffer traceBuffer = TraceReconstructionFilter.traceId2trace.get(traceId);
		if (traceBuffer.isFinished()) {
			synchronized (this) { // has to be synchronized because of timeout cleanup
				TraceReconstructionFilter.traceId2trace.remove(traceId);
			}
			this.put(traceBuffer);
		}
	}

	private Long reconstructTrace(final IFlowRecord record) {
		Long traceId = null;
		if (record instanceof TraceMetadata) {
			traceId = ((TraceMetadata) record).getTraceId();
			final TraceBuffer traceBuffer = TraceReconstructionFilter.traceId2trace.get(traceId);

			traceBuffer.setTrace((TraceMetadata) record);
		} else if (record instanceof AbstractTraceEvent) {
			traceId = ((AbstractTraceEvent) record).getTraceId();
			final TraceBuffer traceBuffer = TraceReconstructionFilter.traceId2trace.get(traceId);

			traceBuffer.insertEvent((AbstractTraceEvent) record);
		}

		return traceId;
	}

	@Override
	public void onStart() {
		this.timeout = !((this.maxTraceTimeout == Long.MAX_VALUE) && (this.maxTraceDuration == Long.MAX_VALUE));
		super.onStart();
	}

	@Override
	public void onIsPipelineHead() {
		Iterator<TraceBuffer> iterator = TraceReconstructionFilter.traceId2trace.values().iterator();
		while (iterator.hasNext()) {
			TraceBuffer traceBuffer = iterator.next();
			this.put(traceBuffer);
			iterator.remove();
		}

		super.onIsPipelineHead();
	}

	private void processTimeoutQueue(final long timestamp) {
		final long duration = timestamp - this.maxTraceDuration;
		final long traceTimeout = timestamp - this.maxTraceTimeout;

		for (final Iterator<Entry<Long, TraceBuffer>> iterator = TraceReconstructionFilter.traceId2trace.entrySet().iterator(); iterator.hasNext();) {
			final TraceBuffer traceBuffer = iterator.next().getValue();
			if ((traceBuffer.getMaxLoggingTimestamp() <= traceTimeout) // long time no see
					|| (traceBuffer.getMinLoggingTimestamp() <= duration)) { // max duration is gone
				this.put(traceBuffer);
				iterator.remove();
			}
		}
	}

	private void put(final TraceBuffer traceBuffer) {
		// final IOutputPort<TraceReconstructionFilter, TraceEventRecords> outputPort =
		// (traceBuffer.isInvalid()) ? this.traceInvalidOutputPort : this.traceValidOutputPort;
		// context.put(outputPort, traceBuffer.toTraceEvents());
		this.send(traceBuffer.toTraceEvents());
	}

	public TimeUnit getTimeunit() {
		return this.timeunit;
	}

	public void setTimeunit(final TimeUnit timeunit) {
		this.timeunit = timeunit;
	}

	public long getMaxTraceDuration() {
		return this.maxTraceDuration;
	}

	public void setMaxTraceDuration(final long maxTraceDuration) {
		this.maxTraceDuration = maxTraceDuration;
	}

	public long getMaxTraceTimeout() {
		return this.maxTraceTimeout;
	}

	public void setMaxTraceTimeout(final long maxTraceTimeout) {
		this.maxTraceTimeout = maxTraceTimeout;
	}

	public long getMaxEncounteredLoggingTimestamp() {
		return this.maxEncounteredLoggingTimestamp;
	}

	public void setMaxEncounteredLoggingTimestamp(final long maxEncounteredLoggingTimestamp) {
		this.maxEncounteredLoggingTimestamp = maxEncounteredLoggingTimestamp;
	}

	// public Map<Long, TraceBuffer> getTraceId2trace() {
	// return TraceReconstructionFilter.traceId2trace;
	// }
	//
	// public void setTraceId2trace(final Map<Long, TraceBuffer> traceId2trace) {
	// TraceReconstructionFilter.traceId2trace = traceId2trace;
	// }

}
