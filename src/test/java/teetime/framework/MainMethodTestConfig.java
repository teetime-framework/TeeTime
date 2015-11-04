package teetime.framework;

import teetime.stage.InitialElementProducer;

public class MainMethodTestConfig extends Configuration {

	public static boolean executed = false;

	public MainMethodTestConfig() {
		connectPorts(new InitialElementProducer<Object>(new Object()).getOutputPort(), new StaticSetter().getInputPort());
	}

	private class StaticSetter extends AbstractConsumerStage<Object> {

		@Override
		protected void execute(final Object element) {
			executed = true;
		}

	}

}
