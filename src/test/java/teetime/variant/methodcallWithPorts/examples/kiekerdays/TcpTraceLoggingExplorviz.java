package teetime.variant.methodcallWithPorts.examples.kiekerdays;

import teetime.variant.explicitScheduling.framework.core.Analysis;
import teetime.variant.methodcallWithPorts.framework.core.Pipeline;
import teetime.variant.methodcallWithPorts.framework.core.RunnableStage;
import teetime.variant.methodcallWithPorts.framework.core.StageWithPort;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SingleElementPipe;
import teetime.variant.methodcallWithPorts.stage.basic.Sink;
import teetime.variant.methodcallWithPorts.stage.explorviz.KiekerRecordTcpReader;

import kieker.common.record.IMonitoringRecord;

public class TcpTraceLoggingExplorviz extends Analysis {

	private Thread tcpThread;

	@Override
	public void init() {
		super.init();
		StageWithPort tcpPipeline = this.buildTcpPipeline();
		this.tcpThread = new Thread(new RunnableStage(tcpPipeline));
	}

	@Override
	public void start() {
		super.start();

		this.tcpThread.start();

		try {
			this.tcpThread.join();
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	private StageWithPort buildTcpPipeline() {
		KiekerRecordTcpReader tcpReader = new KiekerRecordTcpReader();
		Sink<IMonitoringRecord> endStage = new Sink<IMonitoringRecord>();

		SingleElementPipe.connect(tcpReader.getOutputPort(), endStage.getInputPort());

		// create and configure pipeline
		Pipeline<KiekerRecordTcpReader, Sink<IMonitoringRecord>> pipeline = new Pipeline<KiekerRecordTcpReader, Sink<IMonitoringRecord>>();
		pipeline.setFirstStage(tcpReader);
		pipeline.setLastStage(endStage);
		return tcpReader;
	}

	public static void main(final String[] args) {
		final TcpTraceLoggingExplorviz analysis = new TcpTraceLoggingExplorviz();

		analysis.init();
		try {
			analysis.start();
		} finally {
			analysis.onTerminate();
		}
	}

}
