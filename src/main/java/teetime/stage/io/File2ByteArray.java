package teetime.stage.io;

import java.io.File;
import java.io.IOException;

import teetime.framework.ConsumerStage;
import teetime.framework.HeadStage;
import teetime.framework.OutputPort;

import com.google.common.io.Files;

public class File2ByteArray extends ConsumerStage<File> implements HeadStage {

	private final OutputPort<byte[]> outputPort = this.createOutputPort();

	@Override
	protected void execute(final File element) {
		try {
			byte[] cache = Files.toByteArray(element);
			this.send(outputPort, cache);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public OutputPort<? extends byte[]> getOutputPort() {
		return outputPort;
	}

	@Override
	public boolean shouldBeTerminated() {
		return false;
	}

	@Override
	public void terminate() {

	}
}
