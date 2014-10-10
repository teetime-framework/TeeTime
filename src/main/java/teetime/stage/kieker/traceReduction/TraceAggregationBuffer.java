package teetime.stage.kieker.traceReduction;

import kieker.analysis.plugin.filter.flow.TraceEventRecords;

/**
 * Buffer for similar traces that are to be aggregated into a single trace.
 * 
 * @author Jan Waller, Florian Biss
 */
public final class TraceAggregationBuffer {
	private final long bufferCreatedTimestamp;
	private final TraceEventRecords aggregatedTrace;

	private int countOfAggregatedTraces;

	public TraceAggregationBuffer(final long bufferCreatedTimestamp, final TraceEventRecords trace) {
		this.bufferCreatedTimestamp = bufferCreatedTimestamp;
		this.aggregatedTrace = trace;
	}

	public void count() {
		this.countOfAggregatedTraces++;
	}

	public long getBufferCreatedTimestamp() {
		return this.bufferCreatedTimestamp;
	}

	public TraceEventRecords getTraceEventRecords() {
		return this.aggregatedTrace;
	}

	public int getCount() {
		return this.countOfAggregatedTraces;
	}
}
