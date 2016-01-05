package teetime.examples.filecounter;

import java.io.File;

import teetime.framework.AbstractProducerStage;

class DirReader extends AbstractProducerStage<File> {

	private final File element;

	public DirReader(final File directory) {
		if (directory.isDirectory()) { // Check if it is a directory
			element = directory;
		} else {
			throw new IllegalArgumentException("Given file is not a directory.");
		}
	}

	@Override
	protected void execute() {
		visit(element);
		this.terminate(); // If everything is done, terminate
	}

	private void visit(final File element) {
		for (File file : element.listFiles()) {
			if (file.isDirectory()) {
				this.visit(file); // Visit recursively all dirs
			} else {
				outputPort.send(file); // Send file to next stage
			}
		}
	}

}
