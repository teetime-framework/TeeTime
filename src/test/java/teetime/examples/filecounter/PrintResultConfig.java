package teetime.examples.filecounter;

import java.io.File;

import teetime.framework.Configuration;
import teetime.framework.Execution;

public class PrintResultConfig extends Configuration {

	public PrintResultConfig(final File dir) {
		DirReader dirReader = new DirReader(dir);
		FileCounter fileCounter = new FileCounter();
		PrintResult printResult = new PrintResult();

		// Connect stages
		connectPorts(dirReader.getOutputPort(), fileCounter.getInputPort());
		connectPorts(fileCounter.getOutputPort(), printResult.getInputPort());
	}

	public static void main(final String[] args) {
		Execution<PrintResultConfig> execution = new Execution<PrintResultConfig>(new PrintResultConfig(new File(".")));
		execution.executeBlocking();
	}
}
