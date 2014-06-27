package teetime.variant.methodcallWithPorts.examples.traceReconstruction;

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
import teetime.variant.methodcallWithPorts.stage.EndStage;
import teetime.variant.methodcallWithPorts.stage.InstanceOfFilter;
import teetime.variant.methodcallWithPorts.stage.ThroughputFilter;
import teetime.variant.methodcallWithPorts.stage.io.TCPReader;
import teetime.variant.methodcallWithPorts.stage.kieker.className.ClassNameRegistryRepository;
import teetime.variant.methodcallWithPorts.stage.kieker.traceReconstruction.TraceReconstructionFilter;

import kieker.analysis.plugin.filter.flow.TraceEventRecords;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.flow.IFlowRecord;

public class TcpTraceReconstructionAnalysis extends Analysis {

	private final List<TraceEventRecords> elementCollection = new LinkedList<TraceEventRecords>();

	private Thread clockThread;
	private Thread clock2Thread;
	private Thread workerThread;

	private ClassNameRegistryRepository classNameRegistryRepository;

	private CountingFilter<IMonitoringRecord> recordCounter;

	private CountingFilter<TraceEventRecords> traceCounter;

	private ThroughputFilter<IFlowRecord> recordThroughputFilter;
	private ThroughputFilter<TraceEventRecords> traceThroughputFilter;

	@Override
	public void init() {
		super.init();
		StageWithPort<Void, Long> clockStage = this.buildClockPipeline();
		this.clockThread = new Thread(new RunnableStage(clockStage));

		StageWithPort<Void, Long> clock2Stage = this.buildClock2Pipeline();
		this.clock2Thread = new Thread(new RunnableStage(clock2Stage));

		Pipeline<?, ?> pipeline = this.buildPipeline(clockStage, clock2Stage);
		this.workerThread = new Thread(new RunnableStage(pipeline));
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

	private Pipeline<Void, TraceEventRecords> buildPipeline(final StageWithPort<Void, Long> clockStage, final StageWithPort<Void, Long> clock2Stage) {
		this.classNameRegistryRepository = new ClassNameRegistryRepository();

		// create stages
		TCPReader tcpReader = new TCPReader();
		this.recordCounter = new CountingFilter<IMonitoringRecord>();
		final InstanceOfFilter<IMonitoringRecord, IFlowRecord> instanceOfFilter = new InstanceOfFilter<IMonitoringRecord, IFlowRecord>(
				IFlowRecord.class);
		this.recordThroughputFilter = new ThroughputFilter<IFlowRecord>();
		final TraceReconstructionFilter traceReconstructionFilter = new TraceReconstructionFilter();
		this.traceThroughputFilter = new ThroughputFilter<TraceEventRecords>();
		this.traceCounter = new CountingFilter<TraceEventRecords>();
		EndStage<TraceEventRecords> endStage = new EndStage<TraceEventRecords>();

		// connect stages
		SpScPipe.connect(tcpReader.getOutputPort(), this.recordCounter.getInputPort(), 1024);
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
		Pipeline<Void, TraceEventRecords> pipeline = new Pipeline<Void, TraceEventRecords>();
		pipeline.setFirstStage(tcpReader);
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

		this.clockThread.start();
		this.clock2Thread.start();
		this.workerThread.start();

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

	public List<Long> getTraceThroughputFilter() {
		return this.traceThroughputFilter.getThroughputs();
	}

}
