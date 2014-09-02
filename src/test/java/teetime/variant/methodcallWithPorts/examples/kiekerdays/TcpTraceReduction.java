package teetime.variant.methodcallWithPorts.examples.kiekerdays;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import teetime.util.concurrent.hashmap.ConcurrentHashMapWithDefault;
import teetime.util.concurrent.hashmap.TraceBuffer;
import teetime.variant.explicitScheduling.framework.core.Analysis;
import teetime.variant.methodcallWithPorts.framework.core.HeadPipeline;
import teetime.variant.methodcallWithPorts.framework.core.HeadStage;
import teetime.variant.methodcallWithPorts.framework.core.RunnableStage;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SingleElementPipe;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SpScPipe;
import teetime.variant.methodcallWithPorts.stage.Clock;
import teetime.variant.methodcallWithPorts.stage.InstanceOfFilter;
import teetime.variant.methodcallWithPorts.stage.Relay;
import teetime.variant.methodcallWithPorts.stage.basic.Sink;
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
	private static final int MIO = 1000000;
	private static final int TCP_RELAY_MAX_SIZE = 2 * MIO;

	private final List<TraceEventRecords> elementCollection = new LinkedList<TraceEventRecords>();
	private final ConcurrentHashMapWithDefault<Long, TraceBuffer> traceId2trace = new ConcurrentHashMapWithDefault<Long, TraceBuffer>(new TraceBuffer());
	private final Map<TraceEventRecords, TraceAggregationBuffer> trace2buffer = new TreeMap<TraceEventRecords, TraceAggregationBuffer>(new TraceComperator());
	private final List<SpScPipe> tcpRelayPipes = new ArrayList<SpScPipe>();

	private Thread tcpThread;
	private Thread clockThread;
	private Thread[] workerThreads;

	private int numWorkerThreads;

	@Override
	public void init() {
		super.init();
		HeadPipeline<TCPReader, Distributor<IMonitoringRecord>> tcpPipeline = this.buildTcpPipeline();
		this.tcpThread = new Thread(new RunnableStage(tcpPipeline));

		HeadPipeline<Clock, Distributor<Long>> clockStage = this.buildClockPipeline(5000);
		this.clockThread = new Thread(new RunnableStage(clockStage));

		this.numWorkerThreads = Math.min(NUM_VIRTUAL_CORES, this.numWorkerThreads);
		this.workerThreads = new Thread[this.numWorkerThreads];

		for (int i = 0; i < this.workerThreads.length; i++) {
			HeadStage pipeline = this.buildPipeline(tcpPipeline.getLastStage(), clockStage.getLastStage());
			this.workerThreads[i] = new Thread(new RunnableStage(pipeline));
		}
	}

	private HeadPipeline<TCPReader, Distributor<IMonitoringRecord>> buildTcpPipeline() {
		TCPReader tcpReader = new TCPReader();
		Distributor<IMonitoringRecord> distributor = new Distributor<IMonitoringRecord>();

		SingleElementPipe.connect(tcpReader.getOutputPort(), distributor.getInputPort());

		// create and configure pipeline
		HeadPipeline<TCPReader, Distributor<IMonitoringRecord>> pipeline = new HeadPipeline<TCPReader, Distributor<IMonitoringRecord>>();
		pipeline.setFirstStage(tcpReader);
		pipeline.setLastStage(distributor);
		return pipeline;
	}

	private HeadPipeline<Clock, Distributor<Long>> buildClockPipeline(final long intervalDelayInMs) {
		Clock clock = new Clock();
		clock.setInitialDelayInMs(intervalDelayInMs);
		clock.setIntervalDelayInMs(intervalDelayInMs);
		Distributor<Long> distributor = new Distributor<Long>();

		SingleElementPipe.connect(clock.getOutputPort(), distributor.getInputPort());

		// create and configure pipeline
		HeadPipeline<Clock, Distributor<Long>> pipeline = new HeadPipeline<Clock, Distributor<Long>>();
		pipeline.setFirstStage(clock);
		pipeline.setLastStage(distributor);
		return pipeline;
	}

	private HeadStage buildPipeline(final Distributor<IMonitoringRecord> tcpReaderPipeline, final Distributor<Long> clockStage) {
		// create stages
		Relay<IMonitoringRecord> relay = new Relay<IMonitoringRecord>();
		final InstanceOfFilter<IMonitoringRecord, IFlowRecord> instanceOfFilter = new InstanceOfFilter<IMonitoringRecord, IFlowRecord>(
				IFlowRecord.class);
		final TraceReconstructionFilter traceReconstructionFilter = new TraceReconstructionFilter(this.traceId2trace);
		TraceReductionFilter traceReductionFilter = new TraceReductionFilter(this.trace2buffer);
		Sink<TraceEventRecords> endStage = new Sink<TraceEventRecords>();

		// connect stages
		SpScPipe tcpRelayPipe = SpScPipe.connect(tcpReaderPipeline.getNewOutputPort(), relay.getInputPort(), TCP_RELAY_MAX_SIZE);
		this.tcpRelayPipes.add(tcpRelayPipe);

		SingleElementPipe.connect(relay.getOutputPort(), instanceOfFilter.getInputPort());
		SingleElementPipe.connect(instanceOfFilter.getOutputPort(), traceReconstructionFilter.getInputPort());
		SingleElementPipe.connect(traceReconstructionFilter.getTraceValidOutputPort(), traceReductionFilter.getInputPort());
		SingleElementPipe.connect(traceReductionFilter.getOutputPort(), endStage.getInputPort());

		SpScPipe.connect(clockStage.getNewOutputPort(), traceReductionFilter.getTriggerInputPort(), 10);

		// create and configure pipeline
		HeadPipeline<Relay<IMonitoringRecord>, Sink<TraceEventRecords>> pipeline = new HeadPipeline<Relay<IMonitoringRecord>, Sink<TraceEventRecords>>();
		pipeline.setFirstStage(relay);
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

	@Override
	public void onTerminate() {
		int maxNumWaits = 0;
		for (SpScPipe pipe : this.tcpRelayPipes) {
			maxNumWaits = Math.max(maxNumWaits, pipe.getNumWaits());
		}
		System.out.println("max #waits of TcpRelayPipes: " + maxNumWaits);
		super.onTerminate();
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
