package teetime.variant.methodcallWithPorts.examples.traceReconstructionWithThreads;

import java.util.LinkedList;
import java.util.List;

import teetime.variant.explicitScheduling.framework.core.Analysis;
import teetime.variant.methodcallWithPorts.framework.core.Pipeline;
import teetime.variant.methodcallWithPorts.framework.core.RunnableStage;
import teetime.variant.methodcallWithPorts.framework.core.StageWithPort;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SingleElementPipe;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SpScPipe;
import teetime.variant.methodcallWithPorts.stage.Clock;
import teetime.variant.methodcallWithPorts.stage.CountingFilter;
import teetime.variant.methodcallWithPorts.stage.Distributor;
import teetime.variant.methodcallWithPorts.stage.EndStage;
import teetime.variant.methodcallWithPorts.stage.InstanceOfFilter;
import teetime.variant.methodcallWithPorts.stage.Relay;
import teetime.variant.methodcallWithPorts.stage.ThroughputFilter;
import teetime.variant.methodcallWithPorts.stage.io.TCPReader;
import teetime.variant.methodcallWithPorts.stage.kieker.traceReconstruction.TraceReconstructionFilter;

import kieker.analysis.plugin.filter.flow.TraceEventRecords;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.flow.IFlowRecord;

public class TcpTraceReconstructionAnalysisWithThreads extends Analysis {

	private static final int NUM_VIRTUAL_CORES = Runtime.getRuntime().availableProcessors();
	private static final int TCP_RELAY_MAX_SIZE = 500000;

	private final List<TraceEventRecords> elementCollection = new LinkedList<TraceEventRecords>();

	private Thread tcpThread;
	private Thread clockThread;
	private Thread clock2Thread;
	private Thread[] workerThreads;

	private CountingFilter<IMonitoringRecord> recordCounter;

	private CountingFilter<TraceEventRecords> traceCounter;

	private ThroughputFilter<IFlowRecord> recordThroughputFilter;
	private ThroughputFilter<TraceEventRecords> traceThroughputFilter;

	private SpScPipe<IMonitoringRecord> tcpRelayPipe;
	private int numWorkerThreads;

	@Override
	public void init() {
		super.init();
		StageWithPort<Void, IMonitoringRecord> tcpPipeline = this.buildTcpPipeline();
		this.tcpThread = new Thread(new RunnableStage(tcpPipeline));

		StageWithPort<Void, Long> clockStage = this.buildClockPipeline(1000);
		this.clockThread = new Thread(new RunnableStage(clockStage));

		StageWithPort<Void, Long> clock2Stage = this.buildClockPipeline(2000);
		this.clock2Thread = new Thread(new RunnableStage(clock2Stage));

		this.numWorkerThreads = Math.min(NUM_VIRTUAL_CORES, this.numWorkerThreads);
		this.workerThreads = new Thread[this.numWorkerThreads];

		for (int i = 0; i < this.workerThreads.length; i++) {
			StageWithPort<?, ?> pipeline = this.buildPipeline(tcpPipeline, clockStage, clock2Stage);
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
		clock.setIntervalDelayInMs(intervalDelayInMs);
		Distributor<Long> distributor = new Distributor<Long>();

		SingleElementPipe.connect(clock.getOutputPort(), distributor.getInputPort());

		// create and configure pipeline
		Pipeline<Void, Long> pipeline = new Pipeline<Void, Long>();
		pipeline.setFirstStage(clock);
		pipeline.setLastStage(distributor);
		return pipeline;
	}

	private Pipeline<IMonitoringRecord, ?> buildPipeline(final StageWithPort<Void, IMonitoringRecord> tcpReaderPipeline,
			final StageWithPort<Void, Long> clockStage,
			final StageWithPort<Void, Long> clock2Stage) {
		// create stages
		Relay<IMonitoringRecord> relay = new Relay<IMonitoringRecord>();
		this.recordCounter = new CountingFilter<IMonitoringRecord>();
		final InstanceOfFilter<IMonitoringRecord, IFlowRecord> instanceOfFilter = new InstanceOfFilter<IMonitoringRecord, IFlowRecord>(
				IFlowRecord.class);
		this.recordThroughputFilter = new ThroughputFilter<IFlowRecord>();
		final TraceReconstructionFilter traceReconstructionFilter = new TraceReconstructionFilter();
		this.traceThroughputFilter = new ThroughputFilter<TraceEventRecords>();
		this.traceCounter = new CountingFilter<TraceEventRecords>();
		// EndStage<TraceEventRecords> endStage = new EndStage<TraceEventRecords>();
		EndStage<IMonitoringRecord> endStage = new EndStage<IMonitoringRecord>();

		// connect stages
		this.tcpRelayPipe = SpScPipe.connect(tcpReaderPipeline.getOutputPort(), relay.getInputPort(), TCP_RELAY_MAX_SIZE);

		SysOutFilter<IMonitoringRecord> sysout = new SysOutFilter<IMonitoringRecord>(this.tcpRelayPipe);

		// // SingleElementPipe.connect(relay.getOutputPort(), this.recordCounter.getInputPort());
		// // SingleElementPipe.connect(this.recordCounter.getOutputPort(), instanceOfFilter.getInputPort());
		// SingleElementPipe.connect(relay.getOutputPort(), instanceOfFilter.getInputPort());
		// SingleElementPipe.connect(relay.getOutputPort(), sysout.getInputPort());
		// SingleElementPipe.connect(sysout.getOutputPort(), endStage.getInputPort());
		SingleElementPipe.connect(relay.getOutputPort(), endStage.getInputPort());
		// // SingleElementPipe.connect(instanceOfFilter.getOutputPort(), this.recordThroughputFilter.getInputPort());
		// // SingleElementPipe.connect(this.recordThroughputFilter.getOutputPort(), traceReconstructionFilter.getInputPort());
		// SingleElementPipe.connect(instanceOfFilter.getOutputPort(), traceReconstructionFilter.getInputPort());
		// // SingleElementPipe.connect(traceReconstructionFilter.getOutputPort(), this.traceThroughputFilter.getInputPort());
		// // SingleElementPipe.connect(this.traceThroughputFilter.getOutputPort(), this.traceCounter.getInputPort());
		// // SingleElementPipe.connect(this.traceCounter.getOutputPort(), endStage.getInputPort());
		// SingleElementPipe.connect(traceReconstructionFilter.getOutputPort(), endStage.getInputPort());

		SpScPipe.connect(clockStage.getOutputPort(), sysout.getTriggerInputPort(), 10);
		// SpScPipe.connect(clockStage.getOutputPort(), this.recordThroughputFilter.getTriggerInputPort(), 10);
		SpScPipe.connect(clock2Stage.getOutputPort(), this.traceThroughputFilter.getTriggerInputPort(), 10);

		// create and configure pipeline
		// Pipeline<IMonitoringRecord, TraceEventRecords> pipeline = new Pipeline<IMonitoringRecord, TraceEventRecords>();
		Pipeline<IMonitoringRecord, IMonitoringRecord> pipeline = new Pipeline<IMonitoringRecord, IMonitoringRecord>();
		pipeline.setFirstStage(relay);
		// pipeline.addIntermediateStage(this.recordCounter);
		pipeline.addIntermediateStage(sysout);
		// pipeline.addIntermediateStage(instanceOfFilter);
		// pipeline.addIntermediateStage(this.recordThroughputFilter);
		// pipeline.addIntermediateStage(traceReconstructionFilter);
		// pipeline.addIntermediateStage(this.traceThroughputFilter);
		// pipeline.addIntermediateStage(this.traceCounter);
		pipeline.setLastStage(endStage);
		return pipeline;
	}

	@Override
	public void start() {
		super.start();

		this.tcpThread.start();
		// this.clockThread.start();
		// this.clock2Thread.start();

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

	public SpScPipe<IMonitoringRecord> getTcpRelayPipe() {
		return this.tcpRelayPipe;
	}

	public int getNumWorkerThreads() {
		return this.numWorkerThreads;
	}

	public void setNumWorkerThreads(final int numWorkerThreads) {
		this.numWorkerThreads = numWorkerThreads;
	}

}
