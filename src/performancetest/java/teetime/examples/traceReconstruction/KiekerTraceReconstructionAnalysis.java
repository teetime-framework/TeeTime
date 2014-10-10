package teetime.examples.traceReconstruction;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import teetime.framework.OldAnalysis;

import kieker.analysis.AnalysisController;
import kieker.analysis.IAnalysisController;
import kieker.analysis.IProjectContext;
import kieker.analysis.exception.AnalysisConfigurationException;
import kieker.analysis.plugin.filter.AbstractFilterPlugin;
import kieker.analysis.plugin.filter.flow.EventRecordTraceReconstructionFilter;
import kieker.analysis.plugin.filter.flow.TraceEventRecords;
import kieker.analysis.plugin.filter.forward.AnalysisThroughputFilter;
import kieker.analysis.plugin.filter.forward.CountingFilter;
import kieker.analysis.plugin.filter.forward.StringBufferFilter;
import kieker.analysis.plugin.filter.select.TypeFilter;
import kieker.analysis.plugin.reader.AbstractReaderPlugin;
import kieker.analysis.plugin.reader.filesystem.FSReader;
import kieker.analysis.stage.CacheFilter;
import kieker.analysis.stage.CollectorSink;
import kieker.analysis.stage.TimeReader;
import kieker.common.configuration.Configuration;
import kieker.common.record.flow.IFlowRecord;

public class KiekerTraceReconstructionAnalysis extends OldAnalysis {

	private final IAnalysisController analysisController = new AnalysisController();

	private final List<TraceEventRecords> elementCollection = new LinkedList<TraceEventRecords>();
	private final List<Long> throughputCollection = new LinkedList<Long>();

	private CountingFilter recordCounter;
	private CountingFilter traceCounter;
	private AnalysisThroughputFilter throughputFilter;

	private final File inputDir;

	public KiekerTraceReconstructionAnalysis(final File inputDir) {
		this.inputDir = inputDir;
	}

	@Override
	public void init() {
		super.init();

		final Configuration clockConfiguration = new Configuration();
		clockConfiguration.setProperty(TimeReader.CONFIG_PROPERTY_VALUE_UPDATE_INTERVAL_NS, Integer.toString(50 * 1000 * 1000));
		final AbstractReaderPlugin clock = new TimeReader(clockConfiguration, this.analysisController);

		final Configuration readerConfiguration = new Configuration();
		readerConfiguration.setProperty(FSReader.CONFIG_PROPERTY_NAME_INPUTDIRS, this.inputDir.getAbsolutePath());
		final AbstractReaderPlugin reader = new FinalTerminationReader(readerConfiguration, this.analysisController, clock);

		this.recordCounter = new CountingFilter(new Configuration(), this.analysisController);

		final AbstractFilterPlugin cache = new CacheFilter(new Configuration(), this.analysisController);

		final AbstractFilterPlugin stringBufferFilter = new StringBufferFilter(new Configuration(), this.analysisController);

		final Configuration typeFilterConfiguration = new Configuration();
		typeFilterConfiguration.setProperty(TypeFilter.CONFIG_PROPERTY_NAME_TYPES, IFlowRecord.class.getCanonicalName());
		final AbstractFilterPlugin typeFilter = new TypeFilter(typeFilterConfiguration, this.analysisController);

		this.throughputFilter = new AnalysisThroughputFilter(new Configuration(), this.analysisController);
		final EventRecordTraceReconstructionFilter traceReconstructionFilter = new EventRecordTraceReconstructionFilter(new Configuration(), this.analysisController);
		this.traceCounter = new CountingFilter(new Configuration(), this.analysisController);
		final CollectorSink<TraceEventRecords> collector = new CollectorSink<TraceEventRecords>(new Configuration(), this.analysisController, this.elementCollection);

		final CollectorSink<Long> throughputCollector = new CollectorSink<Long>(new Configuration(), this.analysisController, this.throughputCollection);

		try {
			this.analysisController.connect(reader, FSReader.OUTPUT_PORT_NAME_RECORDS, this.recordCounter, CountingFilter.INPUT_PORT_NAME_EVENTS);
			this.analysisController.connect(this.recordCounter, CountingFilter.OUTPUT_PORT_NAME_RELAYED_EVENTS, cache, CacheFilter.INPUT_PORT_NAME);

			this.analysisController.connect(cache, CacheFilter.OUTPUT_PORT_NAME, stringBufferFilter, StringBufferFilter.INPUT_PORT_NAME_EVENTS);

			this.analysisController.connect(stringBufferFilter, StringBufferFilter.OUTPUT_PORT_NAME_RELAYED_EVENTS, typeFilter, TypeFilter.INPUT_PORT_NAME_EVENTS);
			this.analysisController.connect(typeFilter, TypeFilter.OUTPUT_PORT_NAME_TYPE_MATCH, this.throughputFilter,
					AnalysisThroughputFilter.INPUT_PORT_NAME_OBJECTS);
			this.analysisController.connect(this.throughputFilter, AnalysisThroughputFilter.OUTPUT_PORT_NAME_RELAYED_OBJECTS, traceReconstructionFilter,
					EventRecordTraceReconstructionFilter.INPUT_PORT_NAME_TRACE_RECORDS);
			this.analysisController.connect(traceReconstructionFilter, EventRecordTraceReconstructionFilter.OUTPUT_PORT_NAME_TRACE_VALID, this.traceCounter,
					CountingFilter.INPUT_PORT_NAME_EVENTS);
			this.analysisController.connect(traceReconstructionFilter, EventRecordTraceReconstructionFilter.OUTPUT_PORT_NAME_TRACE_INVALID, this.traceCounter,
					CountingFilter.INPUT_PORT_NAME_EVENTS);
			this.analysisController.connect(this.traceCounter, CountingFilter.OUTPUT_PORT_NAME_RELAYED_EVENTS, collector, CollectorSink.INPUT_PORT_NAME);
			this.analysisController.connect(this.throughputFilter, AnalysisThroughputFilter.OUTPUT_PORT_NAME_THROUGHPUT, throughputCollector,
					CollectorSink.INPUT_PORT_NAME);

			this.analysisController.connect(clock, TimeReader.OUTPUT_PORT_NAME_TIMESTAMPS, this.throughputFilter, AnalysisThroughputFilter.INPUT_PORT_NAME_TIME);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (AnalysisConfigurationException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void start() {
		super.start();

		try {
			this.analysisController.run();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (AnalysisConfigurationException e) {
			e.printStackTrace();
		}
	}

	public List<TraceEventRecords> getElementCollection() {
		return this.elementCollection;
	}

	public long getNumRecords() {
		return this.recordCounter.getMessageCount();
	}

	public long getNumTraces() {
		return this.traceCounter.getMessageCount();
	}

	public List<Long> getThroughputs() {
		return this.throughputCollection;
	}

	private static class FinalTerminationReader extends FSReader {

		private final AbstractReaderPlugin clock;

		public FinalTerminationReader(final Configuration configuration, final IProjectContext projectContext, final AbstractReaderPlugin clock) {
			super(configuration, projectContext);

			this.clock = clock;
		}

		@Override
		public boolean read() {
			final boolean result = super.read();

			this.clock.terminate(result);

			return result;
		}

	}

}
