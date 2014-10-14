package teetime.stage;

import teetime.framework.ConsumerStage;
import teetime.framework.OutputPort;

public class ByteArray2String extends ConsumerStage<byte[]> {

	private final OutputPort<String> outputPort = this.createOutputPort();

	@Override
	protected void execute(final byte[] element) {
		this.send(this.outputPort, new String(element));
	}

	public OutputPort<? extends String> getOutputPort() {
		return this.outputPort;
	}
}
