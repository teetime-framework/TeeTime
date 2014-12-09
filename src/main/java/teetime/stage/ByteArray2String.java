package teetime.stage;

import java.nio.charset.Charset;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

public class ByteArray2String extends AbstractConsumerStage<byte[]> {

	private final OutputPort<String> outputPort = this.createOutputPort();

	@Override
	protected void execute(final byte[] element) {
		outputPort.send(new String(element, Charset.forName("UTF-8")));
	}

	public OutputPort<? extends String> getOutputPort() {
		return this.outputPort;
	}
}
