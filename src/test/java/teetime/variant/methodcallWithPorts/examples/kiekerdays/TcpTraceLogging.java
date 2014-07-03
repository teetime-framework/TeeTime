package teetime.variant.methodcallWithPorts.examples.kiekerdays;

import java.util.LinkedList;
import java.util.List;

import teetime.variant.explicitScheduling.framework.core.Analysis;
import teetime.variant.methodcallWithPorts.framework.core.Pipeline;
import teetime.variant.methodcallWithPorts.framework.core.RunnableStage;
import teetime.variant.methodcallWithPorts.framework.core.StageWithPort;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SingleElementPipe;
import teetime.variant.methodcallWithPorts.stage.EndStage;

import kieker.analysis.plugin.filter.flow.TraceEventRecords;
import kieker.common.record.IMonitoringRecord;

public class TcpTraceLogging extends Analysis {

	private final List<TraceEventRecords> elementCollection = new LinkedList<TraceEventRecords>();

	private Thread tcpThread;

	private int numWorkerThreads;

	@Override
	public void init() {
		super.init();
		StageWithPort<Void, IMonitoringRecord> tcpPipeline = this.buildTcpPipeline();
		this.tcpThread = new Thread(new RunnableStage(tcpPipeline));
	}

	private StageWithPort<Void, IMonitoringRecord> buildTcpPipeline() {
		TCPReaderSink tcpReader = new TCPReaderSink();
		EndStage<IMonitoringRecord> endStage = new EndStage<IMonitoringRecord>();

		SingleElementPipe.connect(tcpReader.getOutputPort(), endStage.getInputPort());

		// create and configure pipeline
		Pipeline<Void, IMonitoringRecord> pipeline = new Pipeline<Void, IMonitoringRecord>();
		pipeline.setFirstStage(tcpReader);
		pipeline.setLastStage(endStage);
		return pipeline;
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
		final TcpTraceLogging analysis = new TcpTraceLogging();
		analysis.setNumWorkerThreads(1);

		analysis.init();
		try {
			analysis.start();
		} finally {
			analysis.onTerminate();
		}
	}

}
