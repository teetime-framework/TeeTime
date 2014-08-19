package teetime.variant.methodcallWithPorts.examples.kiekerdays;

import teetime.variant.explicitScheduling.framework.core.Analysis;
import teetime.variant.methodcallWithPorts.framework.core.RunnableStage;
import teetime.variant.methodcallWithPorts.framework.core.StageWithPort;
import teetime.variant.methodcallWithPorts.stage.io.TCPReader;

public class TcpTraceLogging extends Analysis {

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
		// TCPReaderSink tcpReader = new TCPReaderSink();
		TCPReader tcpReader = new TCPReader();

		return tcpReader;
	}

	public static void main(final String[] args) {
		final TcpTraceLogging analysis = new TcpTraceLogging();

		analysis.init();
		try {
			analysis.start();
		} finally {
			analysis.onTerminate();
		}
	}

}
