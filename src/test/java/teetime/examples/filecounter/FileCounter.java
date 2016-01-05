package teetime.examples.filecounter;

import java.io.File;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

class FileCounter extends AbstractConsumerStage<File> {

	private final OutputPort<Integer> outputPort = createOutputPort();

	private int counter = 0;

	@Override
	protected void execute(final File element) {
		counter++; // Increment for every incoming file
	}

	@Override
	public void onTerminating() throws Exception {
		outputPort.send(counter);
		super.onTerminating();
	}

	public OutputPort<Integer> getOutputPort() {
		return outputPort;
	}
}
