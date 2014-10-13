package teetime.stage;

import java.util.StringTokenizer;

import teetime.framework.ConsumerStage;
import teetime.framework.OutputPort;

public class Tokenizer extends ConsumerStage<String> {

	private final OutputPort<String> outputPort = this.createOutputPort();
	private final String regex;

	public Tokenizer(final String regex) {
		this.regex = regex;
	}

	@Override
	protected void execute(final String element) {
		StringTokenizer st = new StringTokenizer(element, regex);
		while (st.hasMoreTokens()) {
			this.send(outputPort, st.nextToken());
		}
	}

	public OutputPort<? extends String> getOutputPort() {
		return outputPort;
	}

}
