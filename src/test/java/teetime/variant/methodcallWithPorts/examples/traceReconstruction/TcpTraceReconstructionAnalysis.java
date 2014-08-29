package teetime.variant.methodcallWithPorts.examples.traceReconstruction;

import java.util.LinkedList;
import java.util.List;

import teetime.util.concurrent.hashmap.ConcurrentHashMapWithDefault;
import teetime.util.concurrent.hashmap.TraceBuffer;
import teetime.variant.explicitScheduling.framework.core.Analysis;
import teetime.variant.methodcallWithPorts.framework.core.HeadPipeline;
import teetime.variant.methodcallWithPorts.framework.core.RunnableStage;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SingleElementPipe;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SpScPipe;
import teetime.variant.methodcallWithPorts.stage.Clock;
import teetime.variant.methodcallWithPorts.stage.Counter;
import teetime.variant.methodcallWithPorts.stage.ElementThroughputMeasuringStage;
import teetime.variant.methodcallWithPorts.stage.InstanceOfFilter;
import teetime.variant.methodcallWithPorts.stage.basic.Sink;
import teetime.variant.methodcallWithPorts.stage.basic.distributor.Distributor;
import teetime.variant.methodcallWithPorts.stage.io.TCPReader;
import teetime.variant.methodcallWithPorts.stage.kieker.traceReconstruction.TraceReconstructionFilter;

import kieker.analysis.plugin.filter.flow.TraceEventRecords;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.flow.IFlowRecord;

public class TcpTraceReconstructionAnalysis extends Analysis {

	private static final int MIO = 1000000;
	private static final int TCP_RELAY_MAX_SIZE = 2 * MIO;

	private final List<TraceEventRecords> elementCollection = new LinkedList<TraceEventRecords>();

	private Thread clockThread;
	private Thread clock2Thread;
	private Thread workerThread;

	private final ConcurrentHashMapWithDefault<Long, TraceBuffer> traceId2trace = new ConcurrentHashMapWithDefault<Long, TraceBuffer>(new TraceBuffer());

	private Counter<IMonitoringRecord> recordCounter;
	private Counter<TraceEventRecords> traceCounter;
	private ElementThroughputMeasuringStage<IFlowRecord> recordThroughputFilter;
	private ElementThroughputMeasuringStage<TraceEventRecords> traceThroughputFilter;

	@Override
	public void init() {
		super.init();
		HeadPipeline<Clock, Distributor<Long>> clockStage = this.buildClockPipeline(1000);
		this.clockThread = new Thread(new RunnableStage(clockStage));

		HeadPipeline<Clock, Distributor<Long>> clock2Stage = this.buildClockPipeline(2000);
		this.clock2Thread = new Thread(new RunnableStage(clock2Stage));

		HeadPipeline<?, ?> pipeline = this.buildPipeline(clockStage.getLastStage(), clock2Stage.getLastStage());
		this.workerThread = new Thread(new RunnableStage(pipeline));
	}

	private HeadPipeline<Clock, Distributor<Long>> buildClockPipeline(final long intervalDelayInMs) {
		Clock clock = new Clock();
		clock.setIntervalDelayInMs(intervalDelayInMs);
		Distributor<Long> distributor = new Distributor<Long>();

		SingleElementPipe.connect(clock.getOutputPort(), distributor.getInputPort());

		// create and configure pipeline
		HeadPipeline<Clock, Distributor<Long>> pipeline = new HeadPipeline<Clock, Distributor<Long>>();
		pipeline.setFirstStage(clock);
		pipeline.setLastStage(distributor);
		return pipeline;
	}

	private HeadPipeline<TCPReader, Sink<TraceEventRecords>> buildPipeline(final Distributor<Long> clockStage, final Distributor<Long> clock2Stage) {
		// create stages
		TCPReader tcpReader = new TCPReader();
		this.recordCounter = new Counter<IMonitoringRecord>();
		final InstanceOfFilter<IMonitoringRecord, IFlowRecord> instanceOfFilter = new InstanceOfFilter<IMonitoringRecord, IFlowRecord>(
				IFlowRecord.class);
		this.recordThroughputFilter = new ElementThroughputMeasuringStage<IFlowRecord>();
		final TraceReconstructionFilter traceReconstructionFilter = new TraceReconstructionFilter(this.traceId2trace);
		this.traceThroughputFilter = new ElementThroughputMeasuringStage<TraceEventRecords>();
		this.traceCounter = new Counter<TraceEventRecords>();
		Sink<TraceEventRecords> endStage = new Sink<TraceEventRecords>();

		// connect stages
		SpScPipe.connect(tcpReader.getOutputPort(), this.recordCounter.getInputPort(), TCP_RELAY_MAX_SIZE);
		SingleElementPipe.connect(this.recordCounter.getOutputPort(), instanceOfFilter.getInputPort());
		// SingleElementPipe.connect(instanceOfFilter.getOutputPort(), this.recordThroughputFilter.getInputPort());
		// SingleElementPipe.connect(this.recordThroughputFilter.getOutputPort(), traceReconstructionFilter.getInputPort());
		SingleElementPipe.connect(instanceOfFilter.getOutputPort(), traceReconstructionFilter.getInputPort());
		SingleElementPipe.connect(traceReconstructionFilter.getTraceValidOutputPort(), this.traceThroughputFilter.getInputPort());
		SingleElementPipe.connect(this.traceThroughputFilter.getOutputPort(), this.traceCounter.getInputPort());
		// SingleElementPipe.connect(traceReconstructionFilter.getTraceValidOutputPort(), this.traceCounter.getInputPort());
		SingleElementPipe.connect(this.traceCounter.getOutputPort(), endStage.getInputPort());

		SpScPipe.connect(clockStage.getNewOutputPort(), this.recordThroughputFilter.getTriggerInputPort(), 10);
		SpScPipe.connect(clock2Stage.getNewOutputPort(), this.traceThroughputFilter.getTriggerInputPort(), 10);

		// create and configure pipeline
		HeadPipeline<TCPReader, Sink<TraceEventRecords>> pipeline = new HeadPipeline<TCPReader, Sink<TraceEventRecords>>();
		pipeline.setFirstStage(tcpReader);
		pipeline.setLastStage(endStage);
		return pipeline;
	}

	@Override
	public void start() {
		super.start();

		this.workerThread.start();
		// this.clockThread.start();
		this.clock2Thread.start();

		try {
			this.workerThread.join();
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
		this.clockThread.interrupt();
		this.clock2Thread.interrupt();
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

	public List<Long> getRecordThroughputs() {
		return this.recordThroughputFilter.getThroughputs();
	}

	public List<Long> getTraceThroughputs() {
		return this.traceThroughputFilter.getThroughputs();
	}

}
