package teetime.variant.methodcallWithPorts.examples.traceReading;

import java.util.List;

import teetime.variant.explicitScheduling.framework.core.Analysis;
import teetime.variant.methodcallWithPorts.framework.core.Pipeline;
import teetime.variant.methodcallWithPorts.framework.core.RunnableStage;
import teetime.variant.methodcallWithPorts.framework.core.StageWithPort;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SingleElementPipe;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SpScPipe;
import teetime.variant.methodcallWithPorts.stage.Clock;
import teetime.variant.methodcallWithPorts.stage.Counter;
import teetime.variant.methodcallWithPorts.stage.ElementThroughputMeasuringStage;
import teetime.variant.methodcallWithPorts.stage.EndStage;
import teetime.variant.methodcallWithPorts.stage.basic.distributor.Distributor;
import teetime.variant.methodcallWithPorts.stage.io.TCPReader;

import kieker.common.record.IMonitoringRecord;

public class TcpTraceLoggingExtAnalysis extends Analysis {

	private static final int MIO = 1000000;
	private static final int TCP_RELAY_MAX_SIZE = 2 * MIO;

	private Thread clockThread;
	private Thread tcpThread;

	private Counter<IMonitoringRecord> recordCounter;
	private ElementThroughputMeasuringStage<IMonitoringRecord> recordThroughputStage;

	private StageWithPort<Void, Long> buildClockPipeline(final long intervalDelayInMs) {
		Clock clockStage = new Clock();
		clockStage.setInitialDelayInMs(intervalDelayInMs);
		clockStage.setIntervalDelayInMs(intervalDelayInMs);
		Distributor<Long> distributor = new Distributor<Long>();

		SingleElementPipe.connect(clockStage.getOutputPort(), distributor.getInputPort());

		// create and configure pipeline
		Pipeline<Void, Long> pipeline = new Pipeline<Void, Long>();
		pipeline.setFirstStage(clockStage);
		pipeline.setLastStage(distributor);
		return pipeline;
	}

	private StageWithPort<Void, IMonitoringRecord> buildTcpPipeline(final StageWithPort<Void, Long> clockPipeline) {
		TCPReader tcpReader = new TCPReader();
		this.recordCounter = new Counter<IMonitoringRecord>();
		this.recordThroughputStage = new ElementThroughputMeasuringStage<IMonitoringRecord>();
		EndStage<IMonitoringRecord> endStage = new EndStage<IMonitoringRecord>();

		SingleElementPipe.connect(tcpReader.getOutputPort(), this.recordCounter.getInputPort());
		SingleElementPipe.connect(this.recordCounter.getOutputPort(), this.recordThroughputStage.getInputPort());
		SingleElementPipe.connect(this.recordThroughputStage.getOutputPort(), endStage.getInputPort());

		SpScPipe.connect(clockPipeline.getOutputPort(), this.recordThroughputStage.getTriggerInputPort(), TCP_RELAY_MAX_SIZE);

		// create and configure pipeline
		Pipeline<Void, IMonitoringRecord> pipeline = new Pipeline<Void, IMonitoringRecord>();
		pipeline.setFirstStage(tcpReader);
		pipeline.addIntermediateStage(this.recordCounter);
		pipeline.addIntermediateStage(this.recordThroughputStage);
		pipeline.setLastStage(endStage);
		return pipeline;
	}

	@Override
	public void init() {
		super.init();

		StageWithPort<Void, Long> clockPipeline = this.buildClockPipeline(1000);
		this.clockThread = new Thread(new RunnableStage<Void>(clockPipeline));

		StageWithPort<Void, IMonitoringRecord> tcpPipeline = this.buildTcpPipeline(clockPipeline);
		this.tcpThread = new Thread(new RunnableStage<Void>(tcpPipeline));
	}

	@Override
	public void start() {
		super.start();

		this.tcpThread.start();
		this.clockThread.start();

		try {
			this.tcpThread.join();
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}

		this.clockThread.interrupt();
	}

	public int getNumRecords() {
		return this.recordCounter.getNumElementsPassed();
	}

	public List<Long> getRecordThroughputs() {
		return this.recordThroughputStage.getThroughputs();
	}

}
