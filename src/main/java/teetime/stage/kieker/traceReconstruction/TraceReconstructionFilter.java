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
package teetime.stage.kieker.traceReconstruction;

import java.util.concurrent.TimeUnit;

import teetime.framework.ConsumerStage;
import teetime.framework.OutputPort;
import teetime.util.concurrent.hashmap.ConcurrentHashMapWithDefault;
import teetime.util.concurrent.hashmap.TraceBuffer;

import kieker.analysis.plugin.filter.flow.TraceEventRecords;
import kieker.common.record.flow.IFlowRecord;
import kieker.common.record.flow.trace.AbstractTraceEvent;
import kieker.common.record.flow.trace.TraceMetadata;

/**
 * @author Christian Wulf
 *
 * @since 1.10
 */
public class TraceReconstructionFilter extends ConsumerStage<IFlowRecord> {

	private final OutputPort<TraceEventRecords> traceValidOutputPort = this.createOutputPort();
	private final OutputPort<TraceEventRecords> traceInvalidOutputPort = this.createOutputPort(); // TODO send output to this port

	private TimeUnit timeunit;
	private long maxTraceDuration = Long.MAX_VALUE;
	private long maxTraceTimeout = Long.MAX_VALUE;
	private long maxEncounteredLoggingTimestamp = -1;

	private final ConcurrentHashMapWithDefault<Long, TraceBuffer> traceId2trace;

	public TraceReconstructionFilter(final ConcurrentHashMapWithDefault<Long, TraceBuffer> traceId2trace) {
		super();
		this.traceId2trace = traceId2trace;
	}

	@Override
	protected void execute(final IFlowRecord element) {
		final Long traceId = this.reconstructTrace(element);
		if (traceId != null) {
			this.put(traceId, true);
		}
	}

	private void put(final Long traceId, final boolean onlyIfFinished) {
		final TraceBuffer traceBuffer = this.traceId2trace.get(traceId);
		if (traceBuffer != null) { // null-check to check whether the trace has already been sent and removed
			boolean shouldSend;
			if (onlyIfFinished) {
				shouldSend = traceBuffer.isFinished();
			} else {
				shouldSend = true;
			}

			if (shouldSend) {
				boolean removed = (null != this.traceId2trace.remove(traceId));
				if (removed) {
					this.sendTraceBuffer(traceBuffer);
				}
			}
		}
	}

	private Long reconstructTrace(final IFlowRecord record) {
		Long traceId = null;
		if (record instanceof TraceMetadata) {
			traceId = ((TraceMetadata) record).getTraceId();
			TraceBuffer traceBuffer = this.traceId2trace.getOrCreate(traceId);

			traceBuffer.setTrace((TraceMetadata) record);
		} else if (record instanceof AbstractTraceEvent) {
			traceId = ((AbstractTraceEvent) record).getTraceId();
			TraceBuffer traceBuffer = this.traceId2trace.getOrCreate(traceId);

			traceBuffer.insertEvent((AbstractTraceEvent) record);
		}

		return traceId;
	}

	@Override
	public void onTerminating() {
		super.onTerminating();
		for (Long traceId : this.traceId2trace.keySet()) {
			this.put(traceId, false);
		}

		super.onTerminating();
	}

	private void sendTraceBuffer(final TraceBuffer traceBuffer) {
		OutputPort<TraceEventRecords> outputPort = (traceBuffer.isInvalid()) ? this.traceInvalidOutputPort
				: this.traceValidOutputPort;
		this.send(outputPort, traceBuffer.toTraceEvents());
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

	public OutputPort<TraceEventRecords> getTraceValidOutputPort() {
		return this.traceValidOutputPort;
	}

	public OutputPort<TraceEventRecords> getTraceInvalidOutputPort() {
		return this.traceInvalidOutputPort;
	}

	// public Map<Long, TraceBuffer> getTraceId2trace() {
	// return TraceReconstructionFilter.traceId2trace;
	// }
	//
	// public void setTraceId2trace(final Map<Long, TraceBuffer> traceId2trace) {
	// TraceReconstructionFilter.traceId2trace = traceId2trace;
	// }

}
