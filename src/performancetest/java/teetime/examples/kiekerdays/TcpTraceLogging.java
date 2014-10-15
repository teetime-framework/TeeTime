package teetime.examples.kiekerdays;

import teetime.framework.HeadStage;
import teetime.framework.RunnableStage;
import teetime.stage.io.TCPReader;

public class TcpTraceLogging {

	private Thread tcpThread;

	public void init() {
		HeadStage tcpPipeline = this.buildTcpPipeline();
		this.tcpThread = new Thread(new RunnableStage(tcpPipeline));
	}

	public void start() {

		this.tcpThread.start();

		try {
			this.tcpThread.join();
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	private HeadStage buildTcpPipeline() {
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
		}
	}

}
