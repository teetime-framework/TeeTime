package teetime.framework;

import org.junit.Before;
import org.junit.Test;

public class AbstractConsumerStageTest {

	private ConsumerStage consumerStage;

	private static class ConsumerStage extends AbstractConsumerStage<Object> {
		@Override
		protected void execute(final Object element) {
			// do nothing
		}
	}

	@Before
	public void before() {
		consumerStage = new ConsumerStage();
	}

	@Test(expected = IllegalStateException.class)
	public void recognizeSecondInputPort() {
		consumerStage.createInputPort();
	}

	@Test(expected = IllegalStateException.class)
	public void recognizeSecondInputPortWithType() {
		consumerStage.createInputPort(Object.class);
	}

	@Test(expected = IllegalStateException.class)
	public void recognizeSecondInputPortWithName() {
		consumerStage.createInputPort("Second input port");
	}

	@Test(expected = IllegalStateException.class)
	public void recognizeSecondInputPortWithTypeAndName() {
		consumerStage.createInputPort(Object.class, "Second input port");
	}
}
