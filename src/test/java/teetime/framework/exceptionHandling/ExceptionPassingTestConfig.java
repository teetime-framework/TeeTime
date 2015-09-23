package teetime.framework.exceptionHandling;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.Configuration;
import teetime.stage.InitialElementProducer;

public class ExceptionPassingTestConfig extends Configuration {

	public ExceptionPassingTestConfig() {
		connectPorts(new InitialElementProducer<Object>(new Object()).getOutputPort(), new ExceptionStage().getInputPort());
	}

	private class ExceptionStage extends AbstractConsumerStage<Object> {

		@Override
		protected void execute(final Object element) {
			throw new IllegalStateException("Correct exception");
		}
	}

}
