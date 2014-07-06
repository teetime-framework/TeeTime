package teetime.variant.methodcallWithPorts.examples.kiekerdays;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import teetime.util.concurrent.hashmap.ConcurrentHashMapWithDefault;
import teetime.util.concurrent.hashmap.TraceBuffer;
import teetime.variant.explicitScheduling.framework.core.Analysis;
import teetime.variant.methodcallWithPorts.framework.core.Pipeline;
import teetime.variant.methodcallWithPorts.framework.core.RunnableStage;
import teetime.variant.methodcallWithPorts.framework.core.StageWithPort;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SingleElementPipe;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SpScPipe;
import teetime.variant.methodcallWithPorts.stage.Clock;
import teetime.variant.methodcallWithPorts.stage.EndStage;
import teetime.variant.methodcallWithPorts.stage.InstanceOfFilter;
import teetime.variant.methodcallWithPorts.stage.Relay;
import teetime.variant.methodcallWithPorts.stage.basic.distributor.Distributor;
import teetime.variant.methodcallWithPorts.stage.io.TCPReader;
import teetime.variant.methodcallWithPorts.stage.kieker.traceReconstruction.TraceReconstructionFilter;
import teetime.variant.methodcallWithPorts.stage.kieker.traceReduction.TraceAggregationBuffer;
import teetime.variant.methodcallWithPorts.stage.kieker.traceReduction.TraceComperator;
import teetime.variant.methodcallWithPorts.stage.kieker.traceReduction.TraceReductionFilter;

import kieker.analysis.plugin.filter.flow.TraceEventRecords;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.flow.IFlowRecord;

public class TcpTraceReduction extends Analysis {

	private static final int NUM_VIRTUAL_CORES = Runtime.getRuntime().availableProcessors();
	private static final int TCP_RELAY_MAX_SIZE = 500000;

	private final List<TraceEventRecords> elementCollection = new LinkedList<TraceEventRecords>();

	private Thread tcpThread;
	private Thread clockThread;
	private Thread[] workerThreads;

	private int numWorkerThreads;

	@Override
	public void init() {
		super.init();
		StageWithPort<Void, IMonitoringRecord> tcpPipeline = this.buildTcpPipeline();
		this.tcpThread = new Thread(new RunnableStage(tcpPipeline));

		StageWithPort<Void, Long> clockStage = this.buildClockPipeline(5000);
		this.clockThread = new Thread(new RunnableStage(clockStage));

		this.numWorkerThreads = Math.min(NUM_VIRTUAL_CORES, this.numWorkerThreads);
		this.workerThreads = new Thread[this.numWorkerThreads];

		for (int i = 0; i < this.workerThreads.length; i++) {
			StageWithPort<?, ?> pipeline = this.buildPipeline(tcpPipeline, clockStage);
			this.workerThreads[i] = new Thread(new RunnableStage(pipeline));
		}
	}

	private StageWithPort<Void, IMonitoringRecord> buildTcpPipeline() {
		TCPReader tcpReader = new TCPReader();
		Distributor<IMonitoringRecord> distributor = new Distributor<IMonitoringRecord>();

		SingleElementPipe.connect(tcpReader.getOutputPort(), distributor.getInputPort());

		// create and configure pipeline
		Pipeline<Void, IMonitoringRecord> pipeline = new Pipeline<Void, IMonitoringRecord>();
		pipeline.setFirstStage(tcpReader);
		pipeline.setLastStage(distributor);
		return pipeline;
	}

	private StageWithPort<Void, Long> buildClockPipeline(final long intervalDelayInMs) {
		Clock clock = new Clock();
		clock.setInitialDelayInMs(intervalDelayInMs);
		clock.setIntervalDelayInMs(intervalDelayInMs);
		Distributor<Long> distributor = new Distributor<Long>();

		SingleElementPipe.connect(clock.getOutputPort(), distributor.getInputPort());

		// create and configure pipeline
		Pipeline<Void, Long> pipeline = new Pipeline<Void, Long>();
		pipeline.setFirstStage(clock);
		pipeline.setLastStage(distributor);
		return pipeline;
	}

	private final Map<Long, TraceBuffer> traceId2trace = new ConcurrentHashMapWithDefault<Long, TraceBuffer>(new TraceBuffer());
	private final Map<TraceEventRecords, TraceAggregationBuffer> trace2buffer = new TreeMap<TraceEventRecords, TraceAggregationBuffer>(new TraceComperator());

	private Pipeline<IMonitoringRecord, ?> buildPipeline(final StageWithPort<Void, IMonitoringRecord> tcpReaderPipeline,
			final StageWithPort<Void, Long> clockStage) {
		// create stages
		Relay<IMonitoringRecord> relay = new Relay<IMonitoringRecord>();
		final InstanceOfFilter<IMonitoringRecord, IFlowRecord> instanceOfFilter = new InstanceOfFilter<IMonitoringRecord, IFlowRecord>(
				IFlowRecord.class);
		final TraceReconstructionFilter traceReconstructionFilter = new TraceReconstructionFilter(this.traceId2trace);
		TraceReductionFilter traceReductionFilter = new TraceReductionFilter(this.trace2buffer);
		EndStage<TraceEventRecords> endStage = new EndStage<TraceEventRecords>();

		// connect stages
		SpScPipe.connect(tcpReaderPipeline.getOutputPort(), relay.getInputPort(), TCP_RELAY_MAX_SIZE);

		SingleElementPipe.connect(relay.getOutputPort(), instanceOfFilter.getInputPort());
		SingleElementPipe.connect(instanceOfFilter.getOutputPort(), traceReconstructionFilter.getInputPort());
		SingleElementPipe.connect(traceReconstructionFilter.getOutputPort(), traceReductionFilter.getInputPort());
		SingleElementPipe.connect(traceReductionFilter.getOutputPort(), endStage.getInputPort());

		SpScPipe.connect(clockStage.getOutputPort(), traceReductionFilter.getTriggerInputPort(), 10);

		// create and configure pipeline
		Pipeline<IMonitoringRecord, TraceEventRecords> pipeline = new Pipeline<IMonitoringRecord, TraceEventRecords>();
		pipeline.setFirstStage(relay);
		pipeline.addIntermediateStage(instanceOfFilter);
		pipeline.addIntermediateStage(traceReconstructionFilter);
		pipeline.addIntermediateStage(traceReductionFilter);
		pipeline.setLastStage(endStage);
		return pipeline;
	}

	@Override
	public void start() {
		super.start();

		this.tcpThread.start();
		this.clockThread.start();

		for (Thread workerThread : this.workerThreads) {
			workerThread.start();
		}

		try {
			this.tcpThread.join();

			for (Thread workerThread : this.workerThreads) {
				workerThread.join();
			}
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
		this.clockThread.interrupt();
	}

	public List<TraceEventRecords> getElementCollection() {
		return this.elementCollection;
	}

	public int getNumWorkerThreads() {
		return this.numWorkerThreads;
	}

	public void setNumWorkerThreads(final int numWorkerThreads) {
		this.numWorkerThreads = numWorkerThreads;
	}

	public static void main(final String[] args) {
		int numWorkerThreads = Integer.valueOf(args[0]);

		final TcpTraceReduction analysis = new TcpTraceReduction();
		analysis.setNumWorkerThreads(numWorkerThreads);

		analysis.init();
		try {
			analysis.start();
		} finally {
			analysis.onTerminate();
		}
	}

}
