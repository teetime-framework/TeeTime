package teetime.stage.string;

import java.util.StringTokenizer;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

public final class Tokenizer extends AbstractConsumerStage<String> {

	private final OutputPort<String> outputPort = this.createOutputPort();
	private final String regex;

	public Tokenizer(final String regex) {
		this.regex = regex;
	}

	@Override
	protected void execute(final String element) {
		StringTokenizer st = new StringTokenizer(element, this.regex);
		while (st.hasMoreTokens()) {
			outputPort.send(st.nextToken());
		}
	}

	public OutputPort<? extends String> getOutputPort() {
		return this.outputPort;
	}

}
