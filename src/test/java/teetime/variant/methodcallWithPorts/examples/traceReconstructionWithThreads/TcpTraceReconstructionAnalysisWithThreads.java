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

	private static final int TCP_RELAY_MAX_SIZE = 500000;

	private final List<TraceEventRecords> elementCollection = new LinkedList<TraceEventRecords>();

	private Thread tcpThread;
	private Thread clockThread;
	private Thread clock2Thread;
	private Thread workerThread;

	private CountingFilter<IMonitoringRecord> recordCounter;

	private CountingFilter<TraceEventRecords> traceCounter;

	private ThroughputFilter<IFlowRecord> recordThroughputFilter;
	private ThroughputFilter<TraceEventRecords> traceThroughputFilter;

	private SpScPipe<IMonitoringRecord> tcpRelayPipe;

	@Override
	public void init() {
		super.init();
		StageWithPort<Void, IMonitoringRecord> tcpPipeline = this.buildTcpPipeline();
		this.tcpThread = new Thread(new RunnableStage(tcpPipeline));

		StageWithPort<Void, Long> clockStage = this.buildClockPipeline();
		this.clockThread = new Thread(new RunnableStage(clockStage));

		StageWithPort<Void, Long> clock2Stage = this.buildClock2Pipeline();
		this.clock2Thread = new Thread(new RunnableStage(clock2Stage));

		StageWithPort<?, ?> pipeline = this.buildPipeline(tcpPipeline, clockStage, clock2Stage);
		this.workerThread = new Thread(new RunnableStage(pipeline));

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

	private StageWithPort<Void, Long> buildClockPipeline() {
		Clock clock = new Clock();
		clock.setIntervalDelayInMs(1000);

		return clock;
	}

	private StageWithPort<Void, Long> buildClock2Pipeline() {
		Clock clock = new Clock();
		clock.setIntervalDelayInMs(2000);

		return clock;
	}

	private Pipeline<IMonitoringRecord, TraceEventRecords> buildPipeline(final StageWithPort<Void, IMonitoringRecord> tcpReaderPipeline,
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
		EndStage<TraceEventRecords> endStage = new EndStage<TraceEventRecords>();

		// connect stages
		this.tcpRelayPipe = SpScPipe.connect(tcpReaderPipeline.getOutputPort(), relay.getInputPort(), TCP_RELAY_MAX_SIZE);
		SingleElementPipe.connect(relay.getOutputPort(), this.recordCounter.getInputPort());
		SingleElementPipe.connect(this.recordCounter.getOutputPort(), instanceOfFilter.getInputPort());
		// SingleElementPipe.connect(instanceOfFilter.getOutputPort(), this.recordThroughputFilter.getInputPort());
		// SingleElementPipe.connect(this.recordThroughputFilter.getOutputPort(), traceReconstructionFilter.getInputPort());
		SingleElementPipe.connect(instanceOfFilter.getOutputPort(), traceReconstructionFilter.getInputPort());
		SingleElementPipe.connect(traceReconstructionFilter.getOutputPort(), this.traceThroughputFilter.getInputPort());
		SingleElementPipe.connect(this.traceThroughputFilter.getOutputPort(), this.traceCounter.getInputPort());
		SingleElementPipe.connect(this.traceCounter.getOutputPort(), endStage.getInputPort());

		SpScPipe.connect(clockStage.getOutputPort(), this.recordThroughputFilter.getTriggerInputPort(), 1);
		SpScPipe.connect(clock2Stage.getOutputPort(), this.traceThroughputFilter.getTriggerInputPort(), 1);

		// create and configure pipeline
		Pipeline<IMonitoringRecord, TraceEventRecords> pipeline = new Pipeline<IMonitoringRecord, TraceEventRecords>();
		pipeline.setFirstStage(relay);
		pipeline.addIntermediateStage(this.recordCounter);
		pipeline.addIntermediateStage(instanceOfFilter);
		// pipeline.addIntermediateStage(this.recordThroughputFilter);
		pipeline.addIntermediateStage(traceReconstructionFilter);
		pipeline.addIntermediateStage(this.traceThroughputFilter);
		pipeline.addIntermediateStage(this.traceCounter);
		pipeline.setLastStage(endStage);
		return pipeline;
	}

	@Override
	public void start() {
		super.start();

		this.tcpThread.start();
		// this.clockThread.start();
		this.clock2Thread.start();
		this.workerThread.start();

		try {
			this.tcpThread.join();
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}

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

	public SpScPipe<IMonitoringRecord> getTcpRelayPipe() {
		return this.tcpRelayPipe;
	}

}
