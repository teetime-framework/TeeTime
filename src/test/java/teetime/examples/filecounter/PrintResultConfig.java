package teetime.examples.filecounter;

import java.io.File;

import teetime.framework.Configuration;
import teetime.framework.Execution;

public class PrintResultConfig extends Configuration {

	public PrintResultConfig(final File dir) {
		DirReader dirReader = new DirReader(dir);
		FileCounter fileCounter = new FileCounter();
		ResultPrinter resultPrinter = new ResultPrinter();

		// Connect stages
		connectPorts(dirReader.getOutputPort(), fileCounter.getInputPort());
		connectPorts(fileCounter.getOutputPort(), resultPrinter.getInputPort());
	}

	public static void main(final String[] args) {
		PrintResultConfig config = new PrintResultConfig(new File("."));
		Execution<PrintResultConfig> execution = new Execution<PrintResultConfig>(config);
		execution.executeBlocking();
	}
}
