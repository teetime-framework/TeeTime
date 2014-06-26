package teetime.variant.methodcallWithPorts.examples.traceReconstruction;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import teetime.variant.explicitScheduling.framework.core.Analysis;
import teetime.variant.methodcallWithPorts.framework.core.Pipeline;
import teetime.variant.methodcallWithPorts.framework.core.RunnableStage;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SingleElementPipe;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SpScPipe;
import teetime.variant.methodcallWithPorts.stage.Cache;
import teetime.variant.methodcallWithPorts.stage.Clock;
import teetime.variant.methodcallWithPorts.stage.CollectorSink;
import teetime.variant.methodcallWithPorts.stage.CountingFilter;
import teetime.variant.methodcallWithPorts.stage.EndStage;
import teetime.variant.methodcallWithPorts.stage.InstanceOfFilter;
import teetime.variant.methodcallWithPorts.stage.ThroughputFilter;
import teetime.variant.methodcallWithPorts.stage.kieker.Dir2RecordsFilter;
import teetime.variant.methodcallWithPorts.stage.kieker.className.ClassNameRegistryRepository;
import teetime.variant.methodcallWithPorts.stage.kieker.traceReconstruction.TraceReconstructionFilter;
import teetime.variant.methodcallWithPorts.stage.stringBuffer.StringBufferFilter;
import teetime.variant.methodcallWithPorts.stage.stringBuffer.handler.IMonitoringRecordHandler;
import teetime.variant.methodcallWithPorts.stage.stringBuffer.handler.StringHandler;

import kieker.analysis.plugin.filter.flow.TraceEventRecords;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.flow.IFlowRecord;

public class TraceReconstructionAnalysis extends Analysis {

	private final List<TraceEventRecords> elementCollection = new LinkedList<TraceEventRecords>();

	private Thread producerThread;

	private ClassNameRegistryRepository classNameRegistryRepository;

	private CountingFilter<IMonitoringRecord> recordCounter;

	private CountingFilter<TraceEventRecords> traceCounter;

	private ThroughputFilter<TraceEventRecords> throughputFilter;

	@Override
	public void init() {
		super.init();
		Pipeline<Void, Void> clockPipeline = this.buildClockPipeline();
		this.producerThread = new Thread(new RunnableStage(clockPipeline));

		Pipeline<File, Void> producerPipeline = this.buildProducerPipeline(clockPipeline);
		this.producerThread = new Thread(new RunnableStage(producerPipeline));
	}

	private Pipeline<Void, Void> buildClockPipeline() {
		Clock clock = new Clock();
		clock.setIntervalDelayInMs(1000);

		Pipeline<Void, Void> pipeline = new Pipeline<Void, Void>();
		pipeline.setFirstStage(clock);
		pipeline.setLastStage(new EndStage<Void>());
		return pipeline;
	}

	private Pipeline<File, Void> buildProducerPipeline(final Pipeline<Void, Void> clockPipeline) {
		this.classNameRegistryRepository = new ClassNameRegistryRepository();

		// final IsIMonitoringRecordInRange isIMonitoringRecordInRange = new IsIMonitoringRecordInRange(0, 1000);
		// final IsOperationExecutionRecordTraceIdPredicate isOperationExecutionRecordTraceIdPredicate = new IsOperationExecutionRecordTraceIdPredicate(
		// false, null);
		// create stages
		final Dir2RecordsFilter dir2RecordsFilter = new Dir2RecordsFilter(this.classNameRegistryRepository);
		this.recordCounter = new CountingFilter<IMonitoringRecord>();
		final Cache<IMonitoringRecord> cache = new Cache<IMonitoringRecord>();

		final StringBufferFilter<IMonitoringRecord> stringBufferFilter = new StringBufferFilter<IMonitoringRecord>();
		// final PredicateFilter<IMonitoringRecord> timestampFilter = new PredicateFilter<IMonitoringRecord>(
		// isIMonitoringRecordInRange);
		// final PredicateFilter<OperationExecutionRecord> traceIdFilter = new PredicateFilter<OperationExecutionRecord>(
		// isOperationExecutionRecordTraceIdPredicate);
		final InstanceOfFilter<IMonitoringRecord, IFlowRecord> instanceOfFilter = new InstanceOfFilter<IMonitoringRecord, IFlowRecord>(
				IFlowRecord.class);
		final TraceReconstructionFilter traceReconstructionFilter = new TraceReconstructionFilter();
		this.throughputFilter = new ThroughputFilter<TraceEventRecords>();
		this.traceCounter = new CountingFilter<TraceEventRecords>();
		final CollectorSink<TraceEventRecords> collector = new CollectorSink<TraceEventRecords>(this.elementCollection);

		// configure stages
		stringBufferFilter.getDataTypeHandlers().add(new IMonitoringRecordHandler());
		stringBufferFilter.getDataTypeHandlers().add(new StringHandler());

		// connect stages
		SpScPipe.connect(null, dir2RecordsFilter.getInputPort(), 1);
		SingleElementPipe.connect(dir2RecordsFilter.getOutputPort(), this.recordCounter.getInputPort());
		SingleElementPipe.connect(this.recordCounter.getOutputPort(), cache.getInputPort());
		SingleElementPipe.connect(cache.getOutputPort(), stringBufferFilter.getInputPort());
		SingleElementPipe.connect(stringBufferFilter.getOutputPort(), instanceOfFilter.getInputPort());
		SingleElementPipe.connect(instanceOfFilter.getOutputPort(), traceReconstructionFilter.getInputPort());
		SingleElementPipe.connect(traceReconstructionFilter.getOutputPort(), this.throughputFilter.getInputPort());
		SingleElementPipe.connect(this.throughputFilter.getOutputPort(), this.traceCounter.getInputPort());
		SingleElementPipe.connect(this.traceCounter.getOutputPort(), collector.getInputPort());

		SpScPipe.connect(clockPipeline.getOutputPort(), this.throughputFilter.getTriggerInputPort(), 1);

		// fill input ports
		dir2RecordsFilter.getInputPort().getPipe().add(new File("src/test/data/Eprints-logs"));

		// create and configure pipeline
		Pipeline<File, Void> pipeline = new Pipeline<File, Void>();
		pipeline.setFirstStage(dir2RecordsFilter);
		pipeline.addIntermediateStage(this.recordCounter);
		pipeline.addIntermediateStage(cache);
		pipeline.addIntermediateStage(stringBufferFilter);
		pipeline.addIntermediateStage(instanceOfFilter);
		pipeline.addIntermediateStage(traceReconstructionFilter);
		pipeline.addIntermediateStage(this.throughputFilter);
		pipeline.addIntermediateStage(this.traceCounter);
		pipeline.setLastStage(collector);
		return pipeline;
	}

	@Override
	public void start() {
		super.start();

		this.producerThread.start();

		try {
			this.producerThread.join();
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	public List<TraceEventRecords> getElementCollection() {
		return this.elementCollection;
	}

	public int getNumRecords() {
		return this.recordCounter.getNumElementsPassed();
	}

	public int getNumTraces() {
		return this.traceCounter.getNumElementsPassed();
	}

	public List<Long> getThroughputs() {
		return this.throughputFilter.getThroughputs();
	}
}
