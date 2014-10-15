package teetime.examples.traceReconstruction;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import teetime.framework.HeadPipeline;
import teetime.framework.RunnableStage;
import teetime.framework.pipe.SingleElementPipe;
import teetime.framework.pipe.SpScPipe;
import teetime.stage.Cache;
import teetime.stage.Clock;
import teetime.stage.CollectorSink;
import teetime.stage.Counter;
import teetime.stage.ElementThroughputMeasuringStage;
import teetime.stage.InitialElementProducer;
import teetime.stage.InstanceOfFilter;
import teetime.stage.basic.merger.Merger;
import teetime.stage.kieker.Dir2RecordsFilter;
import teetime.stage.kieker.className.ClassNameRegistryRepository;
import teetime.stage.kieker.traceReconstruction.TraceReconstructionFilter;
import teetime.stage.stringBuffer.StringBufferFilter;
import teetime.stage.stringBuffer.handler.IMonitoringRecordHandler;
import teetime.stage.stringBuffer.handler.StringHandler;
import teetime.util.concurrent.hashmap.ConcurrentHashMapWithDefault;
import teetime.util.concurrent.hashmap.TraceBuffer;

import kieker.analysis.plugin.filter.flow.TraceEventRecords;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.flow.IFlowRecord;

public class TraceReconstructionAnalysis {

	private final List<TraceEventRecords> elementCollection = new LinkedList<TraceEventRecords>();

	private Thread clockThread;
	private Thread workerThread;

	private ClassNameRegistryRepository classNameRegistryRepository;
	private final ConcurrentHashMapWithDefault<Long, TraceBuffer> traceId2trace = new ConcurrentHashMapWithDefault<Long, TraceBuffer>(new TraceBuffer());

	private Counter<IMonitoringRecord> recordCounter;
	private Counter<TraceEventRecords> traceCounter;
	private ElementThroughputMeasuringStage<IFlowRecord> throughputFilter;

	private File inputDir;

	public void init() {
		Clock clockStage = this.buildClockPipeline();
		this.clockThread = new Thread(new RunnableStage(clockStage));

		HeadPipeline<?, ?> pipeline = this.buildPipeline(clockStage);
		this.workerThread = new Thread(new RunnableStage(pipeline));
	}

	private Clock buildClockPipeline() {
		Clock clock = new Clock();
		clock.setIntervalDelayInMs(100);

		return clock;
	}

	private HeadPipeline<?, ?> buildPipeline(final Clock clockStage) {
		this.classNameRegistryRepository = new ClassNameRegistryRepository();

		// create stages
		InitialElementProducer<File> initialElementProducer = new InitialElementProducer<File>(this.inputDir);
		final Dir2RecordsFilter dir2RecordsFilter = new Dir2RecordsFilter(this.classNameRegistryRepository);
		this.recordCounter = new Counter<IMonitoringRecord>();
		final Cache<IMonitoringRecord> cache = new Cache<IMonitoringRecord>();

		final StringBufferFilter<IMonitoringRecord> stringBufferFilter = new StringBufferFilter<IMonitoringRecord>();
		final InstanceOfFilter<IMonitoringRecord, IFlowRecord> instanceOfFilter = new InstanceOfFilter<IMonitoringRecord, IFlowRecord>(
				IFlowRecord.class);
		this.throughputFilter = new ElementThroughputMeasuringStage<IFlowRecord>();
		final TraceReconstructionFilter traceReconstructionFilter = new TraceReconstructionFilter(this.traceId2trace);
		Merger<TraceEventRecords> merger = new Merger<TraceEventRecords>();
		this.traceCounter = new Counter<TraceEventRecords>();
		final CollectorSink<TraceEventRecords> collector = new CollectorSink<TraceEventRecords>(this.elementCollection);

		// configure stages
		stringBufferFilter.getDataTypeHandlers().add(new IMonitoringRecordHandler());
		stringBufferFilter.getDataTypeHandlers().add(new StringHandler());

		// connect stages
		SingleElementPipe.connect(initialElementProducer.getOutputPort(), dir2RecordsFilter.getInputPort());
		SingleElementPipe.connect(dir2RecordsFilter.getOutputPort(), this.recordCounter.getInputPort());
		SingleElementPipe.connect(this.recordCounter.getOutputPort(), cache.getInputPort());
		SingleElementPipe.connect(cache.getOutputPort(), stringBufferFilter.getInputPort());
		SingleElementPipe.connect(stringBufferFilter.getOutputPort(), instanceOfFilter.getInputPort());
		SingleElementPipe.connect(instanceOfFilter.getOutputPort(), this.throughputFilter.getInputPort());
		SingleElementPipe.connect(this.throughputFilter.getOutputPort(), traceReconstructionFilter.getInputPort());
		// SingleElementPipe.connect(instanceOfFilter.getOutputPort(), traceReconstructionFilter.getInputPort());
		SingleElementPipe.connect(traceReconstructionFilter.getTraceValidOutputPort(), merger.getNewInputPort());
		SingleElementPipe.connect(traceReconstructionFilter.getTraceInvalidOutputPort(), merger.getNewInputPort());
		SingleElementPipe.connect(merger.getOutputPort(), this.traceCounter.getInputPort());
		SingleElementPipe.connect(this.traceCounter.getOutputPort(), collector.getInputPort());

		SpScPipe.connect(clockStage.getOutputPort(), this.throughputFilter.getTriggerInputPort(), 1);

		// create and configure pipeline
		HeadPipeline<InitialElementProducer<File>, CollectorSink<TraceEventRecords>> pipeline = new HeadPipeline<InitialElementProducer<File>, CollectorSink<TraceEventRecords>>();
		pipeline.setFirstStage(initialElementProducer);
		pipeline.setLastStage(collector);
		return pipeline;
	}

	public void start() {

		this.clockThread.start();
		this.workerThread.start();

		try {
			this.workerThread.join();
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}

		this.clockThread.interrupt();
		try {
			this.clockThread.join();
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

	public File getInputDir() {
		return this.inputDir;
	}

	public void setInputDir(final File inputDir) {
		this.inputDir = inputDir;
	}
}
