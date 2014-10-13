package teetime.stage;

import teetime.framework.ConsumerStage;

public class TokenCounter extends ConsumerStage<String> {

	private long i = 0;

	public long getI() {
		return i;
	}

	@Override
	protected void execute(final String element) {
		i++;
	}

}
