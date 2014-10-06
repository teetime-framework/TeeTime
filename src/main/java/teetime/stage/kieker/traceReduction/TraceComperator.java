package teetime.stage.kieker.traceReduction;

import java.io.Serializable;
import java.util.Comparator;

import kieker.analysis.plugin.filter.flow.TraceEventRecords;
import kieker.common.record.flow.trace.AbstractTraceEvent;
import kieker.common.record.flow.trace.operation.AbstractOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationFailedEvent;

/**
 * @author Jan Waller, Florian Fittkau, Florian Biss
 */
public final class TraceComperator implements Comparator<TraceEventRecords>, Serializable {
	private static final long serialVersionUID = 8920766818232517L;

	/**
	 * Creates a new instance of this class.
	 */
	public TraceComperator() {
		// default empty constructor
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(final TraceEventRecords t1, final TraceEventRecords t2) {
		final AbstractTraceEvent[] recordsT1 = t1.getTraceEvents();
		final AbstractTraceEvent[] recordsT2 = t2.getTraceEvents();

		if (recordsT1.length != recordsT2.length) {
			return recordsT1.length - recordsT2.length;
		}

		final int cmpHostnames = t1.getTraceMetadata().getHostname()
				.compareTo(t2.getTraceMetadata().getHostname());
		if (cmpHostnames != 0) {
			return cmpHostnames;
		}

		for (int i = 0; i < recordsT1.length; i++) {
			final AbstractTraceEvent recordT1 = recordsT1[i];
			final AbstractTraceEvent recordT2 = recordsT2[i];

			final int cmpClass = recordT1.getClass().getName()
					.compareTo(recordT2.getClass().getName());
			if (cmpClass != 0) {
				return cmpClass;
			}
			if (recordT1 instanceof AbstractOperationEvent) {
				final int cmpSignature = ((AbstractOperationEvent) recordT1).getOperationSignature()
						.compareTo(((AbstractOperationEvent) recordT2).getOperationSignature());
				if (cmpSignature != 0) {
					return cmpSignature;
				}
			}
			if (recordT1 instanceof AfterOperationFailedEvent) {
				final int cmpError = ((AfterOperationFailedEvent) recordT1).getCause().compareTo(
						((AfterOperationFailedEvent) recordT2).getCause());
				if (cmpError != 0) {
					return cmpClass;
				}
			}
		}
		// All records match.
		return 0;
	}
}
