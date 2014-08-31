package teetime.variant.methodcallWithPorts.examples.traceReading;

import java.util.List;

import teetime.variant.explicitScheduling.framework.core.Analysis;
import teetime.variant.methodcallWithPorts.framework.core.HeadPipeline;
import teetime.variant.methodcallWithPorts.framework.core.RunnableStage;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SingleElementPipe;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SpScPipe;
import teetime.variant.methodcallWithPorts.stage.Clock;
import teetime.variant.methodcallWithPorts.stage.Counter;
import teetime.variant.methodcallWithPorts.stage.ElementThroughputMeasuringStage;
import teetime.variant.methodcallWithPorts.stage.basic.Sink;
import teetime.variant.methodcallWithPorts.stage.basic.distributor.Distributor;
import teetime.variant.methodcallWithPorts.stage.io.TCPReader;

import kieker.common.record.IMonitoringRecord;

public class TcpTraceLoggingExtAnalysis extends Analysis {

	private Thread clockThread;
	private Thread tcpThread;

	private Counter<IMonitoringRecord> recordCounter;
	private ElementThroughputMeasuringStage<IMonitoringRecord> recordThroughputStage;

	private HeadPipeline<Clock, Distributor<Long>> buildClockPipeline(final long intervalDelayInMs) {
		Clock clockStage = new Clock();
		clockStage.setInitialDelayInMs(intervalDelayInMs);
		clockStage.setIntervalDelayInMs(intervalDelayInMs);
		Distributor<Long> distributor = new Distributor<Long>();

		SingleElementPipe.connect(clockStage.getOutputPort(), distributor.getInputPort());

		// create and configure pipeline
		HeadPipeline<Clock, Distributor<Long>> pipeline = new HeadPipeline<Clock, Distributor<Long>>();
		pipeline.setFirstStage(clockStage);
		pipeline.setLastStage(distributor);
		return pipeline;
	}

	private HeadPipeline<?, ?> buildTcpPipeline(final Distributor<Long> previousClockStage) {
		TCPReader tcpReader = new TCPReader();
		this.recordCounter = new Counter<IMonitoringRecord>();
		this.recordThroughputStage = new ElementThroughputMeasuringStage<IMonitoringRecord>();
		Sink<IMonitoringRecord> endStage = new Sink<IMonitoringRecord>();

		SingleElementPipe.connect(tcpReader.getOutputPort(), this.recordCounter.getInputPort());
		SingleElementPipe.connect(this.recordCounter.getOutputPort(), this.recordThroughputStage.getInputPort());
		SingleElementPipe.connect(this.recordThroughputStage.getOutputPort(), endStage.getInputPort());
		// SingleElementPipe.connect(this.recordCounter.getOutputPort(), endStage.getInputPort());

		SpScPipe.connect(previousClockStage.getNewOutputPort(), this.recordThroughputStage.getTriggerInputPort(), 10);

		// create and configure pipeline
		HeadPipeline<TCPReader, Sink<IMonitoringRecord>> pipeline = new HeadPipeline<TCPReader, Sink<IMonitoringRecord>>();
		pipeline.setFirstStage(tcpReader);
		pipeline.setLastStage(endStage);
		return pipeline;
	}

	@Override
	public void init() {
		super.init();

		HeadPipeline<Clock, Distributor<Long>> clockPipeline = this.buildClockPipeline(1000);
		this.clockThread = new Thread(new RunnableStage(clockPipeline));

		HeadPipeline<?, ?> tcpPipeline = this.buildTcpPipeline(clockPipeline.getLastStage());
		this.tcpThread = new Thread(new RunnableStage(tcpPipeline));
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
