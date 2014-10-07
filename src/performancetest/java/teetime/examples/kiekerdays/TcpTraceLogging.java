package teetime.examples.kiekerdays;

import teetime.framework.HeadStage;
import teetime.framework.OldAnalysis;
import teetime.framework.RunnableStage;
import teetime.stage.io.TCPReader;

public class TcpTraceLogging extends OldAnalysis {

	private Thread tcpThread;

	@Override
	public void init() {
		super.init();
		HeadStage tcpPipeline = this.buildTcpPipeline();
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
			analysis.onTerminate();
		}
	}

}
